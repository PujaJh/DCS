package com.amnex.agristack.dao;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class FarmerGrievanceWithMediaRequestDTO {
    String data;
    List<MultipartFile> media;
}
