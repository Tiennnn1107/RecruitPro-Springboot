package duanspringboot.service;

import duanspringboot.dto.admin.AdminDashboardResponse;
import duanspringboot.dto.admin.CompanyResponse;
import duanspringboot.dto.admin.JobPostingResponse;
import duanspringboot.dto.admin.UserResponse;
import duanspringboot.entity.Company;
import duanspringboot.entity.JobPosting;
import duanspringboot.entity.User;
import duanspringboot.enums.JobStatus;
import duanspringboot.enums.Role;
import duanspringboot.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final JobPostingRepository jobPostingRepository;
    private final ApplicationRepository applicationRepository;
    private final InterviewRepository interviewRepository;
    private final BlogRepository blogRepository;

    public AdminDashboardResponse getDashboardStats() {
        List<User> allUsers = userRepository.findAll();

        long totalCandidates = allUsers.stream()
                .filter(u -> u.getRole() == Role.CANDIDATE)
                .count();

        long totalRecruiters = allUsers.stream()
                .filter(u -> u.getRole() == Role.RECRUITER)
                .count();

        return AdminDashboardResponse.builder()
                .totalUsers(allUsers.size())
                .totalCandidates(totalCandidates)
                .totalRecruiters(totalRecruiters)
                .totalCompanies(companyRepository.count())
                .totalJobPostings(jobPostingRepository.count())
                .totalApplications(applicationRepository.count())
                .totalInterviews(interviewRepository.count())
                .totalBlogs(blogRepository.count())
                .build();
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        return mapToUserResponse(user);
    }

    @Transactional
    public UserResponse toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        Boolean currentStatus = user.getIsActive();
        user.setIsActive(currentStatus == null ? true : !currentStatus);
        User savedUser = userRepository.saveAndFlush(user);
        return mapToUserResponse(savedUser);
    }

    @Transactional
    public UserResponse updateUserRole(Long id, String role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        if (user.getRole() == Role.ADMIN) {
            long adminCount = userRepository.countByRole(Role.ADMIN);
            if (adminCount <= 1) {
                throw new RuntimeException("Không thể thay đổi vai trò của Admin cuối cùng");
            }
        }

        try {
            Role newRole = Role.valueOf(role.toUpperCase());
            user.setRole(newRole);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Vai trò không hợp lệ: " + role);
        }

        User savedUser = userRepository.saveAndFlush(user);
        return mapToUserResponse(savedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        if (user.getRole() == Role.ADMIN) {
            throw new RuntimeException("Không thể xóa tài khoản Admin");
        }

        userRepository.delete(user);
    }

    public List<UserResponse> searchUsers(String keyword) {
        String searchTerm = keyword.toLowerCase();
        return userRepository.findAll().stream()
                .filter(u -> u.getEmail().toLowerCase().contains(searchTerm))
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    // === Job Posting Management ===
    public List<JobPostingResponse> getAllJobPostings() {
        return jobPostingRepository.findAll().stream()
                .map(this::mapToJobPostingResponse)
                .collect(Collectors.toList());
    }

    public JobPostingResponse getJobPostingById(Long id) {
        JobPosting job = jobPostingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tin tuyển dụng"));
        return mapToJobPostingResponse(job);
    }

    @Transactional
    public JobPostingResponse updateJobStatus(Long id, String status) {
        JobPosting job = jobPostingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tin tuyển dụng"));

        try {
            JobStatus newStatus = JobStatus.valueOf(status.toUpperCase());
            job.setStatus(newStatus);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Trạng thái không hợp lệ: " + status);
        }

        JobPosting savedJob = jobPostingRepository.saveAndFlush(job);
        return mapToJobPostingResponse(savedJob);
    }

    @Transactional
    public void deleteJobPosting(Long id) {
        JobPosting job = jobPostingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tin tuyển dụng"));
        jobPostingRepository.delete(job);
    }

    private JobPostingResponse mapToJobPostingResponse(JobPosting job) {
        return JobPostingResponse.builder()
                .id(job.getId())
                .companyId(job.getCompany().getId())
                .companyName(job.getCompany().getName())
                .title(job.getTitle())
                .description(job.getDescription())
                .requirements(job.getRequirements())
                .salaryMin(job.getSalaryMin())
                .salaryMax(job.getSalaryMax())
                .location(job.getLocation())
                .jobType(job.getJobType())
                .status(job.getStatus())
                .jobFieldName(job.getJobField() != null ? job.getJobField().getName() : null)
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }

    // === Company Management ===
    public List<CompanyResponse> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(this::mapToCompanyResponse)
                .collect(Collectors.toList());
    }

    public CompanyResponse getCompanyById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công ty"));
        return mapToCompanyResponse(company);
    }

    public List<CompanyResponse> searchCompanies(String keyword) {
        String searchTerm = keyword.toLowerCase();
        return companyRepository.findAll().stream()
                .filter(c -> c.getName().toLowerCase().contains(searchTerm) ||
                        c.getUser().getEmail().toLowerCase().contains(searchTerm))
                .map(this::mapToCompanyResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteCompany(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công ty"));
        companyRepository.delete(company);
    }

    private CompanyResponse mapToCompanyResponse(Company company) {
        return CompanyResponse.builder()
                .id(company.getId())
                .userId(company.getUser().getId())
                .userEmail(company.getUser().getEmail())
                .name(company.getName())
                .logoUrl(company.getLogoUrl())
                .website(company.getWebsite())
                .industry(company.getIndustry())
                .companySize(company.getCompanySize())
                .description(company.getDescription())
                .createdAt(company.getUser().getCreatedAt())
                .build();
    }
}
