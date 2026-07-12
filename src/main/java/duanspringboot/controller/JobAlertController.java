package duanspringboot.controller;

import duanspringboot.dto.jobalert.JobAlertRequest;
import duanspringboot.dto.jobalert.JobAlertResponse;
import duanspringboot.security.CustomUserDetails;
import duanspringboot.service.JobAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job-alerts")
@RequiredArgsConstructor
public class JobAlertController {

    private final JobAlertService jobAlertService;

    @GetMapping
    public ResponseEntity<List<JobAlertResponse>> getMyAlerts(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(jobAlertService.getAlertsByUser(userId));
    }

    @PostMapping
    public ResponseEntity<JobAlertResponse> createAlert(
            @RequestBody JobAlertRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(jobAlertService.createAlert(userId, request));
    }

    @PutMapping("/{alertId}")
    public ResponseEntity<JobAlertResponse> updateAlert(
            @PathVariable Long alertId,
            @RequestBody JobAlertRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(jobAlertService.updateAlert(alertId, userId, request));
    }

    @DeleteMapping("/{alertId}")
    public ResponseEntity<Void> deleteAlert(
            @PathVariable Long alertId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        jobAlertService.deleteAlert(alertId, userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{alertId}/toggle")
    public ResponseEntity<Void> toggleAlert(
            @PathVariable Long alertId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        jobAlertService.toggleAlert(alertId, userId);
        return ResponseEntity.noContent().build();
    }

    private Long getUserId(UserDetails userDetails) {
        return ((CustomUserDetails) userDetails).getId();
    }
}