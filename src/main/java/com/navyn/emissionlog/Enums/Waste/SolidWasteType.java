package com.navyn.emissionlog.Enums.Waste;

import lombok.Getter;

@Getter
public enum SolidWasteType {
    FOOD(0.15,0.700, 0.400, 1.7, 0.67, 13.00, 1.00, 0.500),
    SLUDGE(0.05, 0.700, 0.400, 1.7, 0.67, 13.00, 1.00, 0.500),
    PAPER(0.4, 0.500, 0.070, 9.9, 0.93, 13.00, 1.00, 0.500),
    GARDEN(0.2,0.700,0.170,4.1,0.84,13.00,1.00,0.500),
    WOOD(0.43, 0.100, 0.035, 19.8, 0.97, 13.00, 1.00,0.500),
    TEXTILES(0.24,0.500, 0.070, 9.9, 0.93, 13.00, 1.00, 0.500),
    NAPPIES(0.24, 0.500, 0.170, 4.1, 0.84, 13.00, 1.00,0.500),
    MSW(0.0,0.500,0.170,4.1,0.84,13.00,1.00,0.500),
    INDUSTRY(0.150,0.500,0.170,4.1,0.84,13.00,1.00,0.500),
    ;

    private Double DOC;
    private Double DOCF;
    private Double k;
    private Double H;
    private Double EXP1;
    private Double EXP2;
    private Double M;
    private Double F;

    SolidWasteType(Double DOC, Double DOCF, Double k, Double H, Double EXP1, Double M, Double EXP2, Double F) {
        this.DOC = DOC;
        this.DOCF = DOCF;
        this.k = k;
        this.H = H;
        this.EXP1 = EXP1;
        this.EXP2 = EXP2;
        this.M = M;
        this.F = F;
    }
}
