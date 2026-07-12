package duanspringboot.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import duanspringboot.dto.job.JobPostingRequest;
import duanspringboot.dto.job.JobPostingResponse;
import duanspringboot.enums.JobStatus;
import duanspringboot.security.CustomUserDetails;
import duanspringboot.service.JobPostingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobPostingController {

    private final JobPostingService jobPostingService;

    // 1. Đăng tin mới (Recruiter)
    @PostMapping
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<JobPostingResponse> createJob(
            @Valid @RequestBody JobPostingRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        return ResponseEntity.ok(jobPostingService.create(request, userId));
    }

    // 2. Cập nhật tin (Recruiter)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<JobPostingResponse> updateJob(
            @PathVariable Long id,
            @Valid @RequestBody JobPostingRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        return ResponseEntity.ok(jobPostingService.update(id, request, userId));
    }

    // 3. Xóa tin (Recruiter)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<Void> deleteJob(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        jobPostingService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }

    // 4. Tìm kiếm việc làm với filter (Công khai)
    @GetMapping("/search")
    public ResponseEntity<List<JobPostingResponse>> searchJobs(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer minSalary,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long fieldId) {
        return ResponseEntity.ok(jobPostingService.searchJobs(location, minSalary, title, fieldId));
    }

    // 5. Lấy tin tuyển dụng của chính mình (Recruiter quản lý)
    @GetMapping("/my-jobs")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<List<JobPostingResponse>> getMyJobs(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        return ResponseEntity.ok(jobPostingService.getMyCompanyJobs(userId));
    }

    // 6. Lấy chi tiết tin (Công khai)
    @GetMapping("/{id}")
    public ResponseEntity<JobPostingResponse> getJobById(@PathVariable Long id) {
        return ResponseEntity.ok(jobPostingService.getById(id));
    }

    // 7. Đóng/Mở tin nhanh (Recruiter)
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<JobPostingResponse> changeStatus(
            @PathVariable Long id,
            @RequestParam JobStatus status,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        return ResponseEntity.ok(jobPostingService.changeStatus(id, status, userId));
    }

    // 8. Tìm kiếm việc làm với phân trang (Công khai)
    @GetMapping("/search/paginated")
    public ResponseEntity<Map<String, Object>> searchJobsPaginated(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer minSalary,
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(jobPostingService.searchJobsPaginated(location, minSalary, title, page, size));
    }

    // 9. Lấy tin tuyển dụng của chính mình với phân trang (Recruiter)
    @GetMapping("/my-jobs/paginated")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<Map<String, Object>> getMyJobsPaginated(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        return ResponseEntity.ok(jobPostingService.getMyCompanyJobsPaginated(userId, page, size));
    }

    // 10. Lấy tất cả tin đang mở với phân trang (cho trang chủ)
    @GetMapping("/public/paginated")
    public ResponseEntity<Map<String, Object>> getAllOpenJobsPaginated(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(jobPostingService.getAllOpenJobsPaginated(page, size));
    }
}
