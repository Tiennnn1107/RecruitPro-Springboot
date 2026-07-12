package duanspringboot.dto.candidate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateSkillResponse {
    private Long id;
    private Long candidateId;
    private String skillName;
}
