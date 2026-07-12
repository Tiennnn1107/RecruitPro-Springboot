package duanspringboot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import duanspringboot.dto.Application.ApplicationDetailResponse;
import duanspringboot.dto.Application.ApplicationRequest;
import duanspringboot.dto.Application.ApplicationResponse;
import duanspringboot.dto.Application.ApplicationStatusRequest;
import duanspringboot.dto.Application.PipelineData;
import duanspringboot.security.CustomUserDetails;
import duanspringboot.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationService applicationService;

    // 1. Ứng viên nộp đơn
    @PostMapping
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<ApplicationResponse> applyForJob(
            @Valid @RequestBody ApplicationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        return ResponseEntity.ok(applicationService.apply(request, userId));
    }

    // 2. Ứng viên: Xem lại các công việc đã ứng tuyển
    @GetMapping("/my-applications")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<List<ApplicationResponse>> getMyApplications(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        return ResponseEntity.ok(applicationService.getMyApplications(userId));
    }

    // 3. Nhà tuyển dụng: Xem danh sách ứng viên cho một job cụ thể
    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsByJob(
            @PathVariable Long jobId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        return ResponseEntity.ok(applicationService.getApplicationsByJob(jobId, userId));
    }

    // 4. Nhà tuyển dụng: Cập nhật trạng thái đơn (Pipeline)
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApplicationResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody ApplicationStatusRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        return ResponseEntity.ok(applicationService.changeStatus(id, request, userId));
    }

    // 5. Pipeline: Lấy dữ liệu pipeline với filter (API for AJAX)
    @GetMapping("/pipeline")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<PipelineData> getPipelineData(
            @RequestParam(required = false) Long jobId,
            @RequestParam(required = false) String search,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        PipelineData data = applicationService.getPipelineData(userId, jobId, search);
        return ResponseEntity.ok(data);
    }

    // 6. Pipeline: Lấy chi tiết ứng viên (Quick View Panel)
    @GetMapping("/{id}/detail")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApplicationDetailResponse> getApplicationDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        return ResponseEntity.ok(applicationService.getApplicationDetail(id, userId));
    }
}
