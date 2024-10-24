package com.amnex.agristack.dao;

public interface ReviewSurveyCountDAO {
   Long gettotal_survey();
    Long getapproved_count();
    Long getrejectedcount();
    Long getreassigned_count();
    Long getunder_approval_count();
    Long getverifier_count();
}
