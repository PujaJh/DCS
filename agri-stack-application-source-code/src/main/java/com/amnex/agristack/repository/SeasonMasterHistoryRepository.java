package com.amnex.agristack.repository;

import com.amnex.agristack.entity.SowingSeasonHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeasonMasterHistoryRepository extends JpaRepository<SowingSeasonHistory, Long> {
}
