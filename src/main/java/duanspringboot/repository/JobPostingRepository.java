package duanspringboot.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import duanspringboot.entity.JobPosting;
import duanspringboot.enums.JobStatus;

public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {
    List<JobPosting> findByCompanyId(Long companyId);
    List<JobPosting> findByCompanyIdAndStatus(Long companyId, JobStatus status);
    List<JobPosting> findByStatus(JobStatus status);

    @Query("SELECT j FROM JobPosting j WHERE j.status = :status " +
           "AND (:location IS NULL OR j.location LIKE %:location%) " +
           "AND (:minSalary IS NULL OR j.salaryMax >= :minSalary) " +
           "AND (:title IS NULL OR j.title LIKE %:title%) " +
           "AND (:fieldId IS NULL OR j.jobField.id = :fieldId)")
    List<JobPosting> filterJobs(
            @Param("status") JobStatus status,
            @Param("location") String location,
            @Param("minSalary") Integer minSalary,
            @Param("title") String title,
            @Param("fieldId") Long fieldId);

    // Pagination support for job search
    @Query("SELECT j FROM JobPosting j WHERE j.status = :status " +
           "AND (:location IS NULL OR j.location LIKE %:location%) " +
           "AND (:minSalary IS NULL OR j.salaryMax >= :minSalary) " +
           "AND (:title IS NULL OR j.title LIKE %:title%) " +
           "AND (:fieldId IS NULL OR j.jobField.id = :fieldId)")
    Page<JobPosting> filterJobsPaginated(
            @Param("status") JobStatus status,
            @Param("location") String location,
            @Param("minSalary") Integer minSalary,
            @Param("title") String title,
            @Param("fieldId") Long fieldId,
            Pageable pageable);

    // Pagination for company jobs
    Page<JobPosting> findByCompanyId(Long companyId, Pageable pageable);

    // Pagination for public job listing (homepage)
    Page<JobPosting> findByStatus(JobStatus status, Pageable pageable);

    // Count open jobs
    long countByStatus(JobStatus status);

    @Query("SELECT j FROM JobPosting j WHERE j.company.user.id = :userId ORDER BY j.createdAt DESC")
    List<JobPosting> findByRecruiterUserId(@Param("userId") Long userId);
}
