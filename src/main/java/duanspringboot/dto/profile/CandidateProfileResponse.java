package duanspringboot.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateProfileResponse {
    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private String headline;
    private String summary;
    private String address;
    private String avatarUrl;
    private String resumeUrl;
}
