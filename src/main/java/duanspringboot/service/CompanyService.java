package duanspringboot.service;

import org.springframework.stereotype.Service;
import duanspringboot.entity.Company;
import duanspringboot.entity.User;
import duanspringboot.dto.company.CompanyRequest;
import duanspringboot.dto.company.CompanyResponse;
import duanspringboot.repository.CompanyRepository;
import duanspringboot.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    // 1. Tạo mới công ty
    public CompanyResponse create(CompanyRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        if (companyRepository.findByUserId(userId).isPresent()) {
            throw new RuntimeException("Tài khoản này đã tạo hồ sơ công ty");
        }

        Company company = Company.builder()
                .user(user)
                .name(request.getName())
                .website(request.getWebsite())
                .industry(request.getIndustry())
                .companySize(request.getCompanySize())
                .description(request.getDescription())
                .build();

        return mapToResponse(companyRepository.save(company));
    }

    // 2. Lấy thông tin công ty theo ID (Công khai)
    public CompanyResponse getById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công ty"));
        return mapToResponse(company);
    }

    // 3. Lấy thông tin công ty của chính Recruiter đang đăng nhập
    public CompanyResponse getMyCompany(Long userId) {
        Company company = companyRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Bạn chưa tạo hồ sơ công ty"));
        return mapToResponse(company);
    }

    // 4. Lấy danh sách tất cả công ty
    public List<CompanyResponse> getAll() {
        return companyRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 5. Cập nhật thông tin công ty
    public CompanyResponse update(Long id, CompanyRequest request, Long userId) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công ty"));

        // Kiểm tra quyền: Chỉ chủ sở hữu mới được sửa
        if (!company.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền chỉnh sửa công ty này");
        }

        company.setName(request.getName());
        company.setWebsite(request.getWebsite());
        company.setIndustry(request.getIndustry());
        company.setCompanySize(request.getCompanySize());
        company.setDescription(request.getDescription());

        return mapToResponse(companyRepository.save(company));
    }

    // 6. Xóa hồ sơ công ty
    public void delete(Long id, Long userId) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công ty"));

        if (!company.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền xóa công ty này");
        }

        companyRepository.delete(company);
    }

    private CompanyResponse mapToResponse(Company company) {
        return CompanyResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .website(company.getWebsite())
                .industry(company.getIndustry())
                .companySize(company.getCompanySize())
                .description(company.getDescription())
                .logoUrl(company.getLogoUrl())
                .build();
    }
}
