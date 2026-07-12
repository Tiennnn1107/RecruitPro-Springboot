package duanspringboot.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import duanspringboot.dto.profile.CandidateProfileRequest;
import duanspringboot.dto.profile.CandidateProfileResponse;
import duanspringboot.entity.CandidateProfile;
import duanspringboot.entity.User;
import duanspringboot.repository.CandidateProfileRepository;
import duanspringboot.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CandidateProfileService {
    private final CandidateProfileRepository profileRepository;
    private final UserRepository userRepository;

    // 1. Tạo hoặc Cập nhật Profile
    @Transactional
    public CandidateProfileResponse updateProfile(CandidateProfileRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        // Tìm profile cũ của User, nếu chưa có thì tạo mới (Gán quan hệ 1-1 với User)
        CandidateProfile profile = profileRepository.findByUser_Id(userId)
                .orElse(CandidateProfile.builder().user(user).build());

        // Cập nhật các trường thông tin (Nếu có trong request)
        if (request.getFullName() != null) profile.setFullName(request.getFullName());
        if (request.getHeadline() != null) profile.setHeadline(request.getHeadline());
        if (request.getPhone() != null) profile.setPhone(request.getPhone());
        if (request.getDateOfBirth() != null) profile.setDateOfBirth(request.getDateOfBirth());
        if (request.getGender() != null) profile.setGender(request.getGender());
        if (request.getAddress() != null) profile.setAddress(request.getAddress());
        if (request.getSummary() != null) profile.setSummary(request.getSummary());
        
        // Chỉ cập nhật URL nếu trong request có giá trị (tránh bị null đè lên)
        if (request.getResumeUrl() != null && !request.getResumeUrl().isEmpty()) {
            profile.setResumeUrl(request.getResumeUrl());
        }
        if (request.getAvatarUrl() != null && !request.getAvatarUrl().isEmpty()) {
            profile.setAvatarUrl(request.getAvatarUrl());
        }

        CandidateProfile savedProfile = profileRepository.save(profile);
        System.out.println("DEBUG: Profile ĐÃ LƯU vào DB: " + savedProfile);

        return mapToResponse(savedProfile, user.getEmail());
    }

    // 2. Lấy thông tin Profile của tôi
    public CandidateProfileResponse getMyProfile(Long userId) {
        CandidateProfile profile = profileRepository.findByUser_Id(userId)
                .orElse(null);
        
        if (profile == null) return null;
        
        return mapToResponse(profile, profile.getUser().getEmail());
    }

    private CandidateProfileResponse mapToResponse(CandidateProfile profile, String email) {
        return CandidateProfileResponse.builder()
                .id(profile.getId())
                .email(email)
                .fullName(profile.getFullName())
                .headline(profile.getHeadline())
                .phone(profile.getPhone())
                .summary(profile.getSummary())
                .address(profile.getAddress())
                .avatarUrl(profile.getAvatarUrl())
                .resumeUrl(profile.getResumeUrl())
                .build();
    }
}
