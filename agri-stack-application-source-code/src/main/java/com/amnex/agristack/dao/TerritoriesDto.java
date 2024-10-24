package com.amnex.agristack.dao;

import lombok.Data;

@Data
public class TerritoriesDto {
    Long code;
    String name;

    public TerritoriesDto(Long code, String name) {
        this.code = code;
        this.name = name;
    }
}
