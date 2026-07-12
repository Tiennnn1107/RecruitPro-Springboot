package duanspringboot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import duanspringboot.dto.profile.CandidateProfileRequest;
import duanspringboot.dto.profile.CandidateProfileResponse;
import duanspringboot.security.CustomUserDetails;
import duanspringboot.service.CandidateProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/candidate/profile")
@RequiredArgsConstructor
public class CandidateProfileController {
    private final CandidateProfileService profileService;

    // 1. Tạo hoặc Cập nhật Profile (Ứng viên)
    @PutMapping
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<CandidateProfileResponse> updateProfile(
            @Valid @RequestBody CandidateProfileRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        return ResponseEntity.ok(profileService.updateProfile(request, userId));
    }

    // 2. Xem thông tin hồ sơ của chính mình (Ứng viên)
    @GetMapping
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<CandidateProfileResponse> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        return ResponseEntity.ok(profileService.getMyProfile(userId));
    }
}
