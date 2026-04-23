package ru.yandex.practicum.accountsservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.accountsservice.model.OutboxEvent;
import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {
    List<OutboxEvent> findByProcessedFalseOrderByCreatedAtAsc();
}
