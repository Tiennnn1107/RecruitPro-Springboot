package duanspringboot.service;

import duanspringboot.dto.savedjob.SavedJobResponse;
import duanspringboot.entity.SavedJob;
import duanspringboot.entity.User;
import duanspringboot.repository.SavedJobRepository;
import duanspringboot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SavedJobService {

    private final SavedJobRepository savedJobRepository;
    private final UserRepository userRepository;
    private final JobPostingService jobPostingService;

    @Transactional
    public SavedJobResponse saveJob(Long userId, Long jobPostingId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        if (savedJobRepository.existsByUserIdAndJobPostingId(userId, jobPostingId)) {
            throw new RuntimeException("Job đã được lưu trước đó");
        }

        var jobPosting = duanspringboot.entity.JobPosting.builder().id(jobPostingId).build();

        SavedJob savedJob = SavedJob.builder()
                .user(user)
                .jobPosting(jobPosting)
                .build();

        savedJob = savedJobRepository.save(savedJob);
        return mapToResponse(savedJob);
    }

    @Transactional
    public void unsaveJob(Long userId, Long jobPostingId) {
        savedJobRepository.findByUserIdAndJobPostingId(userId, jobPostingId)
                .ifPresent(savedJobRepository::delete);
    }

    public List<SavedJobResponse> getSavedJobs(Long userId) {
        return savedJobRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public boolean isJobSaved(Long userId, Long jobPostingId) {
        return savedJobRepository.existsByUserIdAndJobPostingId(userId, jobPostingId);
    }

    private SavedJobResponse mapToResponse(SavedJob savedJob) {
        var job = jobPostingService.getById(savedJob.getJobPosting().getId());
        return SavedJobResponse.builder()
                .id(savedJob.getId())
                .jobId(savedJob.getJobPosting().getId())
                .jobTitle(job.getTitle())
                .companyName(job.getCompanyName())
                .location(job.getLocation())
                .salaryRange(job.getSalaryRange())
                .savedAt(savedJob.getSavedAt())
                .build();
    }
}