package duanspringboot.controller;

import duanspringboot.dto.recruiter.RecruiterApprovalRequestDTO;
import duanspringboot.dto.recruiter.RecruiterApprovalResponse;
import duanspringboot.dto.recruiter.RecruiterApprovalStatus;
import duanspringboot.security.CustomUserDetails;
import duanspringboot.service.RecruiterApprovalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recruiter-approval")
@RequiredArgsConstructor
public class RecruiterApprovalController {

    private final RecruiterApprovalService approvalService;

    @PostMapping
    public ResponseEntity<RecruiterApprovalResponse> createApprovalRequest(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody RecruiterApprovalRequestDTO dto) {
        RecruiterApprovalResponse response = approvalService.createApprovalRequest(userDetails.getId(), dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<RecruiterApprovalResponse> getMyApprovalRequest(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        RecruiterApprovalResponse response = approvalService.getApprovalRequestByUserId(userDetails.getId());
        return response != null ? ResponseEntity.ok(response) : ResponseEntity.noContent().build();
    }

    @GetMapping("/status")
    public ResponseEntity<RecruiterApprovalStatus> getApprovalStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        RecruiterApprovalStatus status = approvalService.getApprovalStatus(userDetails.getId());
        return ResponseEntity.ok(status);
    }
}

@RestController
@RequestMapping("/api/admin/recruiter-approvals")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
class AdminRecruiterApprovalController {

    private final RecruiterApprovalService approvalService;

    @GetMapping
    public ResponseEntity<List<RecruiterApprovalResponse>> getAllApprovalRequests() {
        return ResponseEntity.ok(approvalService.getAllApprovalRequests());
    }

    @GetMapping("/pending")
    public ResponseEntity<List<RecruiterApprovalResponse>> getPendingApprovalRequests() {
        return ResponseEntity.ok(approvalService.getPendingApprovalRequests());
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<RecruiterApprovalResponse> approveRequest(
            @PathVariable Long id,
            @RequestParam(required = false) String adminNote) {
        return ResponseEntity.ok(approvalService.approveRequest(id, adminNote));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<RecruiterApprovalResponse> rejectRequest(
            @PathVariable Long id,
            @RequestParam(required = false) String adminNote) {
        return ResponseEntity.ok(approvalService.rejectRequest(id, adminNote));
    }
}
