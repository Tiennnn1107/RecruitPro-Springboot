package duanspringboot.service;

import duanspringboot.dto.recruiter.RecruiterApprovalRequestDTO;
import duanspringboot.dto.recruiter.RecruiterApprovalResponse;
import duanspringboot.dto.recruiter.RecruiterApprovalStatus;
import duanspringboot.entity.RecruiterApprovalRequest;
import duanspringboot.entity.User;
import duanspringboot.enums.ApprovalStatus;
import duanspringboot.enums.Role;
import duanspringboot.repository.RecruiterApprovalRequestRepository;
import duanspringboot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecruiterApprovalService {

    private final RecruiterApprovalRequestRepository approvalRequestRepository;
    private final UserRepository userRepository;

    @Transactional
    public RecruiterApprovalResponse createApprovalRequest(Long userId, RecruiterApprovalRequestDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        if (user.getRole() != Role.RECRUITER) {
            throw new RuntimeException("Chỉ tài khoản RECRUITER mới cần phê duyệt");
        }

        RecruiterApprovalRequest request = RecruiterApprovalRequest.builder()
                .user(user)
                .companyName(dto.getCompanyName())
                .taxCode(dto.getTaxCode())
                .businessLicenseUrl(dto.getBusinessLicenseUrl())
                .companyAddress(dto.getCompanyAddress())
                .companyWebsite(dto.getCompanyWebsite())
                .companyPhone(dto.getCompanyPhone())
                .contactPersonName(dto.getContactPersonName())
                .contactPersonPhone(dto.getContactPersonPhone())
                .contactPersonPosition(dto.getContactPersonPosition())
                .status(ApprovalStatus.PENDING)
                .build();

        RecruiterApprovalRequest saved = approvalRequestRepository.save(request);
        return mapToResponse(saved);
    }

    public RecruiterApprovalStatus getApprovalStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        if (user.getRole() != Role.RECRUITER) {
            return RecruiterApprovalStatus.builder()
                    .hasSubmitted(true)
                    .canPostJobs(true)
                    .message("Tài khoản không phải nhà tuyển dụng")
                    .build();
        }

        RecruiterApprovalRequest request = approvalRequestRepository.findByUserId(userId).orElse(null);

        if (request == null) {
            return RecruiterApprovalStatus.builder()
                    .hasSubmitted(false)
                    .canPostJobs(false)
                    .status("NOT_SUBMITTED")
                    .message("Vui lòng gửi thông tin công ty để được phê duyệt")
                    .build();
        }

        boolean canPost = request.getStatus() == ApprovalStatus.APPROVED;
        String message = switch (request.getStatus()) {
            case PENDING -> "Tài khoản đang chờ phê duyệt. Vui lòng chờ quản trị viên xem xét.";
            case APPROVED -> "Tài khoản đã được phê duyệt. Bạn có thể sử dụng đầy đủ chức năng.";
            case REJECTED -> "Tài khoản đã bị từ chối. Vui lòng liên hệ quản trị viên.";
        };

        return RecruiterApprovalStatus.builder()
                .hasSubmitted(true)
                .canPostJobs(canPost)
                .status(request.getStatus().name())
                .message(message)
                .build();
    }

    public List<RecruiterApprovalResponse> getAllApprovalRequests() {
        return approvalRequestRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<RecruiterApprovalResponse> getPendingApprovalRequests() {
        return approvalRequestRepository.findByStatusOrderByCreatedAtDesc(ApprovalStatus.PENDING)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public RecruiterApprovalResponse getApprovalRequestByUserId(Long userId) {
        return approvalRequestRepository.findByUserId(userId)
                .map(this::mapToResponse)
                .orElse(null);
    }

    @Transactional
    public RecruiterApprovalResponse approveRequest(Long requestId, String adminNote) {
        RecruiterApprovalRequest request = approvalRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu phê duyệt"));

        request.setStatus(ApprovalStatus.APPROVED);
        request.setAdminNote(adminNote);

        RecruiterApprovalRequest saved = approvalRequestRepository.save(request);
        return mapToResponse(saved);
    }

    @Transactional
    public RecruiterApprovalResponse rejectRequest(Long requestId, String adminNote) {
        RecruiterApprovalRequest request = approvalRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu phê duyệt"));

        request.setStatus(ApprovalStatus.REJECTED);
        request.setAdminNote(adminNote);

        RecruiterApprovalRequest saved = approvalRequestRepository.save(request);
        return mapToResponse(saved);
    }

    private RecruiterApprovalResponse mapToResponse(RecruiterApprovalRequest request) {
        return RecruiterApprovalResponse.builder()
                .id(request.getId())
                .userId(request.getUser().getId())
                .userEmail(request.getUser().getEmail())
                .companyName(request.getCompanyName())
                .taxCode(request.getTaxCode())
                .businessLicenseUrl(request.getBusinessLicenseUrl())
                .companyAddress(request.getCompanyAddress())
                .companyWebsite(request.getCompanyWebsite())
                .companyPhone(request.getCompanyPhone())
                .contactPersonName(request.getContactPersonName())
                .contactPersonPhone(request.getContactPersonPhone())
                .contactPersonPosition(request.getContactPersonPosition())
                .adminNote(request.getAdminNote())
                .status(request.getStatus())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .build();
    }
}
