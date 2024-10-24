package com.amnex.agristack.centralcore.Enum;

public enum ACMAttributesDBMapping {
    FARMER_ID("fr_farmer_number", "farmer_registry"),
    FARMER_NAME("fr_name_en", "farmer_registry"),
    AADHAAR("farmer_aadhaar_hash", "farmer_registry"),
    AADHAAR_HASH("farmer_aadhaar_hash", "farmer_registry"),
    DOB("fr_dob", "farmer_registry"),
    STATE_CODE("fr_st_state_lgd_code", "farmer_registry"),
    MOBILE_NO("fr_mobile_number", "farmer_registry"),
    RESIDENCE_ADDRESS("fr_address_en", "farmer_registry"),
    LGD_CODE("fr_vill_village_lgd_code", "farmer_registry"),
    IDENTIFIER_NAME("fr_identifier_name_en", "farmer_registry");

    private String value;
    private String table;

    private ACMAttributesDBMapping(String value, String table) {
        this.value = value;
        this.table = table;
    }

    public String getValue() {
        return this.value;
    }

    public String getTable() {
        return this.table;
    }

    // ACMAttributesDBMapping.valueOf("FARMER_ID").getValue()
}
