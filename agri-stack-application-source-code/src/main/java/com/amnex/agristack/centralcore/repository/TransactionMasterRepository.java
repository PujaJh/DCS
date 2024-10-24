package com.amnex.agristack.centralcore.repository;

import com.amnex.agristack.centralcore.entity.TransactionMaster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionMasterRepository extends JpaRepository<TransactionMaster, Long> {

	TransactionMaster findByTransactionId(String transactionId);
}
