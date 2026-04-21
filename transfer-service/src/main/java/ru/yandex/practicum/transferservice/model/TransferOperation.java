package ru.yandex.practicum.transferservice.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transfer_operations", schema = "transfer")
@Data
@NoArgsConstructor
public class TransferOperation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "from_login", nullable = false)
    private String fromLogin;

    @Column(name = "to_login", nullable = false)
    private String toLogin;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "error_message")
    private String errorMessage;

    @PrePersist
    void prePersist() { this.createdAt = LocalDateTime.now(); }
}
