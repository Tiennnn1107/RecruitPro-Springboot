package duanspringboot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import duanspringboot.entity.LinkCV;
import duanspringboot.security.CustomUserDetails;
import duanspringboot.service.LinkCVService;
import duanspringboot.service.CandidateProfileService;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/candidate/linkcv")
@RequiredArgsConstructor
public class LinkCVController {
    private final LinkCVService linkCVService;
    private final CandidateProfileService candidateProfileService;

    @PostMapping
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<?> addLinkCV(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> request) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        var profile = candidateProfileService.getMyProfile(userId);
        if (profile == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Vui lòng tạo hồ sơ ứng viên trước"));
        }

        LinkCV linkCV = linkCVService.addLinkCV(
                profile.getId(),
                request.get("linkTitle"),
                request.get("linkUrl"),
                request.get("linkType"));
        return ResponseEntity.ok(linkCV);
    }

    @GetMapping
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<List<LinkCV>> getMyLinkCVs(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        var profile = candidateProfileService.getMyProfile(userId);
        if (profile == null) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(linkCVService.getLinkCVsByCandidateId(profile.getId()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<?> updateLinkCV(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        LinkCV linkCV = linkCVService.updateLinkCV(
                id,
                request.get("linkTitle"),
                request.get("linkUrl"),
                request.get("linkType"));
        return ResponseEntity.ok(linkCV);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<?> deleteLinkCV(@PathVariable Long id) {
        linkCVService.deleteLinkCV(id);
        return ResponseEntity.ok(Map.of("message", "Đã xóa link CV"));
    }
}