package duanspringboot.controller;

import duanspringboot.dto.HomeStatsResponse;
import duanspringboot.dto.profile.CandidateProfileResponse;
import duanspringboot.enums.JobStatus;
import duanspringboot.enums.Role;
import duanspringboot.repository.CompanyRepository;
import duanspringboot.repository.JobPostingRepository;
import duanspringboot.repository.UserRepository;
import duanspringboot.security.CustomUserDetails;
import duanspringboot.service.ApplicationService;
import duanspringboot.service.CandidateProfileService;
import duanspringboot.service.CompanyService;
import duanspringboot.service.InterviewService;
import duanspringboot.service.JobFieldService;
import duanspringboot.service.JobPostingService;
import duanspringboot.service.NotificationService;
import duanspringboot.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ViewController {

    private final JobPostingService jobPostingService;
    private final CandidateProfileService candidateProfileService;
    private final ApplicationService applicationService;
    private final CompanyService companyService;
    private final InterviewService interviewService;
    private final NotificationService notificationService;
    private final JobFieldService jobFieldService;
    private final BlogService blogService;
    private final JobPostingRepository jobPostingRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    // --- Trang chủ & Auth ---
    @GetMapping("/")
    public String index(Model model) {
        long totalJobs = jobPostingRepository.countByStatus(JobStatus.OPEN);
        long totalCompanies = companyRepository.count();
        long totalCandidates = userRepository.countByRole(Role.CANDIDATE);

        HomeStatsResponse stats = HomeStatsResponse.builder()
                .totalJobs(totalJobs)
                .totalCompanies(totalCompanies)
                .totalCandidates(totalCandidates)
                .build();

        model.addAttribute("homeStats", stats);
        return "common/index";
    }

    // --- Jobs by Field (Ngành nghề) ---
    @GetMapping("/jobs/field/{fieldId}")
    public String jobsByField(@PathVariable Long fieldId, Model model) {
        try {
            var field = jobFieldService.getJobFieldById(fieldId);
            model.addAttribute("fieldId", fieldId);
            model.addAttribute("fieldName", field.getName());
        } catch (Exception e) {
            model.addAttribute("fieldId", null);
            model.addAttribute("fieldName", "Ngành nghề");
        }
        return "common/jobs-by-field";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String register() {
        return "auth/register";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "auth/forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPassword(@RequestParam(required = false) String token, Model model) {
        model.addAttribute("token", token);
        return "auth/reset-password";
    }

    // --- Job Details ---
    @GetMapping("/jobs/{id}")
    public String jobDetail(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("job", jobPostingService.getById(id));
        } catch (Exception e) {
            model.addAttribute("job", null);
        }
        return "common/job-detail";
    }

    // --- Company Details ---
    @GetMapping("/companies/{id}")
    public String companyDetail(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("company", companyService.getById(id));
            model.addAttribute("jobs", jobPostingService.getPublicJobsByCompanyId(id));
        } catch (Exception e) {
            model.addAttribute("company", null);
            model.addAttribute("jobs", null);
        }
        return "common/company-detail";
    }

    // --- Candidate Views ---
    @GetMapping("/candidate/profile")
    public String candidateProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            Long userId = ((CustomUserDetails) userDetails).getId();
            CandidateProfileResponse profile = candidateProfileService.getMyProfile(userId);
            System.out.println("DEBUG: Username: " + userDetails.getUsername() + " | Profile fetched: " + profile);
            model.addAttribute("profile", profile);
            model.addAttribute("userEmail", userDetails.getUsername());
        }
        return "candidate/profile";
    }

    // ✅ ĐÃ SỬA: Thêm @AuthenticationPrincipal và truyền data vào model
    @GetMapping("/candidate/applications")
    public String myApplications(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        if (userDetails == null)
            return "redirect:/login";
        Long userId = ((CustomUserDetails) userDetails).getId();
        try {
            model.addAttribute("applications", applicationService.getMyApplications(userId));
        } catch (Exception e) {
            model.addAttribute("applications", List.of());
        }
        return "candidate/my-applications";
    }

    // --- Saved Jobs ---
    @GetMapping("/candidate/saved-jobs")
    public String savedJobs() {
        return "candidate/saved-jobs";
    }

    // --- Job Alerts ---
    @GetMapping("/candidate/job-alerts")
    public String jobAlerts() {
        return "candidate/job-alerts";
    }

    // --- Messages ---
    @GetMapping("/messages")
    public String messages() {
        return "common/messages";
    }

    @GetMapping("/messages/{userId}")
    public String conversationWith(@PathVariable Long userId, Model model) {
        model.addAttribute("otherUserId", userId);
        return "common/messages";
    }

    // --- Recruiter Views ---
    @GetMapping("/recruiter/approval")
    public String recruiterApproval(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        return "recruiter/approval";
    }

    @GetMapping("/recruiter/company")
    public String companyInfo(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            try {
                Long userId = ((CustomUserDetails) userDetails).getId();
                model.addAttribute("company", companyService.getMyCompany(userId));
            } catch (Exception e) {
                model.addAttribute("company", null);
            }
        }
        return "recruiter/company-info";
    }

    @GetMapping("/recruiter/jobs")
    public String manageJobs() {
        return "recruiter/jobs/list";
    }

    @GetMapping("/recruiter/jobs/create")
    public String createJob() {
        return "recruiter/jobs/create-edit";
    }

    @GetMapping("/recruiter/jobs/edit/{id}")
    public String editJob(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            try {
                model.addAttribute("job", jobPostingService.getById(id));
            } catch (Exception e) {
                model.addAttribute("job", null);
            }
        }
        return "recruiter/jobs/create-edit";
    }

    @GetMapping("/recruiter/applications")
    public String viewApplicationsList() {
        return "redirect:/recruiter/jobs";
    }

    @GetMapping("/recruiter/applications/job/{jobId}")
    public String viewApplicationsForJob(@PathVariable Long jobId, @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        if (userDetails != null) {
            Long userId = ((CustomUserDetails) userDetails).getId();
            try {
                model.addAttribute("job", jobPostingService.getById(jobId));
                model.addAttribute("applications", applicationService.getApplicationsByJob(jobId, userId));
            } catch (Exception e) {
                // Ignore or handle
            }
        }
        return "recruiter/applications/job-applications";
    }

    @GetMapping("/recruiter/applications/pipeline")
    public String viewPipeline(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        Long userId = ((CustomUserDetails) userDetails).getId();
        try {
            var pipelineData = applicationService.getPipelineData(userId);
            model.addAttribute("pendingApps", pipelineData.getPendingApps());
            model.addAttribute("reviewingApps", pipelineData.getReviewingApps());
            model.addAttribute("interviewingApps", pipelineData.getInterviewingApps());
            model.addAttribute("finalApps", pipelineData.getFinalApps());
            model.addAttribute("counts", pipelineData.getCounts());
            model.addAttribute("myJobs", jobPostingService.getJobsByRecruiterUserId(userId));
        } catch (Exception e) {
            model.addAttribute("pendingApps", List.of());
            model.addAttribute("reviewingApps", List.of());
            model.addAttribute("interviewingApps", List.of());
            model.addAttribute("finalApps", List.of());
            model.addAttribute("counts", Map.of("pending", 0, "reviewing", 0, "interviewing", 0, "final", 0));
            model.addAttribute("myJobs", List.of());
        }
        return "recruiter/applications/pipeline";
    }

    @GetMapping("/recruiter/applications/{id}")
    public String viewApplicationDetail(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        Long userId = ((CustomUserDetails) userDetails).getId();
        try {
            var detail = applicationService.getApplicationDetail(id, userId);
            model.addAttribute("application", detail);
        } catch (Exception e) {
            model.addAttribute("application", null);
        }
        return "recruiter/applications/application-detail";
    }

    @GetMapping("/recruiter/interviews")
    public String viewInterviews(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            Long userId = ((CustomUserDetails) userDetails).getId();
            try {
                model.addAttribute("interviews", interviewService.getMyInterviews(userId));
            } catch (Exception e) {
                model.addAttribute("interviews", List.of());
            }
        }
        return "recruiter/interviews/schedule";
    }

    // --- Notification Views ---
    @GetMapping("/notifications")
    public String viewNotifications(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        return "common/notifications";
    }

    // --- Admin Views ---
    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminDashboard(@AuthenticationPrincipal UserDetails userDetails) {
        return "admin/dashboard";
    }

    @GetMapping("/admin/blogs")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminBlogs(@AuthenticationPrincipal UserDetails userDetails) {
        return "admin/blogs";
    }

    // --- Public Blogs ---
    @GetMapping("/blogs")
    public String publicBlogs(Model model) {
        model.addAttribute("blogs", blogService.getAll());
        return "common/blogs";
    }

    @GetMapping("/blogs/{id}")
    public String publicBlogDetail(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("blog", blogService.getById(id));
        } catch (Exception e) {
            model.addAttribute("blog", null);
        }
        return "common/blog-detail";
    }
}
