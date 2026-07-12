package duanspringboot.dto.recruiter;

import duanspringboot.enums.ApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruiterApprovalResponse {
    private Long id;
    private Long userId;
    private String userEmail;
    private String companyName;
    private String taxCode;
    private String businessLicenseUrl;
    private String companyAddress;
    private String companyWebsite;
    private String companyPhone;
    private String contactPersonName;
    private String contactPersonPhone;
    private String contactPersonPosition;
    private String adminNote;
    private ApprovalStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
