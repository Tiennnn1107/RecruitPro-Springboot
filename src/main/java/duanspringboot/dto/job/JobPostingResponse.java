package duanspringboot.dto.job;
import java.time.LocalDateTime;
import java.util.List;

import duanspringboot.enums.JobStatus;
import duanspringboot.enums.JobType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPostingResponse {
    private Long id;
    private Long companyId;
    private Long recruiterUserId;
    private String recruiterEmail;
    private String title;
    private String companyName;
    private String description;
    private String requirements;
    private String location;
    private String salaryRange;
    private Integer salaryMax;
    private Integer salaryMin;
    private JobType jobType;
    private JobStatus status;
    private LocalDateTime expiredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer applicationCount;
    private List<String> requiredSkills;
    private String jobFieldName;
}
