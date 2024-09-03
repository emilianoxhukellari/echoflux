package transcribe.domain.audit.data;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditEntity {

    @CreatedDate
    @Column(name = "created_at")
    @SuppressWarnings("unused")
    protected LocalDateTime createdAt;

    @CreatedBy
    @Column(name = "created_by")
    @SuppressWarnings("unused")
    protected String createdBy;

    @LastModifiedDate
    @Column(name = "updated_at")
    @SuppressWarnings("unused")
    protected LocalDateTime updatedAt;

    @LastModifiedBy
    @Column(name = "updated_by")
    @SuppressWarnings("unused")
    protected String updatedBy;

}
