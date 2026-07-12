package duanspringboot.service;

import duanspringboot.dto.candidate.CandidateExperienceRequest;
import duanspringboot.dto.candidate.CandidateExperienceResponse;
import duanspringboot.entity.CandidateExperience;
import duanspringboot.entity.CandidateProfile;
import duanspringboot.repository.CandidateExperienceRepository;
import duanspringboot.repository.CandidateProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidateExperienceService {

    private final CandidateExperienceRepository experienceRepository;
    private final CandidateProfileRepository profileRepository;

    @Transactional
    public CandidateExperienceResponse create(CandidateExperienceRequest request, Long userId) {
        CandidateProfile candidate = profileRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ ứng viên"));

        CandidateExperience experience = CandidateExperience.builder()
                .candidate(candidate)
                .companyName(request.getCompanyName())
                .position(request.getPosition())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        return mapToResponse(experienceRepository.save(experience));
    }

    public List<CandidateExperienceResponse> getByCandidateId(Long candidateId) {
        return experienceRepository.findByCandidateIdOrderByStartDateDesc(candidateId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<CandidateExperienceResponse> getMyExperiences(Long userId) {
        CandidateProfile candidate = profileRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ ứng viên"));
        return getByCandidateId(candidate.getId());
    }

    @Transactional
    public CandidateExperienceResponse update(Long id, CandidateExperienceRequest request, Long userId) {
        CandidateExperience experience = experienceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kinh nghiệm làm việc"));

        CandidateProfile candidate = profileRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ ứng viên"));

        if (!experience.getCandidate().getId().equals(candidate.getId())) {
            throw new RuntimeException("Bạn không có quyền cập nhật kinh nghiệm này");
        }

        experience.setCompanyName(request.getCompanyName());
        experience.setPosition(request.getPosition());
        experience.setStartDate(request.getStartDate());
        experience.setEndDate(request.getEndDate());

        return mapToResponse(experienceRepository.save(experience));
    }

    @Transactional
    public void delete(Long id, Long userId) {
        CandidateExperience experience = experienceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kinh nghiệm làm việc"));

        CandidateProfile candidate = profileRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ ứng viên"));

        if (!experience.getCandidate().getId().equals(candidate.getId())) {
            throw new RuntimeException("Bạn không có quyền xóa kinh nghiệm này");
        }

        experienceRepository.delete(experience);
    }

    private CandidateExperienceResponse mapToResponse(CandidateExperience experience) {
        return CandidateExperienceResponse.builder()
                .id(experience.getId())
                .candidateId(experience.getCandidate().getId())
                .companyName(experience.getCompanyName())
                .position(experience.getPosition())
                .startDate(experience.getStartDate())
                .endDate(experience.getEndDate())
                .build();
    }
}
