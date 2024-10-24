package com.amnex.agristack.dao.externalDAO;

import com.amnex.agristack.entity.LocationTypeMaster;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Data
public class FarmLandPlotDAO {
    public Long farmlandPlotRegistryId;
    public String landParcelId;
    public Object landParcelUlpin;
    public String plotGeometry;
    public double plotArea;
    public String unitType;
    public Long villageLgdCode;
    public LocationTypeMaster locationTypeMasterId;
    public String farmlandId;
    public Object landType;
    public String surveyNumber;
    public String subSurveyNumber;
}
