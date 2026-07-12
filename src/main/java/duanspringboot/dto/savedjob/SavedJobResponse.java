package duanspringboot.dto.savedjob;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedJobResponse {
    private Long id;
    private Long jobId;
    private String jobTitle;
    private String companyName;
    private String location;
    private String salaryRange;
    private LocalDateTime savedAt;
}