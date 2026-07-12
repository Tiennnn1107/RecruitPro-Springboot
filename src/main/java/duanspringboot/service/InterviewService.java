package duanspringboot.service;

import duanspringboot.dto.interview.InterviewRequest;
import duanspringboot.dto.interview.InterviewResponse;
import duanspringboot.entity.Application;
import duanspringboot.entity.Interview;
import duanspringboot.enums.InterviewResult;
import duanspringboot.repository.ApplicationRepository;
import duanspringboot.repository.InterviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final ApplicationRepository applicationRepository;
    private final EmailService emailService;

    // 1. Tạo lịch phỏng vấn và gửi email
    @Transactional
    public InterviewResponse scheduleInterview(InterviewRequest request, Long recruiterId) {
        Application application = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new RuntimeException("Đơn ứng tuyển không tồn tại"));

        // Kiểm tra quyền (chỉ chủ sở hữu tin mới được mời phỏng vấn)
        if (!application.getJobPosting().getCompany().getUser().getId().equals(recruiterId)) {
            throw new RuntimeException("Bạn không có quyền lên lịch cho ứng viên này");
        }

        Interview interview = Interview.builder()
                .application(application)
                .scheduledAt(request.getScheduledAt())
                .type(request.getType())
                .locationOrLink(request.getLocationOrLink())
                .interviewerNote(request.getInterviewerNote())
                .result(InterviewResult.PENDING)
                .build();

        Interview savedInterview = interviewRepository.save(interview);

        // Gửi thông báo email
        String timeStr = savedInterview.getScheduledAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        emailService.sendInterviewInvitation(
                application.getCandidate().getUser().getEmail(),
                application.getCandidate().getFullName(),
                application.getJobPosting().getTitle(),
                timeStr,
                savedInterview.getLocationOrLink()
        );

        return mapToResponse(savedInterview);
    }

    // 2. Lấy danh sách phỏng vấn của Recruiter
    public List<InterviewResponse> getMyInterviews(Long recruiterId) {
        return interviewRepository.findByApplicationJobPostingCompanyUserId(recruiterId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 3. Cập nhật kết quả phỏng vấn
    public InterviewResponse updateResult(Long id, InterviewResult result, String note, Long recruiterId) {
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cuộc phỏng vấn"));

        if (!interview.getApplication().getJobPosting().getCompany().getUser().getId().equals(recruiterId)) {
            throw new RuntimeException("Bạn không có quyền cập nhật kết quả này");
        }

        interview.setResult(result);
        if (note != null) interview.setInterviewerNote(note);

        return mapToResponse(interviewRepository.save(interview));
    }

    // 4. Lấy chi tiết một cuộc phỏng vấn
    public InterviewResponse getInterviewById(Long id, Long recruiterId) {
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cuộc phỏng vấn"));

        if (!interview.getApplication().getJobPosting().getCompany().getUser().getId().equals(recruiterId)) {
            throw new RuntimeException("Bạn không có quyền xem cuộc phỏng vấn này");
        }

        return mapToResponse(interview);
    }

    // 5. Xóa/Hủy một cuộc phỏng vấn
    @Transactional
    public void deleteInterview(Long id, Long recruiterId) {
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cuộc phỏng vấn"));

        if (!interview.getApplication().getJobPosting().getCompany().getUser().getId().equals(recruiterId)) {
            throw new RuntimeException("Bạn không có quyền hủy cuộc phỏng vấn này");
        }

        interviewRepository.delete(interview);
    }

    private InterviewResponse mapToResponse(Interview interview) {
        Application app = interview.getApplication();
        return InterviewResponse.builder()
                .id(interview.getId())
                .applicationId(app.getId())
                .candidateName(app.getCandidate().getFullName())
                .candidateEmail(app.getCandidate().getUser().getEmail())
                .jobTitle(app.getJobPosting().getTitle())
                .scheduledAt(interview.getScheduledAt())
                .type(interview.getType())
                .locationOrLink(interview.getLocationOrLink())
                .interviewerNote(interview.getInterviewerNote())
                .result(interview.getResult())
                .build();
    }
}
