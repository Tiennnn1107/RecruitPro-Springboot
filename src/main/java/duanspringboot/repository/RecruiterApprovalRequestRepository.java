package duanspringboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import duanspringboot.entity.RecruiterApprovalRequest;
import duanspringboot.enums.ApprovalStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecruiterApprovalRequestRepository extends JpaRepository<RecruiterApprovalRequest, Long> {
    Optional<RecruiterApprovalRequest> findByUserId(Long userId);
    List<RecruiterApprovalRequest> findByStatus(ApprovalStatus status);
    List<RecruiterApprovalRequest> findAllByOrderByCreatedAtDesc();
    List<RecruiterApprovalRequest> findByStatusOrderByCreatedAtDesc(ApprovalStatus status);
}
