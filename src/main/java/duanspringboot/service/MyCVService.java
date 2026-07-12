package duanspringboot.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import duanspringboot.entity.MyCV;
import duanspringboot.entity.CandidateProfile;
import duanspringboot.repository.MyCVRepository;
import duanspringboot.repository.CandidateProfileRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyCVService {
    private final MyCVRepository myCVRepository;
    private final CandidateProfileRepository candidateProfileRepository;

    @Transactional
    public MyCV addMyCV(Long candidateId, String cvTitle, String cvUrl, String fileName, String fileType,
            Long fileSize) {
        CandidateProfile candidate = candidateProfileRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ ứng viên"));

        // If this is set as default, unset other defaults first
        MyCV newCV = MyCV.builder()
                .candidate(candidate)
                .cvTitle(cvTitle)
                .cvUrl(cvUrl)
                .fileName(fileName)
                .fileType(fileType)
                .fileSize(fileSize)
                .isDefault(false)
                .isActive(true)
                .build();

        return myCVRepository.save(newCV);
    }

    public List<MyCV> getMyCVsByCandidateId(Long candidateId) {
        return myCVRepository.findByCandidate_IdAndIsActiveTrue(candidateId);
    }

    @Transactional
    public MyCV updateMyCV(Long cvId, String cvTitle, String cvUrl, String fileName, String fileType, Long fileSize) {
        MyCV myCV = myCVRepository.findById(cvId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy CV"));

        if (cvTitle != null)
            myCV.setCvTitle(cvTitle);
        if (cvUrl != null)
            myCV.setCvUrl(cvUrl);
        if (fileName != null)
            myCV.setFileName(fileName);
        if (fileType != null)
            myCV.setFileType(fileType);
        if (fileSize != null)
            myCV.setFileSize(fileSize);

        return myCVRepository.save(myCV);
    }

    @Transactional
    public void deleteMyCV(Long cvId) {
        MyCV myCV = myCVRepository.findById(cvId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy CV"));
        myCV.setIsActive(false);
        myCVRepository.save(myCV);
    }

    @Transactional
    public MyCV setDefaultCV(Long cvId) {
        MyCV myCV = myCVRepository.findById(cvId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy CV"));

        // Unset all other defaults for this candidate
        List<MyCV> allCVs = myCVRepository.findByCandidate_Id(myCV.getCandidate().getId());
        for (MyCV cv : allCVs) {
            if (cv.getIsDefault()) {
                cv.setIsDefault(false);
                myCVRepository.save(cv);
            }
        }

        // Set this one as default
        myCV.setIsDefault(true);
        return myCVRepository.save(myCV);
    }
}