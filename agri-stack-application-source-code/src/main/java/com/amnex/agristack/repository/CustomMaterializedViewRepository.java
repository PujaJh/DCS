package com.amnex.agristack.repository;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Repository
public class CustomMaterializedViewRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void refreshMaterializedView() {
        entityManager.createNativeQuery("REFRESH MATERIALIZED VIEW agri_stack.mview_plot_detail").executeUpdate();

    }

}
