package com.amnex.agristack.centralcore.dao;

import lombok.Data;

@Data
public class ACMRequestDAO {
    public String state_sso_token;
    public String acm_token;
    public String sender_id;
    public String request_id;
    public String farmer_id;
    public ACMAssetRequestDAO asset_data;
    // public String land_asset_data;
    // public String crop_asset_data;
    public String callback_url;
    public String redirect_url;
    // public ArrayList<AssetSeasonDao> survey_season;
}
