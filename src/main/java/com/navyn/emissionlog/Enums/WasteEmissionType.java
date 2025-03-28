package com.navyn.emissionlog.Enums;

/**
 * Categories of waste that produce greenhouse gas emissions
 */
public enum WasteEmissionType {
    // Solid waste disposal
    MANAGED_DISPOSED_SOLID_WASTE("Solid Waste", "Managed"),
    MUNICIPAL_SOLID_WASTE("Solid Waste", "Unmanaged"),
    INDUSTRIAL_SOLID_WASTE("Solid Waste", "Unmanaged"),
    UNCATEGORIZED_DISPOSED_SOLID_WASTE("Solid Waste", "Uncategorized"),

    // Treatment methods
    BIOLOGICALLY_TREATED_SOLID_WASTE("Treatment", "Biological"),
    OPEN_BURNING("Treatment", "Thermal"),
    INCINERATION("Treatment", "Thermal"),

    // Wastewater
    MUNICIPAL_TREATED_WASTE_WATER("Wastewater", "Municipal"),
    INDUSTRIAL_TREATED_WASTE_WATER("Wastewater", "Industrial");

    private final String category;
    private final String subCategory;

    WasteEmissionType(String category, String subCategory) {
        this.category = category;
        this.subCategory = subCategory;
    }

    public String getCategory() {
        return category;
    }

    public String getSubCategory() {
        return subCategory;
    }
}