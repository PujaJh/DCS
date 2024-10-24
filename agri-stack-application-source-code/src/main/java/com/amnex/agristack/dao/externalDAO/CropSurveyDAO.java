package com.amnex.agristack.dao.externalDAO;

//import com.amnex.agristack.farmerregistry.entity.LocationTypeMaster;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Data
public class CropSurveyDAO {
    public Long lpsm_id;
    public CropDetail surveyDetail;
}
