package duanspringboot.service;

import org.springframework.stereotype.Service;

import duanspringboot.dto.Application.ApplicationDetailResponse;
import duanspringboot.dto.Application.ApplicationRequest;
import duanspringboot.dto.Application.ApplicationResponse;
import duanspringboot.dto.Application.ApplicationStatusRequest;
import duanspringboot.dto.Application.PipelineData;
import duanspringboot.entity.Application;
import duanspringboot.entity.CandidateExperience;
import duanspringboot.entity.CandidateProfile;
import duanspringboot.entity.CandidateSkill;
import duanspringboot.entity.JobPosting;
import duanspringboot.enums.ApplicationStatus;
import duanspringboot.repository.ApplicationRepository;
import duanspringboot.repository.CandidateExperienceRepository;
import duanspringboot.repository.CandidateProfileRepository;
import duanspringboot.repository.CandidateSkillRepository;
import duanspringboot.repository.InterviewRepository;
import duanspringboot.repository.JobPostingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final JobPostingRepository jobPostingRepository;
    private final CandidateProfileRepository profileRepository;
    private final CandidateSkillRepository skillRepository;
    private final CandidateExperienceRepository experienceRepository;
    private final InterviewRepository interviewRepository;
    private final EmailService emailService;

    // 1. Ứng viên: Nộp đơn ứng tuyển
    public ApplicationResponse apply(ApplicationRequest request, Long userId) {
        CandidateProfile profile = profileRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Vui lòng cập nhật hồ sơ ứng viên trước khi ứng tuyển"));

        JobPosting job = jobPostingRepository.findById(request.getJobId())
                .orElseThrow(() -> new RuntimeException("Công việc không tồn tại"));

        // Kiểm tra xem đã ứng tuyển vào job này chưa
        if (applicationRepository.existsByCandidateIdAndJobPostingId(profile.getId(), job.getId())) {
            throw new RuntimeException("Bạn đã ứng tuyển vào công việc này rồi");
        }

        Application application = Application.builder()
                .candidate(profile)
                .jobPosting(job)
                .coverLetter(request.getCoverLetter())
                .status(ApplicationStatus.APPLIED) // Trạng thái ban đầu
                .build();

        return mapToResponse(applicationRepository.save(application));
    }

    // 2. Nhà tuyển dụng: Cập nhật trạng thái đơn (Pipeline: Screen, Interview, Offer,...)
    public ApplicationResponse changeStatus(Long applicationId, ApplicationStatusRequest request, Long userId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Đơn ứng tuyển không tồn tại"));

        // Kiểm tra quyền: Đảm bảo người cập nhật là chủ sở hữu tin tuyển dụng này
        if (!application.getJobPosting().getCompany().getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền quản lý đơn ứng tuyển này");
        }

        ApplicationStatus oldStatus = application.getStatus();
        application.setStatus(request.getStatus());
        Application savedApplication = applicationRepository.save(application);

        if (oldStatus != ApplicationStatus.OFFERED && savedApplication.getStatus() == ApplicationStatus.OFFERED) {
            sendApplicationApprovedEmail(savedApplication);
        }

        return mapToResponse(savedApplication);
    }

    private void sendApplicationApprovedEmail(Application application) {
        try {
            emailService.sendApplicationApprovedEmail(
                    application.getCandidate().getUser().getEmail(),
                    application.getCandidate().getFullName(),
                    application.getJobPosting().getTitle(),
                    application.getJobPosting().getCompany().getName()
            );
        } catch (Exception ex) {
            log.warn("Could not send application approved email for application {}", application.getId(), ex);
        }
    }

    // 3. Ứng viên: Xem danh sách các việc đã ứng tuyển
    public List<ApplicationResponse> getMyApplications(Long userId) {
        CandidateProfile profile = profileRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ ứng viên"));

        return applicationRepository.findByCandidateId(profile.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 4. Nhà tuyển dụng: Xem danh sách ứng viên của một tin tuyển dụng cụ thể
    public List<ApplicationResponse> getApplicationsByJob(Long jobId, Long userId) {
        JobPosting job = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tin tuyển dụng"));

        if (!job.getCompany().getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền xem danh sách này");
        }

        return applicationRepository.findByJobPostingId(jobId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 5. Pipeline: Lấy dữ liệu pipeline cho recruiter
    public PipelineData getPipelineData(Long userId) {
        return getPipelineData(userId, null, null);
    }

    public PipelineData getPipelineData(Long userId, Long jobId, String searchQuery) {
        List<Application> allApps = applicationRepository.findByRecruiterUserId(userId);
        
        Set<Long> scheduledAppIds = interviewRepository.findApplicationIdsWithInterviewByRecruiter(userId);

        if (jobId != null) {
            allApps = allApps.stream()
                    .filter(a -> a.getJobPosting().getId().equals(jobId))
                    .collect(Collectors.toList());
        }

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            String query = searchQuery.toLowerCase().trim();
            allApps = allApps.stream()
                    .filter(a -> a.getCandidate().getFullName().toLowerCase().contains(query)
                            || a.getCandidate().getUser().getEmail().toLowerCase().contains(query))
                    .collect(Collectors.toList());
        }

        List<ApplicationResponse> pendingApps = allApps.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.APPLIED)
                .map(a -> mapToResponse(a, scheduledAppIds)).collect(Collectors.toList());

        List<ApplicationResponse> reviewingApps = allApps.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.SHORTLISTED)
                .map(a -> mapToResponse(a, scheduledAppIds)).collect(Collectors.toList());

        List<ApplicationResponse> interviewingApps = allApps.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.INTERVIEWING)
                .map(a -> mapToResponse(a, scheduledAppIds)).collect(Collectors.toList());

        List<ApplicationResponse> finalApps = allApps.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.OFFERED || a.getStatus() == ApplicationStatus.REJECTED)
                .map(a -> mapToResponse(a, scheduledAppIds)).collect(Collectors.toList());

        Map<String, Integer> counts = new LinkedHashMap<>();
        counts.put("pending", pendingApps.size());
        counts.put("reviewing", reviewingApps.size());
        counts.put("interviewing", interviewingApps.size());
        counts.put("final", finalApps.size());

        return PipelineData.builder()
                .pendingApps(pendingApps)
                .reviewingApps(reviewingApps)
                .interviewingApps(interviewingApps)
                .finalApps(finalApps)
                .counts(counts)
                .build();
    }

    // 6. Lấy danh sách ứng viên có thể được mời phỏng vấn (chưa có lịch)
    public List<ApplicationResponse> getApplicationsAvailableForInterview(Long userId) {
        // Lấy tất cả application của recruiter
        List<Application> allApps = applicationRepository.findByRecruiterUserId(userId);

        // Lấy danh sách application IDs đã có lịch phỏng vấn
        Set<Long> scheduledAppIds = interviewRepository.findApplicationIdsWithInterviewByRecruiter(userId);

        // Lọc: chỉ lấy APPLIED hoặc SHORTLISTED và chưa có lịch
        return allApps.stream()
                .filter(app -> !scheduledAppIds.contains(app.getId()))
                .filter(app -> app.getStatus() == ApplicationStatus.APPLIED
                        || app.getStatus() == ApplicationStatus.SHORTLISTED)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 7. Pipeline: Lấy chi tiết ứng viên (Quick View Panel)
    public ApplicationDetailResponse getApplicationDetail(Long applicationId, Long userId) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Đơn ứng tuyển không tồn tại"));

        if (!app.getJobPosting().getCompany().getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền xem chi tiết này");
        }

        CandidateProfile candidate = app.getCandidate();
        Set<Long> scheduledAppIds = interviewRepository.findApplicationIdsWithInterviewByRecruiter(userId);
        int daysSinceApplied = (int) java.time.temporal.ChronoUnit.DAYS.between(app.getAppliedAt().toLocalDate(), java.time.LocalDate.now());

        List<ApplicationDetailResponse.SkillDto> skills = candidate.getSkills() != null ?
                candidate.getSkills().stream()
                        .map(s -> ApplicationDetailResponse.SkillDto.builder()
                                .id(s.getId())
                                .skillName(s.getSkillName())
                                .build())
                        .collect(Collectors.toList()) : List.of();

        List<ApplicationDetailResponse.ExperienceDto> experiences = candidate.getExperiences() != null ?
                candidate.getExperiences().stream()
                        .map(e -> ApplicationDetailResponse.ExperienceDto.builder()
                                .id(e.getId())
                                .companyName(e.getCompanyName())
                                .position(e.getPosition())
                                .startDate(e.getStartDate())
                                .endDate(e.getEndDate())
                                .build())
                        .collect(Collectors.toList()) : List.of();

        return ApplicationDetailResponse.builder()
                .id(app.getId())
                .candidateId(candidate.getId())
                .candidateName(candidate.getFullName())
                .candidateEmail(candidate.getUser().getEmail())
                .candidatePhone(candidate.getPhone())
                .candidateHeadline(candidate.getHeadline())
                .candidateAvatarUrl(candidate.getAvatarUrl())
                .candidateAddress(candidate.getAddress())
                .candidateSummary(candidate.getSummary())
                .jobId(app.getJobPosting().getId())
                .jobTitle(app.getJobPosting().getTitle())
                .companyName(app.getJobPosting().getCompany().getName())
                .coverLetter(app.getCoverLetter())
                .resumeUrl(candidate.getResumeUrl())
                .status(app.getStatus().name())
                .appliedAt(app.getAppliedAt())
                .hasInterview(scheduledAppIds.contains(app.getId()))
                .daysSinceApplied(daysSinceApplied)
                .skills(skills)
                .experiences(experiences)
                .build();
    }

    private ApplicationResponse mapToResponse(Application app) {
        return mapToResponse(app, Set.of());
    }

    private ApplicationResponse mapToResponse(Application app, Set<Long> scheduledAppIds) {
        int daysSinceApplied = (int) java.time.temporal.ChronoUnit.DAYS.between(app.getAppliedAt().toLocalDate(), java.time.LocalDate.now());
        
        return ApplicationResponse.builder()
                .id(app.getId())
                .candidateId(app.getCandidate().getId())
                .candidateName(app.getCandidate().getFullName())
                .candidateEmail(app.getCandidate().getUser().getEmail())
                .candidatePhone(app.getCandidate().getPhone())
                .jobId(app.getJobPosting().getId())
                .jobTitle(app.getJobPosting().getTitle())
                .companyName(app.getJobPosting().getCompany().getName())
                .coverLetter(app.getCoverLetter())
                .resumeUrl(app.getCandidate().getResumeUrl())
                .status(app.getStatus())
                .appliedAt(app.getAppliedAt())
                .hasInterview(scheduledAppIds.contains(app.getId()))
                .daysSinceApplied(daysSinceApplied)
                .build();
    }
}
