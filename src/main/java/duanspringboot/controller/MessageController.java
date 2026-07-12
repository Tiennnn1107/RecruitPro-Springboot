package duanspringboot.controller;

import duanspringboot.dto.message.ConversationResponse;
import duanspringboot.dto.message.MessageRequest;
import duanspringboot.dto.message.MessageResponse;
import duanspringboot.security.CustomUserDetails;
import duanspringboot.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationResponse>> getConversations(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(messageService.getConversations(userId));
    }

    @GetMapping("/conversation/{otherUserId}")
    public ResponseEntity<List<MessageResponse>> getConversation(
            @PathVariable Long otherUserId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(messageService.getConversation(userId, otherUserId));
    }

    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(
            @RequestBody MessageRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(messageService.sendMessage(userId, request));
    }

    @PatchMapping("/read/{senderId}")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long senderId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        messageService.markAsRead(userId, senderId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(messageService.getUnreadCount(userId));
    }

    private Long getUserId(UserDetails userDetails) {
        return ((CustomUserDetails) userDetails).getId();
    }
}