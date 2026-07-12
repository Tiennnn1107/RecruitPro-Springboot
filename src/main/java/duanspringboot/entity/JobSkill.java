package duanspringboot.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "job_skills")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private JobPosting jobPosting;

    @Column(name = "skill_name", nullable = false)
    private String skillName;
}
