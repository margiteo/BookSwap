package com.bookswap.backend.repository;

import com.bookswap.backend.entity.SwapRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SwapRequestRepository
        extends JpaRepository<SwapRequestEntity, Long> {

    List<SwapRequestEntity> findByBook_Owner_IdAndStatus(
            Long ownerId,
            SwapRequestEntity.Status status
    );

    List<SwapRequestEntity> findByRequester_Id(Long requesterId);
}
