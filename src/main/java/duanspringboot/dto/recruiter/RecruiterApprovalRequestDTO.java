package duanspringboot.dto.recruiter;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RecruiterApprovalRequestDTO {
    @NotBlank(message = "Tên công ty không được để trống")
    private String companyName;
    
    private String taxCode;
    
    private String businessLicenseUrl;
    
    private String companyAddress;
    
    private String companyWebsite;
    
    private String companyPhone;
    
    private String contactPersonName;
    
    private String contactPersonPhone;
    
    private String contactPersonPosition;
}
