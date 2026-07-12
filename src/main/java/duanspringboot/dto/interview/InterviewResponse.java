package duanspringboot.dto.interview;

import duanspringboot.enums.InterviewResult;
import duanspringboot.enums.InterviewType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewResponse {
    private Long id;
    private Long applicationId;
    private String candidateName;
    private String candidateEmail;
    private String jobTitle;
    private LocalDateTime scheduledAt;
    private InterviewType type;
    private String locationOrLink;
    private String interviewerNote;
    private InterviewResult result;
}
