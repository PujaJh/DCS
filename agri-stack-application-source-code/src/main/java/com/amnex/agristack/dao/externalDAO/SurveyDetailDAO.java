package com.amnex.agristack.dao.externalDAO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Data
public class SurveyDetailDAO {
    private CropSurveyDAO cropSurveyDetail;
}
