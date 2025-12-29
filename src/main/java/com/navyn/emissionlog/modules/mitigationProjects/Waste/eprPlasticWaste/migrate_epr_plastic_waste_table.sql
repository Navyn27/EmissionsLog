-- Migration script to update epr_plastic_waste_mitigation table from old schema to new schema
-- This script migrates existing data if the table already exists with the old schema
-- Run this ONLY if you have existing data that needs to be preserved

-- Step 1: Create backup table
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 
        FROM information_schema.tables 
        WHERE table_name = 'epr_plastic_waste_mitigation'
    ) THEN
        -- Create backup table
        CREATE TABLE IF NOT EXISTS epr_plastic_waste_mitigation_backup AS 
        SELECT * FROM epr_plastic_waste_mitigation;
        
        RAISE NOTICE 'Backup table created: epr_plastic_waste_mitigation_backup';
    END IF;
END $$;

-- Step 2: Add new columns as nullable first
ALTER TABLE epr_plastic_waste_mitigation 
ADD COLUMN IF NOT EXISTS project_intervention_id UUID;

ALTER TABLE epr_plastic_waste_mitigation 
ADD COLUMN IF NOT EXISTS recycled_plastic_without_epr_tonnes_per_year DOUBLE PRECISION;

ALTER TABLE epr_plastic_waste_mitigation 
ADD COLUMN IF NOT EXISTS recycled_plastic_with_epr_tonnes_per_year DOUBLE PRECISION;

ALTER TABLE epr_plastic_waste_mitigation 
ADD COLUMN IF NOT EXISTS additional_recycling_vs_bau_tonnes_per_year DOUBLE PRECISION;

ALTER TABLE epr_plastic_waste_mitigation 
ADD COLUMN IF NOT EXISTS adjusted_bau_emission_mitigation DOUBLE PRECISION;

-- Step 3: Migrate data from old columns to new columns (if old columns exist)
DO $$
BEGIN
    -- If old column exists, copy data to new column
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'epr_plastic_waste_mitigation' 
        AND column_name = 'recycling_without_epr_tonnes_per_year'
    ) THEN
        UPDATE epr_plastic_waste_mitigation 
        SET recycled_plastic_without_epr_tonnes_per_year = recycling_without_epr_tonnes_per_year
        WHERE recycled_plastic_without_epr_tonnes_per_year IS NULL;
    END IF;
    
    -- Copy recycled_plastic_with_epr_tonnes_per_year if it already exists with same name
    -- (no action needed if column name is the same)
    
    -- Copy additional_recycling_vs_bau_tonnes_per_year if it already exists
    -- (no action needed if column name is the same)
END $$;

-- Step 4: Set default values for new columns that don't have data
-- For existing records, we'll set default values or calculate from existing data
UPDATE epr_plastic_waste_mitigation 
SET recycled_plastic_without_epr_tonnes_per_year = 0.0
WHERE recycled_plastic_without_epr_tonnes_per_year IS NULL;

UPDATE epr_plastic_waste_mitigation 
SET recycled_plastic_with_epr_tonnes_per_year = 0.0
WHERE recycled_plastic_with_epr_tonnes_per_year IS NULL;

UPDATE epr_plastic_waste_mitigation 
SET additional_recycling_vs_bau_tonnes_per_year = 0.0
WHERE additional_recycling_vs_bau_tonnes_per_year IS NULL;

UPDATE epr_plastic_waste_mitigation 
SET adjusted_bau_emission_mitigation = 0.0
WHERE adjusted_bau_emission_mitigation IS NULL;

-- Step 5: Set a default intervention for existing records
-- WARNING: This uses the first intervention from the interventions table
-- You may want to adjust this based on your business logic
UPDATE epr_plastic_waste_mitigation 
SET project_intervention_id = (
    SELECT id FROM interventions LIMIT 1
)
WHERE project_intervention_id IS NULL;

-- Step 6: Make columns NOT NULL after setting default values
ALTER TABLE epr_plastic_waste_mitigation 
ALTER COLUMN recycled_plastic_without_epr_tonnes_per_year SET NOT NULL;

ALTER TABLE epr_plastic_waste_mitigation 
ALTER COLUMN recycled_plastic_with_epr_tonnes_per_year SET NOT NULL;

ALTER TABLE epr_plastic_waste_mitigation 
ALTER COLUMN additional_recycling_vs_bau_tonnes_per_year SET NOT NULL;

ALTER TABLE epr_plastic_waste_mitigation 
ALTER COLUMN adjusted_bau_emission_mitigation SET NOT NULL;

-- Step 7: Add foreign key constraint for project_intervention_id
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.table_constraints 
        WHERE table_name = 'epr_plastic_waste_mitigation' 
        AND constraint_name = 'fk_epr_project_intervention'
    ) THEN
        ALTER TABLE epr_plastic_waste_mitigation
        ADD CONSTRAINT fk_epr_project_intervention 
            FOREIGN KEY (project_intervention_id) 
            REFERENCES interventions(id)
            ON DELETE RESTRICT 
            ON UPDATE CASCADE;
    END IF;
END $$;

-- Step 8: Drop old columns if they exist (after migration)
ALTER TABLE epr_plastic_waste_mitigation 
DROP COLUMN IF EXISTS bau_solid_waste_emissions;

ALTER TABLE epr_plastic_waste_mitigation 
DROP COLUMN IF EXISTS plastic_waste_growth_factor;

ALTER TABLE epr_plastic_waste_mitigation 
DROP COLUMN IF EXISTS recycling_rate_with_epr;

ALTER TABLE epr_plastic_waste_mitigation 
DROP COLUMN IF EXISTS plastic_waste_base_tonnes_per_year;

ALTER TABLE epr_plastic_waste_mitigation 
DROP COLUMN IF EXISTS recycling_without_epr_tonnes_per_year;

-- Step 9: Create index on new foreign key
CREATE INDEX IF NOT EXISTS idx_epr_plastic_waste_intervention 
ON epr_plastic_waste_mitigation(project_intervention_id);

-- Step 10: Verify the migration
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'epr_plastic_waste_mitigation' 
        AND column_name = 'project_intervention_id'
    ) AND EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'epr_plastic_waste_mitigation' 
        AND column_name = 'adjusted_bau_emission_mitigation'
    ) THEN
        RAISE NOTICE 'Migration completed successfully!';
    ELSE
        RAISE WARNING 'Migration may not have completed correctly. Please verify manually.';
    END IF;
END $$;

