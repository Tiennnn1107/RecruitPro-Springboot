package duanspringboot.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import duanspringboot.entity.LinkCV;
import duanspringboot.entity.CandidateProfile;
import duanspringboot.repository.LinkCVRepository;
import duanspringboot.repository.CandidateProfileRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LinkCVService {
    private final LinkCVRepository linkCVRepository;
    private final CandidateProfileRepository candidateProfileRepository;

    @Transactional
    public LinkCV addLinkCV(Long candidateId, String linkTitle, String linkUrl, String linkType) {
        CandidateProfile candidate = candidateProfileRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ ứng viên"));

        LinkCV linkCV = LinkCV.builder()
                .candidate(candidate)
                .linkTitle(linkTitle)
                .linkUrl(linkUrl)
                .linkType(linkType)
                .isActive(true)
                .build();

        return linkCVRepository.save(linkCV);
    }

    public List<LinkCV> getLinkCVsByCandidateId(Long candidateId) {
        return linkCVRepository.findByCandidate_IdAndIsActiveTrue(candidateId);
    }

    @Transactional
    public LinkCV updateLinkCV(Long linkId, String linkTitle, String linkUrl, String linkType) {
        LinkCV linkCV = linkCVRepository.findById(linkId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy link CV"));

        if (linkTitle != null)
            linkCV.setLinkTitle(linkTitle);
        if (linkUrl != null)
            linkCV.setLinkUrl(linkUrl);
        if (linkType != null)
            linkCV.setLinkType(linkType);

        return linkCVRepository.save(linkCV);
    }

    @Transactional
    public void deleteLinkCV(Long linkId) {
        LinkCV linkCV = linkCVRepository.findById(linkId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy link CV"));
        linkCV.setIsActive(false);
        linkCVRepository.save(linkCV);
    }
}