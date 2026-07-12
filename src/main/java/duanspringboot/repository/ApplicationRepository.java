package duanspringboot.repository;

import duanspringboot.entity.Application;
import duanspringboot.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByCandidateId(Long candidateId);
    List<Application> findByJobPostingId(Long jobPostingId);
    boolean existsByCandidateIdAndJobPostingId(Long candidateId, Long jobPostingId);

    // Pipeline: Lấy tất cả applications của recruiter (qua company -> job postings)
    @Query("SELECT a FROM Application a WHERE a.jobPosting.company.user.id = :userId ORDER BY a.appliedAt DESC")
    List<Application> findByRecruiterUserId(@Param("userId") Long userId);

    // Pipeline: Lấy applications theo recruiter và status
    @Query("SELECT a FROM Application a WHERE a.jobPosting.company.user.id = :userId AND a.status = :status ORDER BY a.appliedAt DESC")
    List<Application> findByRecruiterUserIdAndStatus(@Param("userId") Long userId, @Param("status") ApplicationStatus status);

    // Pipeline: Lấy applications cho final column (OFFERED hoặc REJECTED)
    @Query("SELECT a FROM Application a WHERE a.jobPosting.company.user.id = :userId AND a.status IN (duanspringboot.enums.ApplicationStatus.OFFERED, duanspringboot.enums.ApplicationStatus.REJECTED) ORDER BY a.appliedAt DESC")
    List<Application> findByRecruiterUserIdAndFinalStatus(@Param("userId") Long userId);
}
