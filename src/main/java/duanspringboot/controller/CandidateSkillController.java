package duanspringboot.controller;

import duanspringboot.dto.candidate.CandidateSkillRequest;
import duanspringboot.dto.candidate.CandidateSkillResponse;
import duanspringboot.security.CustomUserDetails;
import duanspringboot.service.CandidateSkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candidate/skills")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CANDIDATE')")
public class CandidateSkillController {

    private final CandidateSkillService skillService;

    @PostMapping
    public ResponseEntity<CandidateSkillResponse> addSkill(
            @Valid @RequestBody CandidateSkillRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        return ResponseEntity.ok(skillService.addSkill(request, userId));
    }

    @GetMapping
    public ResponseEntity<List<CandidateSkillResponse>> getMySkills(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        return ResponseEntity.ok(skillService.getMySkills(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSkill(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        skillService.deleteSkill(id, userId);
        return ResponseEntity.noContent().build();
    }
}
