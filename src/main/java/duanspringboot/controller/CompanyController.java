package duanspringboot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import duanspringboot.dto.company.CompanyRequest;
import duanspringboot.dto.company.CompanyResponse;
import duanspringboot.security.CustomUserDetails;
import duanspringboot.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {
    private final CompanyService companyService;

    // 1. Tạo mới công ty (Chỉ cho Recruiter)
    @PostMapping
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<CompanyResponse> createCompany(
            @Valid @RequestBody CompanyRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        return ResponseEntity.ok(companyService.create(request, userId));
    }

    // 2. Lấy danh sách tất cả công ty (Công khai)
    @GetMapping
    public ResponseEntity<List<CompanyResponse>> getAll() {
        return ResponseEntity.ok(companyService.getAll());
    }

    // 3. Lấy thông tin công ty theo ID (Công khai)
    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.getById(id));
    }

    // 4. Lấy công ty của tôi (Cho Recruiter đăng nhập)
    @GetMapping("/my-company")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<CompanyResponse> getMyCompany(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        return ResponseEntity.ok(companyService.getMyCompany(userId));
    }

    // 5. Cập nhật thông tin công ty (Chỉ cho Recruiter là chủ sở hữu)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<CompanyResponse> updateCompany(
            @PathVariable Long id,
            @Valid @RequestBody CompanyRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        return ResponseEntity.ok(companyService.update(id, request, userId));
    }

    // 6. Xóa hồ sơ công ty
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<Void> deleteCompany(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        companyService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }
}
