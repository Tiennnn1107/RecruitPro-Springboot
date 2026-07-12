package duanspringboot.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import duanspringboot.dto.job.JobPostingRequest;
import duanspringboot.dto.job.JobPostingResponse;
import duanspringboot.entity.Company;
import duanspringboot.entity.JobPosting;
import duanspringboot.entity.JobSkill;
import duanspringboot.enums.JobStatus;
import duanspringboot.repository.CompanyRepository;
import duanspringboot.repository.JobPostingRepository;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class JobPostingService {

    private final JobPostingRepository jobPostingRepository;
    private final CompanyRepository companyRepository;
    private final JobAlertService jobAlertService;

    // 0. Lấy theo ID
    public JobPostingResponse getById(Long id) {
        JobPosting job = jobPostingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tin tuyển dụng"));
        return mapToResponse(job);
    }

    // 1. Đăng tin mới
    @Transactional
    public JobPostingResponse create(JobPostingRequest request, Long userId) {
        Company company = companyRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Bạn cần tạo hồ sơ công ty trước khi đăng tin"));

        JobPosting job = JobPosting.builder()
                .company(company)
                .title(request.getTitle())
                .description(request.getDescription())
                .requirements(request.getRequirements())
                .salaryMin(request.getSalaryMin())
                .salaryMax(request.getSalaryMax())
                .location(request.getLocation())
                .jobType(request.getJobType())
                .status(JobStatus.OPEN)
                .expiredAt(request.getExpiredAt())
                .build();

        JobPosting savedJob = jobPostingRepository.save(job);

        // Gửi thông báo cho các job alerts phù hợp
        jobAlertService.checkAndNotifyNewJob(savedJob);

        // Xử lý skills
        if (request.getSkills() != null && !request.getSkills().isEmpty()) {
            List<JobSkill> jobSkills = request.getSkills().stream()
                    .map(skillName -> JobSkill.builder()
                            .jobPosting(savedJob)
                            .skillName(skillName)
                            .build())
                    .collect(Collectors.toList());
            savedJob.setSkills(jobSkills);
        }

        return mapToResponse(jobPostingRepository.save(savedJob));
    }

    // 2. Cập nhật tin tuyển dụng
    @Transactional
    public JobPostingResponse update(Long id, JobPostingRequest request, Long userId) {
        JobPosting job = jobPostingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tin tuyển dụng"));

        if (!job.getCompany().getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền chỉnh sửa tin này");
        }

        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setRequirements(request.getRequirements());
        job.setSalaryMin(request.getSalaryMin());
        job.setSalaryMax(request.getSalaryMax());
        job.setLocation(request.getLocation());
        job.setJobType(request.getJobType());
        job.setExpiredAt(request.getExpiredAt());

        // Xử lý skills: Xóa cũ, thêm mới
        if (job.getSkills() != null) {
            job.getSkills().clear();
        } else {
            job.setSkills(new ArrayList<>());
        }
        
        if (request.getSkills() != null && !request.getSkills().isEmpty()) {
            List<JobSkill> jobSkills = request.getSkills().stream()
                    .map(skillName -> JobSkill.builder()
                            .jobPosting(job)
                            .skillName(skillName)
                            .build())
                    .collect(Collectors.toList());
            job.getSkills().addAll(jobSkills);
        }

        return mapToResponse(jobPostingRepository.save(job));
    }

    // 3. Xóa tin tuyển dụng
    public void delete(Long id, Long userId) {
        JobPosting job = jobPostingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tin tuyển dụng"));

        if (!job.getCompany().getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền xóa tin này");
        }

        jobPostingRepository.delete(job);
    }

    // 4. Lấy danh sách tin đang mở với bộ lọc (Filter)
    public List<JobPostingResponse> searchJobs(String location, Integer minSalary, String title, Long fieldId) {
        return jobPostingRepository.filterJobs(JobStatus.OPEN, location, minSalary, title, fieldId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 5. Lấy danh sách tin của chính công ty (Dành cho Recruiter quản lý)
    public List<JobPostingResponse> getMyCompanyJobs(Long userId) {
        Company company = companyRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ công ty"));
        
        return jobPostingRepository.findByCompanyId(company.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 5.1. Lấy danh sách tin đang mở của một công ty (Công khai)
    public List<JobPostingResponse> getPublicJobsByCompanyId(Long companyId) {
        return jobPostingRepository.findByCompanyIdAndStatus(companyId, JobStatus.OPEN)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 6. Đóng/Mở tin tuyển dụng nhanh
    public JobPostingResponse changeStatus(Long id, JobStatus status, Long userId) {
        JobPosting job = jobPostingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tin tuyển dụng"));

        if (!job.getCompany().getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền thực hiện hành động này");
        }

        job.setStatus(status);
        return mapToResponse(jobPostingRepository.save(job));
    }

    // 7. Lấy danh sách tin với phân trang (cho recruiter)
    public Map<String, Object> getMyCompanyJobsPaginated(Long userId, int page, int size) {
        Company company = companyRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ công ty"));
        
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<JobPosting> jobPage = jobPostingRepository.findByCompanyId(company.getId(), pageable);
        
        List<JobPostingResponse> jobs = jobPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("jobs", jobs);
        result.put("currentPage", jobPage.getNumber() + 1);
        result.put("totalPages", jobPage.getTotalPages());
        result.put("totalItems", jobPage.getTotalElements());
        result.put("hasNext", jobPage.hasNext());
        result.put("hasPrevious", jobPage.hasPrevious());
        
        return result;
    }

    // 8. Tìm kiếm với phân trang
    public Map<String, Object> searchJobsPaginated(String location, Integer minSalary, String title, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<JobPosting> jobPage = jobPostingRepository.filterJobsPaginated(
                JobStatus.OPEN, location, minSalary, title, null, pageable);
        
        List<JobPostingResponse> jobs = jobPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("jobs", jobs);
        result.put("currentPage", jobPage.getNumber() + 1);
        result.put("totalPages", jobPage.getTotalPages());
        result.put("totalItems", jobPage.getTotalElements());
        result.put("hasNext", jobPage.hasNext());
        result.put("hasPrevious", jobPage.hasPrevious());
        
        return result;
    }

    // 9. Lấy tất cả tin đang mở với phân trang (cho trang chủ)
    public Map<String, Object> getAllOpenJobsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<JobPosting> jobPage = jobPostingRepository.findByStatus(JobStatus.OPEN, pageable);
        
        List<JobPostingResponse> jobs = jobPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("jobs", jobs);
        result.put("currentPage", jobPage.getNumber() + 1);
        result.put("totalPages", jobPage.getTotalPages());
        result.put("totalItems", jobPage.getTotalElements());
        result.put("hasNext", jobPage.hasNext());
        result.put("hasPrevious", jobPage.hasPrevious());
        
        return result;
    }

    private JobPostingResponse mapToResponse(JobPosting job) {
        List<String> skills = (job.getSkills() != null) 
            ? job.getSkills().stream().map(JobSkill::getSkillName).collect(Collectors.toList())
            : new ArrayList<>();

        String salaryRange = (job.getSalaryMin() != null && job.getSalaryMax() != null)
                ? job.getSalaryMin() + " - " + job.getSalaryMax()
                : "Thỏa thuận";

        return JobPostingResponse.builder()
                .id(job.getId())
                .companyId(job.getCompany().getId())
                .recruiterUserId(job.getCompany().getUser().getId())
                .recruiterEmail(job.getCompany().getUser().getEmail())
                .title(job.getTitle())
                .companyName(job.getCompany().getName())
                .description(job.getDescription())
                .requirements(job.getRequirements())
                .location(job.getLocation())
                .salaryRange(salaryRange)
                .salaryMin(job.getSalaryMin())
                .salaryMax(job.getSalaryMax())
                .jobType(job.getJobType())
                .status(job.getStatus())
                .expiredAt(job.getExpiredAt())
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .applicationCount(job.getApplications() != null ? job.getApplications().size() : 0)
                .requiredSkills(skills)
                .jobFieldName(job.getJobField() != null ? job.getJobField().getName() : null)
                .build();
    }

    public List<JobPostingResponse> getJobsByRecruiterUserId(Long userId) {
        return jobPostingRepository.findByRecruiterUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
}
