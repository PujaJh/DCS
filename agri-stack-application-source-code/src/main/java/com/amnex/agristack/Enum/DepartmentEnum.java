package com.amnex.agristack.Enum;

import org.geolatte.geom.M;

public enum DepartmentEnum {
    REVENUE_DEPARTMENT(1),
    AGRICULTURE_DEPARTMENT(2),
    HORTICULTURE_DEPARTMENT(3),
    AGRI_TECHNO_PROMOTERS(4),
    SURVEYOR(5),
    PRIVATE_RESIDENT(6),
    ADMIN(7),
    FINANCE(8),
    ACCOUNT(9),
    HUMAN_RESOURCE(10),
    FRUITS(11),
    PULSES(12),
    AGRICULTURE_CENSUS(13),
    AGRICULTURE_MARKETING(14),
    PRICE_SUPPORT(15),
    CREDIT(16),
    INSURANCE(17),
    CROPS(18),
    DROUGHT_MANAGEMENT(19),
    STATISTICS(20),
    ECONOMIC_ADMINISTRATION(21),
    EXTENSION(22),
    GENERAL_COORDINATION(23),
    INTERNATIONAL_COOPERATION(24),
    INTEGRATED_NUTRIENT_MANAGEMENT(25),
    DIGITAL_AGRICULTURE_DIVISION(26),
    MECHANIZATION_AND_TECHNOLOGY(27),
    OIL_SEEDS(28),
    NATURAL_RESOURCE_MANAGEMENT(29),
    PLAN_COORDINATION(30),
    PLANT_PROTECTION(31),
    POLICY(32),
    NATURAL_FARMING(33),
    ORGANIC_FARMING(34),
    RAINFED_FARMING_SYSTEM(35),
    SEEDS(36),
    RASHTRIYA_KRISHI_VIKAS_YOJANA(37),
    FARMERS_WELFARE_DIVISION(38),
    VIGILANCE(39),
    O_M_PG_RTI(40),
    PANCHAYAT(41);



    private Integer value;

    public Integer getValue()
    {
        return this.value;
    }

    private DepartmentEnum(Integer value)
    {
        this.value = value;
    }
}
