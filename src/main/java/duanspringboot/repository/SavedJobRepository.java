package duanspringboot.repository;

import duanspringboot.entity.SavedJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {
    List<SavedJob> findByUserId(Long userId);
    Optional<SavedJob> findByUserIdAndJobPostingId(Long userId, Long jobPostingId);
    boolean existsByUserIdAndJobPostingId(Long userId, Long jobPostingId);
    void deleteByUserIdAndJobPostingId(Long userId, Long jobPostingId);
}