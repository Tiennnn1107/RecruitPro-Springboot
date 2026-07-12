package duanspringboot.repository;

import duanspringboot.entity.JobAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobAlertRepository extends JpaRepository<JobAlert, Long> {
    List<JobAlert> findByUserId(Long userId);
    List<JobAlert> findByIsActiveTrue();
    List<JobAlert> findByUserIdAndIsActiveTrue(Long userId);
}