package com.amnex.agristack.dao;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class SurveyDetailMasterRequestDTO {
    String data;
    List<MultipartFile> media;
}
