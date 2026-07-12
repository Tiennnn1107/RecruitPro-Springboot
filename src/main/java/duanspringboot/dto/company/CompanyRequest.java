package duanspringboot.dto.company;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyRequest {
    @NotBlank(message = "Tên công ty không được để trống")
    private String name;
    
    private String website;
    private String industry;
    private String companySize;
    private String description;
}
