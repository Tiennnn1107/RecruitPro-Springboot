package duanspringboot.controller;

import duanspringboot.dto.jobfield.JobFieldRequest;
import duanspringboot.dto.jobfield.JobFieldResponse;
import duanspringboot.service.JobFieldService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job-fields")
@RequiredArgsConstructor
public class JobFieldController {

    private final JobFieldService jobFieldService;

    @GetMapping
    public ResponseEntity<List<JobFieldResponse>> getAllJobFields() {
        return ResponseEntity.ok(jobFieldService.getAllJobFields());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobFieldResponse> getJobFieldById(@PathVariable Long id) {
        return ResponseEntity.ok(jobFieldService.getJobFieldById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<JobFieldResponse> createJobField(@Valid @RequestBody JobFieldRequest request) {
        return ResponseEntity.ok(jobFieldService.createJobField(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<JobFieldResponse> updateJobField(
            @PathVariable Long id,
            @Valid @RequestBody JobFieldRequest request) {
        return ResponseEntity.ok(jobFieldService.updateJobField(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteJobField(@PathVariable Long id) {
        jobFieldService.deleteJobField(id);
        return ResponseEntity.noContent().build();
    }
}
