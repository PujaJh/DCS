package com.amnex.agristack.centralcore.dao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Data
public class RegRecordsResponseDAO {

        @JsonProperty("farm_id")
     String farm_id;

        @JsonProperty("year")
             String yearString ;
        @JsonProperty("season")
        String season;

        @JsonProperty("season_id")
             String season_id;

        @JsonProperty("crop_name")
             String crop_name;

        @JsonProperty("sown_area")
             String sown_area;

        @JsonProperty("sown_area_unit")
             String sown_area_unit;
        @JsonProperty("crop_code")
        String crop_code;


}
