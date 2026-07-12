package duanspringboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import duanspringboot.entity.MyCV;

@Repository
public interface MyCVRepository extends JpaRepository<MyCV, Long> {
    List<MyCV> findByCandidate_IdAndIsActiveTrue(Long candidateId);

    List<MyCV> findByCandidate_Id(Long candidateId);
}