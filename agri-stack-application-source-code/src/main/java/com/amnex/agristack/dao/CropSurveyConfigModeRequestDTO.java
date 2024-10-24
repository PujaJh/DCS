package com.amnex.agristack.dao;

import java.util.List;

import lombok.Data;

@Data
public class CropSurveyConfigModeRequestDTO {
    String territoryLevel;
    List<Long> territoryCodes;
    Long mode;
}
