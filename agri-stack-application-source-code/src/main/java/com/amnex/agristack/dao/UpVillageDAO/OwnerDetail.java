package com.amnex.agristack.dao.UpVillageDAO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Data
public class OwnerDetail {
    public String ownerName;
    public String ownerNo;
    public Double extentAssignedArea;
    public Double extentTotalArea;
    public String indentifierType;
    public String indentifierName;
}
