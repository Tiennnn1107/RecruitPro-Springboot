package duanspringboot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeStatsResponse {
    private long totalJobs;
    private long totalCompanies;
    private long totalCandidates;
}
