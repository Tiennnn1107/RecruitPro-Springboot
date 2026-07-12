package duanspringboot.dto.blog;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogResponse {
    private Long id;
    private String title;
    private String summary;
    private String content;
    private String imageUrl;
    private String authorName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
