package com.amnex.agristack.dao.UpVillageDAO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Data
public class UpVillageDetailDAO {
    public UpVillageDetailDAO data;
    public Long villageLgdCode;
    public Long totalPlotCount;
    public List<PlotLevelInfo> plotLevelInfo;
}
