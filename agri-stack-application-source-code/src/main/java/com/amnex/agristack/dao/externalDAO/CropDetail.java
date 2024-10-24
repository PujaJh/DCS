package com.amnex.agristack.dao.externalDAO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Data
public class CropDetail {
    public Long lpsd_id;
    public List<CropDetail2> cropDetails;
}
