package duanspringboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import duanspringboot.entity.LinkCV;

@Repository
public interface LinkCVRepository extends JpaRepository<LinkCV, Long> {
    List<LinkCV> findByCandidate_IdAndIsActiveTrue(Long candidateId);

    List<LinkCV> findByCandidate_Id(Long candidateId);
}