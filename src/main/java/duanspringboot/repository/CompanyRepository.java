package duanspringboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import duanspringboot.entity.Company;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByUserId(Long userId);
}
