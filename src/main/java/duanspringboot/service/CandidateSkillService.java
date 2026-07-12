package duanspringboot.service;

import duanspringboot.dto.candidate.CandidateSkillRequest;
import duanspringboot.dto.candidate.CandidateSkillResponse;
import duanspringboot.entity.CandidateProfile;
import duanspringboot.entity.CandidateSkill;
import duanspringboot.repository.CandidateProfileRepository;
import duanspringboot.repository.CandidateSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidateSkillService {

    private final CandidateSkillRepository skillRepository;
    private final CandidateProfileRepository profileRepository;

    @Transactional
    public CandidateSkillResponse addSkill(CandidateSkillRequest request, Long userId) {
        CandidateProfile candidate = profileRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ ứng viên"));

        if (skillRepository.existsByCandidateIdAndSkillName(candidate.getId(), request.getSkillName())) {
            throw new RuntimeException("Kỹ năng này đã tồn tại trong hồ sơ của bạn");
        }

        CandidateSkill skill = CandidateSkill.builder()
                .candidate(candidate)
                .skillName(request.getSkillName())
                .build();

        return mapToResponse(skillRepository.save(skill));
    }

    public List<CandidateSkillResponse> getMySkills(Long userId) {
        CandidateProfile candidate = profileRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ ứng viên"));
        return skillRepository.findByCandidateId(candidate.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteSkill(Long id, Long userId) {
        CandidateSkill skill = skillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kỹ năng"));

        CandidateProfile candidate = profileRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ ứng viên"));

        if (!skill.getCandidate().getId().equals(candidate.getId())) {
            throw new RuntimeException("Bạn không có quyền xóa kỹ năng này");
        }

        skillRepository.delete(skill);
    }

    private CandidateSkillResponse mapToResponse(CandidateSkill skill) {
        return CandidateSkillResponse.builder()
                .id(skill.getId())
                .candidateId(skill.getCandidate().getId())
                .skillName(skill.getSkillName())
                .build();
    }
}
