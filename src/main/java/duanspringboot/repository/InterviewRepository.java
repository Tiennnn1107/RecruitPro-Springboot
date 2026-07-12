package duanspringboot.repository;

import duanspringboot.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface InterviewRepository extends JpaRepository<Interview, Long> {
    List<Interview> findByApplicationId(Long applicationId);
    // Tìm các cuộc phỏng vấn liên quan đến HR cụ thể (thông qua Application -> JobPosting -> Company -> User)
    List<Interview> findByApplicationJobPostingCompanyUserId(Long userId);

    // Lấy tất cả application IDs đã có lịch phỏng vấn của một recruiter
    @Query("SELECT DISTINCT i.application.id FROM Interview i WHERE i.application.jobPosting.company.user.id = :userId")
    Set<Long> findApplicationIdsWithInterviewByRecruiter(@Param("userId") Long userId);
}
