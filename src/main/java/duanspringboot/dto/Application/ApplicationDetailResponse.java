package duanspringboot.dto.Application;

import lombok.Data;
import lombok.Builder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ApplicationDetailResponse {
    private Long id;
    private Long candidateId;
    private String candidateName;
    private String candidateEmail;
    private String candidatePhone;
    private String candidateHeadline;
    private String candidateAvatarUrl;
    private String candidateAddress;
    private String candidateSummary;
    
    private Long jobId;
    private String jobTitle;
    private String companyName;
    
    private String coverLetter;
    private String resumeUrl;
    private String status;
    private LocalDateTime appliedAt;
    private boolean hasInterview;
    private int daysSinceApplied;
    
    private List<SkillDto> skills;
    private List<ExperienceDto> experiences;
    
    @Data
    @Builder
    public static class SkillDto {
        private Long id;
        private String skillName;
    }
    
    @Data
    @Builder
    public static class ExperienceDto {
        private Long id;
        private String companyName;
        private String position;
        private LocalDate startDate;
        private LocalDate endDate;
    }
}