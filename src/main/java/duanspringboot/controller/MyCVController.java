package duanspringboot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import duanspringboot.entity.MyCV;
import duanspringboot.security.CustomUserDetails;
import duanspringboot.service.MyCVService;
import duanspringboot.service.CandidateProfileService;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/candidate/mycv")
@RequiredArgsConstructor
public class MyCVController {
    private final MyCVService myCVService;
    private final CandidateProfileService candidateProfileService;

    @PostMapping
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<?> addMyCV(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> request) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        var profile = candidateProfileService.getMyProfile(userId);
        if (profile == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Vui lòng tạo hồ sơ ứng viên trước"));
        }

        MyCV myCV = myCVService.addMyCV(
                profile.getId(),
                request.get("cvTitle"),
                request.get("cvUrl"),
                request.get("fileName"),
                request.get("fileType"),
                request.get("fileSize") != null ? Long.parseLong(request.get("fileSize")) : null);
        return ResponseEntity.ok(myCV);
    }

    @GetMapping
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<List<MyCV>> getMyCVs(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        var profile = candidateProfileService.getMyProfile(userId);
        if (profile == null) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(myCVService.getMyCVsByCandidateId(profile.getId()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<?> updateMyCV(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        MyCV myCV = myCVService.updateMyCV(
                id,
                request.get("cvTitle"),
                request.get("cvUrl"),
                request.get("fileName"),
                request.get("fileType"),
                request.get("fileSize") != null ? Long.parseLong(request.get("fileSize")) : null);
        return ResponseEntity.ok(myCV);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<?> deleteMyCV(@PathVariable Long id) {
        myCVService.deleteMyCV(id);
        return ResponseEntity.ok(Map.of("message", "Đã xóa CV"));
    }

    @PutMapping("/{id}/default")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<?> setDefaultCV(@PathVariable Long id) {
        MyCV myCV = myCVService.setDefaultCV(id);
        return ResponseEntity.ok(myCV);
    }
}