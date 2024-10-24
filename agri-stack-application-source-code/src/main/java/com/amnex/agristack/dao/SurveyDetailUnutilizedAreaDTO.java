package com.amnex.agristack.dao;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class SurveyDetailUnutilizedAreaDTO {
    private Boolean isAvailable;
    private Double area;
    private Long unit;
    private List<String> media;
    private String remark;
}
