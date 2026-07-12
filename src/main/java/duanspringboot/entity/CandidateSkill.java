package duanspringboot.entity;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "candidate_skills")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    private CandidateProfile candidate;

    @Column(name = "skill_name", nullable = false)
    private String skillName;
}
