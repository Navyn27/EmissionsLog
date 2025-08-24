package com.navyn.emissionlog.Enums.Agriculture;

import java.util.List;

public enum LivestockCategory {
    DAIRY_COWS(List.of(LivestockSpecies.DAIRY_MATURE_COWS, LivestockSpecies.DAIRY_LACTATING_COWS, LivestockSpecies.DAIRY_GROWING_COWS)),
    SHEEP_OR_GOATS(List.of(LivestockSpecies.SHEEP, LivestockSpecies.GOATS)),
    SWINE(List.of(LivestockSpecies.SWINE)),
    POULTRY(List.of(LivestockSpecies.POULTRY)),
    RABBITS(List.of(LivestockSpecies.RABBITS));

    private List<LivestockSpecies> subCategories;

    LivestockCategory(List<LivestockSpecies> subCategories) {
        this.subCategories = subCategories;
    }
}
