package duanspringboot.dto.Application;

import duanspringboot.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationStatusRequest {
    @NotNull(message = "Trạng thái không được để trống")
    private ApplicationStatus status;
}
