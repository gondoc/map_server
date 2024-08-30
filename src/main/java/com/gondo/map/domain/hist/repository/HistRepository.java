package com.gondo.map.domain.hist.repository;

import com.gondo.map.domain.hist.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistRepository extends JpaRepository<History, String> {
}
