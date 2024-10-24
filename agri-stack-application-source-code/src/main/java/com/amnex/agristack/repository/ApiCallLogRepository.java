package com.amnex.agristack.repository;

import com.amnex.agristack.entity.APICallLog;
import com.amnex.agristack.entity.VillageWiseCropDataLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApiCallLogRepository extends JpaRepository<APICallLog, Long>{

}
