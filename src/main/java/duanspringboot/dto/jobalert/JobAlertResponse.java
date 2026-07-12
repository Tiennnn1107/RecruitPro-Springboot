package duanspringboot.dto.jobalert;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobAlertResponse {
    private Long id;
    private String keywords;
    private String location;
    private Integer minSalary;
    private Long jobFieldId;
    private String jobFieldName;
    private Boolean isActive;
    private LocalDateTime createdAt;
}