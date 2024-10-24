package com.amnex.agristack.dao;

import com.amnex.agristack.Enum.ConfigCode;

import lombok.Data;

@Data
public class ConfigurationDAO {
	private Long configurationId;
    private Integer configCodeValue;
    private String configKey;
    private String configValue;
    private Boolean isActive;
    private Boolean isDeleted;
    private String description;
}
