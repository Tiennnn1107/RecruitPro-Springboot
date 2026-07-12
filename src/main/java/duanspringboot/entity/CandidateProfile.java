package duanspringboot.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "candidate_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "full_name")
    private String fullName;

    private String headline;

    private String phone;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "resume_url")
    private String resumeUrl;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    private String gender;
    private String address;

    @Column(columnDefinition = "TEXT")
    private String summary;

    // Relationships
    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CandidateSkill> skills;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CandidateExperience> experiences;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    private List<Application> applications;
}
