package duanspringboot.service;

import duanspringboot.dto.jobfield.JobFieldRequest;
import duanspringboot.dto.jobfield.JobFieldResponse;
import duanspringboot.entity.JobField;
import duanspringboot.repository.JobFieldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobFieldService {

    private final JobFieldRepository jobFieldRepository;

    public List<JobFieldResponse> getAllJobFields() {
        return jobFieldRepository.findAllByOrderByNameAsc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public JobFieldResponse getJobFieldById(Long id) {
        JobField jobField = jobFieldRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lĩnh vực"));
        return mapToResponse(jobField);
    }

    public JobField getJobFieldEntity(Long id) {
        return jobFieldRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lĩnh vực"));
    }

    @Transactional
    public JobFieldResponse createJobField(JobFieldRequest request) {
        if (jobFieldRepository.findAllByOrderByNameAsc().stream()
                .anyMatch(jf -> jf.getName().equalsIgnoreCase(request.getName()))) {
            throw new RuntimeException("Lĩnh vực này đã tồn tại");
        }

        JobField jobField = JobField.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        return mapToResponse(jobFieldRepository.save(jobField));
    }

    @Transactional
    public JobFieldResponse updateJobField(Long id, JobFieldRequest request) {
        JobField jobField = jobFieldRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lĩnh vực"));

        if (jobFieldRepository.findAllByOrderByNameAsc().stream()
                .anyMatch(jf -> jf.getName().equalsIgnoreCase(request.getName()) && !jf.getId().equals(id))) {
            throw new RuntimeException("Lĩnh vực này đã tồn tại");
        }

        jobField.setName(request.getName());
        jobField.setDescription(request.getDescription());

        return mapToResponse(jobFieldRepository.save(jobField));
    }

    @Transactional
    public void deleteJobField(Long id) {
        JobField jobField = jobFieldRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lĩnh vực"));

        if (!jobField.getJobPostings().isEmpty()) {
            throw new RuntimeException("Không thể xóa lĩnh vực đã có tin tuyển dụng");
        }

        jobFieldRepository.delete(jobField);
    }

    private JobFieldResponse mapToResponse(JobField jobField) {
        return JobFieldResponse.builder()
                .id(jobField.getId())
                .name(jobField.getName())
                .description(jobField.getDescription())
                .createdAt(jobField.getCreatedAt())
                .updatedAt(jobField.getUpdatedAt())
                .build();
    }
}
