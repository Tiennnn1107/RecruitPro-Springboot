package duanspringboot.dto.interview;

import duanspringboot.enums.InterviewType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewRequest {
    @NotNull(message = "ID đơn ứng tuyển không được để trống")
    private Long applicationId;

    @NotNull(message = "Thời gian phỏng vấn không được để trống")
    private LocalDateTime scheduledAt;

    @NotNull(message = "Hình thức phỏng vấn không được để trống")
    private InterviewType type;

    @NotBlank(message = "Địa điểm hoặc link phỏng vấn không được để trống")
    private String locationOrLink;

    private String interviewerNote;
}
