package duanspringboot.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "linkcvs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LinkCV {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    private CandidateProfile candidate;

    @Column(name = "link_title")
    private String linkTitle;

    @Column(name = "link_url", nullable = false)
    private String linkUrl;

    @Column(name = "link_type")
    private String linkType; // LINKEDIN, GITHUB, PORTFOLIO, OTHER

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}