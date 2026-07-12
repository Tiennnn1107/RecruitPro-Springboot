package duanspringboot.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "mycvs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyCV {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    private CandidateProfile candidate;

    @Column(name = "cv_title", nullable = false)
    private String cvTitle;

    @Column(name = "cv_url", nullable = false)
    private String cvUrl;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_type")
    private String fileType; // PDF, DOC, DOCX

    @Column(name = "file_size")
    private Long fileSize;

    @Builder.Default
    @Column(name = "is_default")
    private Boolean isDefault = false;

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