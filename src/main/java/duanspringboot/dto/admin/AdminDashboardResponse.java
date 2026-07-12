package duanspringboot.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDashboardResponse {
    private long totalUsers;
    private long totalCandidates;
    private long totalRecruiters;
    private long totalCompanies;
    private long totalJobPostings;
    private long totalApplications;
    private long totalInterviews;
    private long totalBlogs;
}
