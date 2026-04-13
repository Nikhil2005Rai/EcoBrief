package com.example.aimeetingtranscription.repository;

import com.example.aimeetingtranscription.entity.Meeting;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    @EntityGraph(attributePaths = {"actionItems"})
    List<Meeting> findByUserIdOrderByCreatedAtDesc(Long userId);

    @EntityGraph(attributePaths = {"actionItems"})
    Optional<Meeting> findByIdAndUserId(Long id, Long userId);
}
