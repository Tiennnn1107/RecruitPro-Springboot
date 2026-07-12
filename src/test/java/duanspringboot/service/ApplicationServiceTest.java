package duanspringboot.service;

import duanspringboot.dto.Application.ApplicationResponse;
import duanspringboot.dto.Application.ApplicationStatusRequest;
import duanspringboot.entity.Application;
import duanspringboot.entity.CandidateProfile;
import duanspringboot.entity.Company;
import duanspringboot.entity.JobPosting;
import duanspringboot.entity.User;
import duanspringboot.enums.ApplicationStatus;
import duanspringboot.repository.ApplicationRepository;
import duanspringboot.repository.CandidateExperienceRepository;
import duanspringboot.repository.CandidateProfileRepository;
import duanspringboot.repository.CandidateSkillRepository;
import duanspringboot.repository.InterviewRepository;
import duanspringboot.repository.JobPostingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private JobPostingRepository jobPostingRepository;

    @Mock
    private CandidateProfileRepository profileRepository;

    @Mock
    private CandidateSkillRepository skillRepository;

    @Mock
    private CandidateExperienceRepository experienceRepository;

    @Mock
    private InterviewRepository interviewRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ApplicationService applicationService;

    private Application mockApplication;
    private User mockRecruiterUser;

    @BeforeEach
    void setUp() {
        User mockCandidateUser = new User();
        mockCandidateUser.setId(20L);
        mockCandidateUser.setEmail("candidate@test.com");

        mockRecruiterUser = new User();
        mockRecruiterUser.setId(10L);
        mockRecruiterUser.setEmail("recruiter@test.com");

        Company mockCompany = new Company();
        mockCompany.setId(1L);
        mockCompany.setName("Tech Viet");
        mockCompany.setUser(mockRecruiterUser);

        JobPosting mockJob = new JobPosting();
        mockJob.setId(100L);
        mockJob.setTitle("Backend Engineer");
        mockJob.setCompany(mockCompany);

        CandidateProfile mockCandidate = new CandidateProfile();
        mockCandidate.setId(5L);
        mockCandidate.setFullName("Nguyen Van A");
        mockCandidate.setUser(mockCandidateUser);

        mockApplication = new Application();
        mockApplication.setId(1000L);
        mockApplication.setJobPosting(mockJob);
        mockApplication.setCandidate(mockCandidate);
        mockApplication.setStatus(ApplicationStatus.APPLIED);
        mockApplication.setAppliedAt(LocalDateTime.now().minusDays(1));
    }

    @Test
    void testChangeStatus_Success() {
        // Arrange
        ApplicationStatusRequest request = new ApplicationStatusRequest();
        request.setStatus(ApplicationStatus.SHORTLISTED);

        when(applicationRepository.findById(1000L)).thenReturn(Optional.of(mockApplication));
        when(applicationRepository.save(any(Application.class))).thenReturn(mockApplication);

        // Act
        ApplicationResponse response = applicationService.changeStatus(1000L, request, 10L); // 10L is valid Recruiter ID

        // Assert
        assertEquals(ApplicationStatus.SHORTLISTED, response.getStatus());
        assertEquals(ApplicationStatus.SHORTLISTED, mockApplication.getStatus());
        verifyNoInteractions(emailService);
    }

    @Test
    void testChangeStatus_ToOffered_SendsApprovedEmail() {
        // Arrange
        ApplicationStatusRequest request = new ApplicationStatusRequest();
        request.setStatus(ApplicationStatus.OFFERED);

        when(applicationRepository.findById(1000L)).thenReturn(Optional.of(mockApplication));
        when(applicationRepository.save(any(Application.class))).thenReturn(mockApplication);

        // Act
        ApplicationResponse response = applicationService.changeStatus(1000L, request, 10L);

        // Assert
        assertEquals(ApplicationStatus.OFFERED, response.getStatus());
        verify(emailService).sendApplicationApprovedEmail(
                "candidate@test.com",
                "Nguyen Van A",
                "Backend Engineer",
                "Tech Viet"
        );
    }

    @Test
    void testChangeStatus_WrongRecruiter_ThrowsException() {
        // Arrange
        ApplicationStatusRequest request = new ApplicationStatusRequest();
        request.setStatus(ApplicationStatus.SHORTLISTED);

        when(applicationRepository.findById(1000L)).thenReturn(Optional.of(mockApplication));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            applicationService.changeStatus(1000L, request, 99L); // 99L is wrong Recruiter ID
        });
        
        assertEquals("Bạn không có quyền quản lý đơn ứng tuyển này", exception.getMessage());
    }
}
