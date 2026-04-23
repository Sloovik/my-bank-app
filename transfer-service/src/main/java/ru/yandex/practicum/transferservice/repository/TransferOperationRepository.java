package ru.yandex.practicum.transferservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.transferservice.model.TransferOperation;

public interface TransferOperationRepository extends JpaRepository<TransferOperation, Long> {}