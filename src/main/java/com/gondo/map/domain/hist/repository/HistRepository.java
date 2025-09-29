package com.gondo.map.domain.hist.repository;

import com.gondo.map.domain.hist.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;

@Repository
public interface HistRepository extends JpaRepository<History, String> {

    @Transactional
    @Modifying
    @Query("""
            update History h set h.histUseYn = false where h.histId = :histId
            """)
    int deleteByHistId(@Param("histId") String histId);
}
