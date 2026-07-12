package duanspringboot.service;

import duanspringboot.dto.message.ConversationResponse;
import duanspringboot.dto.message.MessageRequest;
import duanspringboot.dto.message.MessageResponse;
import duanspringboot.entity.Message;
import duanspringboot.entity.User;
import duanspringboot.repository.MessageRepository;
import duanspringboot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public MessageResponse sendMessage(Long senderId, MessageRequest request) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người gửi"));
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người nhận"));

        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .content(request.getContent())
                .isRead(false)
                .build();

        message = messageRepository.save(message);

        // Send notification to receiver
        String notifTitle = "Tin nhắn mới từ " + sender.getEmail();
        String notifMsg = request.getContent().length() > 100
                ? request.getContent().substring(0, 100) + "..."
                : request.getContent();
        notificationService.createNotification(receiver.getId(), notifTitle, notifMsg);

        return mapToResponse(message);
    }

    public List<MessageResponse> getConversation(Long userId, Long otherUserId) {
        return messageRepository.findConversation(userId, otherUserId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ConversationResponse> getConversations(Long userId) {
        List<Message> lastMessages = messageRepository.findConversationsByUser(userId);

        return lastMessages.stream().map(msg -> {
            // Determine the other user in conversation
            Long otherUserId = msg.getSender().getId().equals(userId)
                    ? msg.getReceiver().getId()
                    : msg.getSender().getId();

            User otherUser = msg.getSender().getId().equals(userId)
                    ? msg.getReceiver() : msg.getSender();

            // Count unread messages from this conversation
            long unreadCount = messageRepository.countByReceiverIdAndSenderIdAndIsReadFalse(userId, otherUserId);

            return ConversationResponse.builder()
                    .userId(otherUserId)
                    .userEmail(otherUser.getEmail())
                    .userName(getUserDisplayName(otherUser))
                    .lastMessage(msg.getContent())
                    .lastMessageAt(msg.getSentAt())
                    .unreadCount(unreadCount)
                    .build();
        }).collect(Collectors.toList());
    }

    @Transactional
    public void markAsRead(Long userId, Long senderId) {
        messageRepository.markAsRead(userId, senderId);
    }

    public long getUnreadCount(Long userId) {
        return messageRepository.countUnreadByUser(userId);
    }

    private MessageResponse mapToResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .senderId(message.getSender().getId())
                .senderEmail(message.getSender().getEmail())
                .senderName(getUserDisplayName(message.getSender()))
                .receiverId(message.getReceiver().getId())
                .receiverEmail(message.getReceiver().getEmail())
                .receiverName(getUserDisplayName(message.getReceiver()))
                .content(message.getContent())
                .isRead(message.getIsRead())
                .sentAt(message.getSentAt())
                .build();
    }

    private String getUserDisplayName(User user) {
        // Try to get name from candidate profile or company
        if (user.getCandidateProfile() != null
                && user.getCandidateProfile().getFullName() != null) {
            return user.getCandidateProfile().getFullName();
        }
        if (user.getCompany() != null
                && user.getCompany().getName() != null) {
            return user.getCompany().getName();
        }
        return user.getEmail();
    }
}