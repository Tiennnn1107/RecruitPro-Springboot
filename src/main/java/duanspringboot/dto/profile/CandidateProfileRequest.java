package duanspringboot.dto.profile;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CandidateProfileRequest {
    private String fullName;
    private String headline;
    private String phone;
    private String avatarUrl;
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private String summary;
    private String resumeUrl;
}
