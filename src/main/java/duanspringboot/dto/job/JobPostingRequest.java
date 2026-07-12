package duanspringboot.dto.job;
import duanspringboot.enums.JobType;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPostingRequest {

    @NotBlank(message = "Title không được để trống")
    private String title;
    private String description;
    private String requirements;
    private String location;
    private Integer salaryMax;
    private Integer salaryMin;
    private JobType jobType;
    private LocalDateTime expiredAt;
    private List<String> skills;

}
