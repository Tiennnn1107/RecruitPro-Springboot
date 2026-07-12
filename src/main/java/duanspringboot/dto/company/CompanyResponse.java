package duanspringboot.dto.company;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyResponse {
    private Long id;
    private String name;
    private String website;
    private String industry;
    private String companySize;
    private String description;
    private String logoUrl;
}
