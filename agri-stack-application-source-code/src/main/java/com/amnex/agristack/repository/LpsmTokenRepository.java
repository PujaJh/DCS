package com.amnex.agristack.repository;

import com.amnex.agristack.entity.LpsmToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LpsmTokenRepository extends JpaRepository<LpsmToken, Long> {

}
