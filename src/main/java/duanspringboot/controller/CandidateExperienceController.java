package duanspringboot.controller;

import duanspringboot.dto.candidate.CandidateExperienceRequest;
import duanspringboot.dto.candidate.CandidateExperienceResponse;
import duanspringboot.security.CustomUserDetails;
import duanspringboot.service.CandidateExperienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candidate/experiences")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CANDIDATE')")
public class CandidateExperienceController {

    private final CandidateExperienceService experienceService;

    @PostMapping
    public ResponseEntity<CandidateExperienceResponse> create(
            @RequestBody CandidateExperienceRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        return ResponseEntity.ok(experienceService.create(request, userId));
    }

    @GetMapping
    public ResponseEntity<List<CandidateExperienceResponse>> getMyExperiences(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        return ResponseEntity.ok(experienceService.getMyExperiences(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CandidateExperienceResponse> update(
            @PathVariable Long id,
            @RequestBody CandidateExperienceRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        return ResponseEntity.ok(experienceService.update(id, request, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        experienceService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }
}
