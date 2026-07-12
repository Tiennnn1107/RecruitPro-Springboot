package duanspringboot.dto.admin;

import duanspringboot.enums.JobStatus;
import duanspringboot.enums.JobType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class JobPostingResponse {
    private Long id;
    private Long companyId;
    private String companyName;
    private String title;
    private String description;
    private String requirements;
    private Integer salaryMin;
    private Integer salaryMax;
    private String location;
    private JobType jobType;
    private JobStatus status;
    private String jobFieldName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
