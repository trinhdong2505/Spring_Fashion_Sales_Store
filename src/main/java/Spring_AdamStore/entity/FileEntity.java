package Spring_AdamStore.entity;

import Spring_AdamStore.constants.FileType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;


@Getter
@Setter
@Table(name = "files")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String publicId;
    private String fileName;
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private FileType fileType;


    @CreatedBy
    private String createdBy;
    @CreationTimestamp
    private LocalDate createdAt;

}
