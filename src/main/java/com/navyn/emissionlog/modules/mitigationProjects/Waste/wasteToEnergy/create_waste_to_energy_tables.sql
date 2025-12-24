-- SQL Script to create Waste to Energy tables
-- Run this script in your PostgreSQL database

-- 1. Create waste_to_wte_parameters table
CREATE TABLE IF NOT EXISTS waste_to_wte_parameters (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    net_emission_factor DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Drop existing waste_to_energy_mitigation table if it exists (with old schema)
-- WARNING: This will delete all existing data!
-- Uncomment the next line only if you want to drop the old table:
-- DROP TABLE IF EXISTS waste_to_energy_mitigation CASCADE;

-- 3. Create waste_to_energy_mitigation table (new schema)
-- Note: interventions table must exist before running this script
CREATE TABLE IF NOT EXISTS waste_to_energy_mitigation (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    year INTEGER NOT NULL,
    waste_to_wte DOUBLE PRECISION NOT NULL,
    project_intervention_id UUID NOT NULL,
    ghg_reduction_tonnes DOUBLE PRECISION NOT NULL,
    ghg_reduction_kilotonnes DOUBLE PRECISION NOT NULL,
    adjusted_emissions_with_wte DOUBLE PRECISION NOT NULL,
    CONSTRAINT fk_project_intervention 
        FOREIGN KEY (project_intervention_id) 
        REFERENCES interventions(id) 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE,
    CONSTRAINT unique_year UNIQUE (year)
);

-- 4. Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_waste_to_energy_year ON waste_to_energy_mitigation(year);
CREATE INDEX IF NOT EXISTS idx_waste_to_energy_intervention ON waste_to_energy_mitigation(project_intervention_id);
CREATE INDEX IF NOT EXISTS idx_wte_parameters_created_at ON waste_to_wte_parameters(created_at DESC);

-- 5. Add comments for documentation
COMMENT ON TABLE waste_to_wte_parameters IS 'Stores dynamic Net Emission Factor for Waste to Energy calculations';
COMMENT ON TABLE waste_to_energy_mitigation IS 'Stores Waste to Energy mitigation project records';
COMMENT ON COLUMN waste_to_wte_parameters.net_emission_factor IS 'Net Emission Factor in tCO2eq/t';
COMMENT ON COLUMN waste_to_energy_mitigation.waste_to_wte IS 'Waste to Waste-to-Energy in t/year';
COMMENT ON COLUMN waste_to_energy_mitigation.ghg_reduction_tonnes IS 'GHG Reduction in tCO2eq';
COMMENT ON COLUMN waste_to_energy_mitigation.ghg_reduction_kilotonnes IS 'GHG Reduction in ktCO2eq';
COMMENT ON COLUMN waste_to_energy_mitigation.adjusted_emissions_with_wte IS 'Adjusted Emissions with WtE in ktCO2e';

