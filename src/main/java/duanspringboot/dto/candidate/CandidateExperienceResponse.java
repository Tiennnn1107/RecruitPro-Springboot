package duanspringboot.dto.candidate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateExperienceResponse {
    private Long id;
    private Long candidateId;
    private String companyName;
    private String position;
    private LocalDate startDate;
    private LocalDate endDate;
}
