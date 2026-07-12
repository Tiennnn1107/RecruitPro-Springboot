package duanspringboot.dto.jobalert;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobAlertRequest {
    private String keywords;
    private String location;
    private Integer minSalary;
    private Long jobFieldId;
    private Boolean isActive;
}