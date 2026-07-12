package duanspringboot.dto.message;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationResponse {
    private Long userId;
    private String userEmail;
    private String userName;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private long unreadCount;
}