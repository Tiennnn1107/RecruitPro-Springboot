package duanspringboot.dto.recruiter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruiterApprovalStatus {
    private boolean hasSubmitted;
    private String status;
    private boolean canPostJobs;
    private String message;
}
