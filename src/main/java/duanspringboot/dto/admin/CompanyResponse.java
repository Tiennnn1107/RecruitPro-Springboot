package duanspringboot.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CompanyResponse {
    private Long id;
    private Long userId;
    private String userEmail;
    private String name;
    private String logoUrl;
    private String website;
    private String industry;
    private String companySize;
    private String description;
    private LocalDateTime createdAt;
}
