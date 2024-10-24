package com.amnex.agristack.dao;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class SurveyDetailTreeDTO {
    Boolean isAvailable;
    Long numberOfTree;
    Long area;
    Long unit;
    List<String> media;
    String remark;
}
