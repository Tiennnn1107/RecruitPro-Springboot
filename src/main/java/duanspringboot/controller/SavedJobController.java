package duanspringboot.controller;

import duanspringboot.dto.savedjob.SavedJobResponse;
import duanspringboot.security.CustomUserDetails;
import duanspringboot.service.SavedJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/saved-jobs")
@RequiredArgsConstructor
public class SavedJobController {

    private final SavedJobService savedJobService;

    @GetMapping
    public ResponseEntity<List<SavedJobResponse>> getSavedJobs(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(savedJobService.getSavedJobs(userId));
    }

    @PostMapping("/{jobId}")
    public ResponseEntity<SavedJobResponse> saveJob(
            @PathVariable Long jobId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(savedJobService.saveJob(userId, jobId));
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<Void> unsaveJob(
            @PathVariable Long jobId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        savedJobService.unsaveJob(userId, jobId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check/{jobId}")
    public ResponseEntity<Boolean> isJobSaved(
            @PathVariable Long jobId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(savedJobService.isJobSaved(userId, jobId));
    }

    private Long getUserId(UserDetails userDetails) {
        return ((CustomUserDetails) userDetails).getId();
    }
}