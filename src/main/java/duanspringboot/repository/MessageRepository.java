package duanspringboot.repository;

import duanspringboot.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // Get conversation between two users (ordered by time)
    @Query("SELECT m FROM Message m WHERE " +
           "(m.sender.id = :userId1 AND m.receiver.id = :userId2) OR " +
           "(m.sender.id = :userId2 AND m.receiver.id = :userId1) " +
           "ORDER BY m.sentAt ASC")
    List<Message> findConversation(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    // Get all conversations for a user (distinct users they messaged with)
    @Query("SELECT m FROM Message m WHERE m.id IN " +
           "(SELECT MAX(m2.id) FROM Message m2 WHERE m2.sender.id = :userId OR m2.receiver.id = :userId GROUP BY " +
           "CASE WHEN m2.sender.id = :userId THEN m2.receiver.id ELSE m2.sender.id END) " +
           "ORDER BY m.sentAt DESC")
    List<Message> findConversationsByUser(@Param("userId") Long userId);

    // Count unread messages for a user from a specific sender
    int countByReceiverIdAndSenderIdAndIsReadFalse(Long receiverId, Long senderId);

    // Count all unread messages for a user
    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiver.id = :userId AND m.isRead = false")
    long countUnreadByUser(@Param("userId") Long userId);

    // Mark messages as read
    @Query("UPDATE Message m SET m.isRead = true WHERE m.receiver.id = :receiverId AND m.sender.id = :senderId AND m.isRead = false")
    void markAsRead(@Param("receiverId") Long receiverId, @Param("senderId") Long senderId);
}