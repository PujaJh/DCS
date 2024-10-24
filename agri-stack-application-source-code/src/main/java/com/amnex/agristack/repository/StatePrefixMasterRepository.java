package com.amnex.agristack.repository;

import com.amnex.agristack.entity.StatePrefixMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatePrefixMasterRepository extends JpaRepository<StatePrefixMaster, Long> {
}
