package duanspringboot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import duanspringboot.entity.JobField;

@Repository
public interface JobFieldRepository extends JpaRepository<JobField, Long> {
    
    List<JobField> findAllByOrderByNameAsc();
    
    @Query("SELECT jf FROM JobField jf ORDER BY jf.name ASC")
    List<JobField> findAllJobFieldsSorted();
}
