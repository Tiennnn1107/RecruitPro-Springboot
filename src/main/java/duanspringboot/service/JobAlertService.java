package duanspringboot.service;

import duanspringboot.dto.jobalert.JobAlertRequest;
import duanspringboot.dto.jobalert.JobAlertResponse;
import duanspringboot.entity.JobAlert;
import duanspringboot.entity.JobPosting;
import duanspringboot.entity.User;
import duanspringboot.repository.JobAlertRepository;
import duanspringboot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobAlertService {

    private final JobAlertRepository jobAlertRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final JobFieldService jobFieldService;

    public List<JobAlertResponse> getAlertsByUser(Long userId) {
        return jobAlertRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public JobAlertResponse createAlert(Long userId, JobAlertRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        JobAlert alert = JobAlert.builder()
                .user(user)
                .keywords(request.getKeywords())
                .location(request.getLocation())
                .minSalary(request.getMinSalary())
                .isActive(true)
                .build();

        if (request.getJobFieldId() != null) {
            var jobField = jobFieldService.getJobFieldEntity(request.getJobFieldId());
            alert.setJobField(jobField);
        }

        return mapToResponse(jobAlertRepository.save(alert));
    }

    @Transactional
    public JobAlertResponse updateAlert(Long alertId, Long userId, JobAlertRequest request) {
        JobAlert alert = jobAlertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy alert"));

        if (!alert.getUser().getId().equals(userId)) {
            throw new RuntimeException("Không có quyền cập nhật alert này");
        }

        alert.setKeywords(request.getKeywords());
        alert.setLocation(request.getLocation());
        alert.setMinSalary(request.getMinSalary());
        if (request.getIsActive() != null) {
            alert.setIsActive(request.getIsActive());
        }

        if (request.getJobFieldId() != null) {
            var jobField = jobFieldService.getJobFieldEntity(request.getJobFieldId());
            alert.setJobField(jobField);
        }

        return mapToResponse(jobAlertRepository.save(alert));
    }

    @Transactional
    public void deleteAlert(Long alertId, Long userId) {
        JobAlert alert = jobAlertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy alert"));

        if (!alert.getUser().getId().equals(userId)) {
            throw new RuntimeException("Không có quyền xóa alert này");
        }

        jobAlertRepository.delete(alert);
    }

    @Transactional
    public void toggleAlert(Long alertId, Long userId) {
        JobAlert alert = jobAlertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy alert"));

        if (!alert.getUser().getId().equals(userId)) {
            throw new RuntimeException("Không có quyền cập nhật alert này");
        }

        alert.setIsActive(!alert.getIsActive());
        jobAlertRepository.save(alert);
    }

    @Transactional(readOnly = true)
    public void checkAndNotifyNewJob(JobPosting job) {
        List<JobAlert> activeAlerts = jobAlertRepository.findByIsActiveTrue();

        for (JobAlert alert : activeAlerts) {
            boolean matches = matchesAlert(job, alert);
            if (matches) {
                String title = "Có việc làm mới phù hợp!";
                String message = String.format("Việc làm '%s' tại %s phù hợp với alert '%s' của bạn.",
                        job.getTitle(),
                        job.getLocation(),
                        alert.getKeywords() != null ? alert.getKeywords() : "đã thiết lập");
                notificationService.createNotification(alert.getUser().getId(), title, message);
            }
        }
    }

    private boolean matchesAlert(JobPosting job, JobAlert alert) {
        // Check keywords
        if (alert.getKeywords() != null && !alert.getKeywords().isBlank()) {
            if (!job.getTitle().toLowerCase().contains(alert.getKeywords().toLowerCase())) {
                return false;
            }
        }

        // Check location
        if (alert.getLocation() != null && !alert.getLocation().isBlank()) {
            if (!job.getLocation().toLowerCase().contains(alert.getLocation().toLowerCase())) {
                return false;
            }
        }

        // Check salary
        if (alert.getMinSalary() != null && job.getSalaryMax() != null) {
            if (job.getSalaryMax() < alert.getMinSalary()) {
                return false;
            }
        }

        // Check job field
        if (alert.getJobField() != null && job.getJobField() != null) {
            if (!alert.getJobField().getId().equals(job.getJobField().getId())) {
                return false;
            }
        }

        return true;
    }

    private JobAlertResponse mapToResponse(JobAlert alert) {
        return JobAlertResponse.builder()
                .id(alert.getId())
                .keywords(alert.getKeywords())
                .location(alert.getLocation())
                .minSalary(alert.getMinSalary())
                .jobFieldId(alert.getJobField() != null ? alert.getJobField().getId() : null)
                .jobFieldName(alert.getJobField() != null ? alert.getJobField().getName() : null)
                .isActive(alert.getIsActive())
                .createdAt(alert.getCreatedAt())
                .build();
    }
}