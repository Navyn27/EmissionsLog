-- Simple SQL script to create Waste to Energy tables
-- Run this in PostgreSQL after confirming interventions table exists

-- 1. Create waste_to_wte_parameters table
CREATE TABLE IF NOT EXISTS waste_to_wte_parameters (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    net_emission_factor DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Create waste_to_energy_mitigation table
-- IMPORTANT: interventions table must exist first!
CREATE TABLE IF NOT EXISTS waste_to_energy_mitigation (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    year INTEGER NOT NULL UNIQUE,
    waste_to_wte DOUBLE PRECISION NOT NULL,
    project_intervention_id UUID NOT NULL REFERENCES interventions(id),
    ghg_reduction_tonnes DOUBLE PRECISION NOT NULL,
    ghg_reduction_kilotonnes DOUBLE PRECISION NOT NULL,
    adjusted_emissions_with_wte DOUBLE PRECISION NOT NULL
);

-- 3. Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_waste_to_energy_year 
    ON waste_to_energy_mitigation(year);

CREATE INDEX IF NOT EXISTS idx_waste_to_energy_intervention 
    ON waste_to_energy_mitigation(project_intervention_id);

CREATE INDEX IF NOT EXISTS idx_wte_parameters_created_at 
    ON waste_to_wte_parameters(created_at DESC);

-- Verification queries (run these to confirm tables were created)
-- SELECT * FROM waste_to_wte_parameters;
-- SELECT * FROM waste_to_energy_mitigation;

