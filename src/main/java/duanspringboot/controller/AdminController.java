package duanspringboot.controller;

import duanspringboot.dto.admin.AdminDashboardResponse;
import duanspringboot.dto.admin.CompanyResponse;
import duanspringboot.dto.admin.JobPostingResponse;
import duanspringboot.dto.admin.UserResponse;
import duanspringboot.dto.blog.BlogRequest;
import duanspringboot.dto.blog.BlogResponse;
import duanspringboot.security.CustomUserDetails;
import duanspringboot.service.AdminService;
import duanspringboot.service.BlogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final BlogService blogService;

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardResponse> getDashboardStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    // === User Management ===
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getUserById(id));
    }

    @GetMapping("/users/search")
    public ResponseEntity<List<UserResponse>> searchUsers(@RequestParam String keyword) {
        return ResponseEntity.ok(adminService.searchUsers(keyword));
    }

    @PatchMapping("/users/{id}/toggle-status")
    public ResponseEntity<UserResponse> toggleUserStatus(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.toggleUserStatus(id));
    }

    @PatchMapping("/users/{id}/role")
    public ResponseEntity<UserResponse> updateUserRole(@PathVariable Long id, @RequestParam String role) {
        return ResponseEntity.ok(adminService.updateUserRole(id, role));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // === Job Posting Management ===
    @GetMapping("/jobs")
    public ResponseEntity<List<JobPostingResponse>> getAllJobPostings() {
        return ResponseEntity.ok(adminService.getAllJobPostings());
    }

    @GetMapping("/jobs/{id}")
    public ResponseEntity<JobPostingResponse> getJobPostingById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getJobPostingById(id));
    }

    @PatchMapping("/jobs/{id}/status")
    public ResponseEntity<JobPostingResponse> updateJobStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(adminService.updateJobStatus(id, status));
    }

    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<Void> deleteJobPosting(@PathVariable Long id) {
        adminService.deleteJobPosting(id);
        return ResponseEntity.noContent().build();
    }

    // === Company Management ===
    @GetMapping("/companies")
    public ResponseEntity<List<CompanyResponse>> getAllCompanies() {
        return ResponseEntity.ok(adminService.getAllCompanies());
    }

    @GetMapping("/companies/{id}")
    public ResponseEntity<CompanyResponse> getCompanyById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getCompanyById(id));
    }

    @GetMapping("/companies/search")
    public ResponseEntity<List<CompanyResponse>> searchCompanies(@RequestParam String keyword) {
        return ResponseEntity.ok(adminService.searchCompanies(keyword));
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        adminService.deleteCompany(id);
        return ResponseEntity.noContent().build();
    }

    // === Blog Management ===
    @GetMapping("/blogs")
    public ResponseEntity<List<BlogResponse>> getAllBlogs() {
        return ResponseEntity.ok(blogService.getAll());
    }

    @GetMapping("/blogs/{id}")
    public ResponseEntity<BlogResponse> getBlogById(@PathVariable Long id) {
        return ResponseEntity.ok(blogService.getById(id));
    }

    @PostMapping("/blogs")
    public ResponseEntity<BlogResponse> createBlog(
            @Valid @RequestBody BlogRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(blogService.create(request, userDetails.getId()));
    }

    @PutMapping("/blogs/{id}")
    public ResponseEntity<BlogResponse> updateBlog(
            @PathVariable Long id,
            @Valid @RequestBody BlogRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(blogService.update(id, request, userDetails.getId()));
    }

    @DeleteMapping("/blogs/{id}")
    public ResponseEntity<Void> deleteBlog(@PathVariable Long id) {
        blogService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
