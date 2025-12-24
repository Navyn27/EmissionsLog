-- SQL Script to create Landfill Gas Utilization tables
-- Run this script in your PostgreSQL database
-- IMPORTANT: interventions table must exist before running this script

-- 1. Create landfill_gas_parameters table
CREATE TABLE IF NOT EXISTS landfill_gas_parameters (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    destruction_efficiency_percentage DOUBLE PRECISION NOT NULL,
    global_warming_potential_ch4 DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Create landfill_gas_utilization_mitigation table
-- Note: interventions table must exist before running this script
CREATE TABLE IF NOT EXISTS landfill_gas_utilization_mitigation (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    year INTEGER NOT NULL,
    ch4_captured DOUBLE PRECISION NOT NULL,
    project_intervention_id UUID NOT NULL,
    ch4_destroyed DOUBLE PRECISION NOT NULL,
    equivalent_co2e_reduction DOUBLE PRECISION NOT NULL,
    mitigation_scenario_grand DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_landfill_gas_intervention 
        FOREIGN KEY (project_intervention_id) 
        REFERENCES interventions(id) 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE,
    CONSTRAINT unique_year_landfill_gas UNIQUE (year)
);

-- 3. Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_landfill_gas_year 
    ON landfill_gas_utilization_mitigation(year);
    
CREATE INDEX IF NOT EXISTS idx_landfill_gas_intervention 
    ON landfill_gas_utilization_mitigation(project_intervention_id);
    
CREATE INDEX IF NOT EXISTS idx_landfill_gas_parameters_created_at 
    ON landfill_gas_parameters(created_at DESC);

-- 4. Add comments for documentation
COMMENT ON TABLE landfill_gas_parameters IS 'Stores dynamic parameters for Landfill Gas Utilization calculations (destruction efficiency and GWP)';
COMMENT ON TABLE landfill_gas_utilization_mitigation IS 'Stores Landfill Gas Utilization mitigation project records';
COMMENT ON COLUMN landfill_gas_parameters.destruction_efficiency_percentage IS 'Destruction efficiency percentage (0-100)';
COMMENT ON COLUMN landfill_gas_parameters.global_warming_potential_ch4 IS 'Global Warming Potential for CH₄';
COMMENT ON COLUMN landfill_gas_utilization_mitigation.ch4_captured IS 'CH₄ captured (user input)';
COMMENT ON COLUMN landfill_gas_utilization_mitigation.ch4_destroyed IS 'CH₄Captured * DestructionEfficiency(%)';
COMMENT ON COLUMN landfill_gas_utilization_mitigation.equivalent_co2e_reduction IS 'CH₄Destroyed * GlobalWarmingPotential(CH₄)';
COMMENT ON COLUMN landfill_gas_utilization_mitigation.mitigation_scenario_grand IS 'BAU - EquivalentCO₂eReduction';

