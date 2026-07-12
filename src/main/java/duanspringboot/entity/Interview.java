package duanspringboot.entity;
import java.time.LocalDateTime;

import duanspringboot.enums.InterviewResult;
import duanspringboot.enums.InterviewType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "interviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @Enumerated(EnumType.STRING)
    private InterviewType type;

    @Column(name = "location_or_link")
    private String locationOrLink;

    @Column(name = "interviewer_note", columnDefinition = "TEXT")
    private String interviewerNote;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private InterviewResult result = InterviewResult.PENDING;
}
