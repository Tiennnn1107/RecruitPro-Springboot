package duanspringboot.service;

import duanspringboot.dto.job.JobPostingResponse;
import duanspringboot.entity.Application;
import duanspringboot.entity.Company;
import duanspringboot.entity.JobPosting;
import duanspringboot.entity.User;
import duanspringboot.enums.JobStatus;
import duanspringboot.repository.CompanyRepository;
import duanspringboot.repository.JobPostingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JobPostingServiceTest {

    @Mock
    private JobPostingRepository jobPostingRepository;

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private JobPostingService jobPostingService;

    private JobPosting mockJob;
    private Company mockCompany;

    @BeforeEach
    void setUp() {
        User mockUser = new User();
        mockUser.setId(10L);
        mockUser.setEmail("recruiter@test.com");

        mockCompany = new Company();
        mockCompany.setId(1L);
        mockCompany.setName("Tech Corp");
        mockCompany.setUser(mockUser);

        mockJob = new JobPosting();
        mockJob.setId(100L);
        mockJob.setTitle("Java Developer");
        mockJob.setCompany(mockCompany);
        mockJob.setStatus(JobStatus.OPEN);
        
        // Add 2 mock applications
        List<Application> applications = new ArrayList<>();
        applications.add(new Application());
        applications.add(new Application());
        
        mockJob.setApplications(applications);
    }

    @Test
    void testGetById_ShouldReturnCorrectApplicationCount() {
        // Arrange
        when(jobPostingRepository.findById(100L)).thenReturn(Optional.of(mockJob));

        // Act
        JobPostingResponse response = jobPostingService.getById(100L);

        // Assert
        assertEquals(100L, response.getId());
        assertEquals("Tech Corp", response.getCompanyName());
        assertEquals(2, response.getApplicationCount(), "Application count should be 2");
    }
}
