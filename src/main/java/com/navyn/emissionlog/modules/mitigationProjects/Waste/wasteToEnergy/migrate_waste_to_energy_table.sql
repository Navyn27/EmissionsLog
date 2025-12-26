-- Migration script to update waste_to_energy_mitigation table from old schema to new schema
-- This script migrates existing data if the table already exists with the old schema
-- Run this ONLY if you have existing data that needs to be preserved

-- Step 1: Check if old column exists and create backup
DO $$
BEGIN
    -- Check if bau_emissions_solid_waste column exists (old schema)
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'waste_to_energy_mitigation' 
        AND column_name = 'bau_emissions_solid_waste'
    ) THEN
        -- Create backup table
        CREATE TABLE IF NOT EXISTS waste_to_energy_mitigation_backup AS 
        SELECT * FROM waste_to_energy_mitigation;
        
        RAISE NOTICE 'Backup table created: waste_to_energy_mitigation_backup';
    END IF;
END $$;

-- Step 2: Add new column if it doesn't exist
ALTER TABLE waste_to_energy_mitigation 
ADD COLUMN IF NOT EXISTS project_intervention_id UUID;

-- Step 3: Set a default intervention for existing records (you may need to adjust this)
-- WARNING: You need to provide a valid intervention UUID
-- Replace 'YOUR-DEFAULT-INTERVENTION-UUID-HERE' with an actual UUID from the interventions table
UPDATE waste_to_energy_mitigation 
SET project_intervention_id = (
    SELECT id FROM interventions LIMIT 1
)
WHERE project_intervention_id IS NULL;

-- Step 4: Make project_intervention_id NOT NULL after setting default values
ALTER TABLE waste_to_energy_mitigation 
ALTER COLUMN project_intervention_id SET NOT NULL;

-- Step 5: Add foreign key constraint
ALTER TABLE waste_to_energy_mitigation
ADD CONSTRAINT fk_project_intervention 
    FOREIGN KEY (project_intervention_id) 
    REFERENCES interventions(id);

-- Step 6: Drop old column if it exists
ALTER TABLE waste_to_energy_mitigation 
DROP COLUMN IF EXISTS bau_emissions_solid_waste;

-- Step 7: Create index on new foreign key
CREATE INDEX IF NOT EXISTS idx_waste_to_energy_intervention 
ON waste_to_energy_mitigation(project_intervention_id);

-- Step 8: Verify the migration
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'waste_to_energy_mitigation' 
        AND column_name = 'project_intervention_id'
    ) AND NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'waste_to_energy_mitigation' 
        AND column_name = 'bau_emissions_solid_waste'
    ) THEN
        RAISE NOTICE 'Migration completed successfully!';
    ELSE
        RAISE WARNING 'Migration may not have completed correctly. Please verify manually.';
    END IF;
END $$;

