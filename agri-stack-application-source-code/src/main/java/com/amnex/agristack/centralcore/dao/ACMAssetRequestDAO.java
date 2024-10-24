package com.amnex.agristack.centralcore.dao;

import java.util.ArrayList;

import lombok.Data;

@Data
public class ACMAssetRequestDAO {
    public String land_asset_data;
    public String crop_asset_data;
    public ArrayList<AssetSeasonDao> survey_season;
}
