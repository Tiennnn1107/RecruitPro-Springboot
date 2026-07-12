package duanspringboot.dto.blog;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogRequest {

    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    private String summary;

    @NotBlank(message = "Nội dung không được để trống")
    private String content;

    private String imageUrl;
}
