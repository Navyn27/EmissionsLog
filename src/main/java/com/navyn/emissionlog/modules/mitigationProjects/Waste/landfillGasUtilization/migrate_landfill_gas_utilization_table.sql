-- Migration script to update landfill_gas_utilization_mitigation table
-- This script adds the project_intervention_id column and foreign key constraint
-- Run this if the table exists but doesn't have the project_intervention_id column

-- Step 1: Create backup table (optional, for safety)
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 
        FROM information_schema.tables 
        WHERE table_name = 'landfill_gas_utilization_mitigation'
    ) THEN
        CREATE TABLE IF NOT EXISTS landfill_gas_utilization_mitigation_backup AS 
        SELECT * FROM landfill_gas_utilization_mitigation;
        
        RAISE NOTICE 'Backup table created: landfill_gas_utilization_mitigation_backup';
    END IF;
END $$;

-- Step 2: Add new column if it doesn't exist
ALTER TABLE landfill_gas_utilization_mitigation 
ADD COLUMN IF NOT EXISTS project_intervention_id UUID;

-- Step 3: Set a default intervention for existing records
-- This uses the first available intervention from the interventions table
UPDATE landfill_gas_utilization_mitigation 
SET project_intervention_id = (
    SELECT id FROM interventions LIMIT 1
)
WHERE project_intervention_id IS NULL;

-- Step 4: Make project_intervention_id NOT NULL after setting default values
-- Only if there are no NULL values remaining
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM landfill_gas_utilization_mitigation 
        WHERE project_intervention_id IS NULL
    ) THEN
        ALTER TABLE landfill_gas_utilization_mitigation 
        ALTER COLUMN project_intervention_id SET NOT NULL;
        RAISE NOTICE 'Column project_intervention_id set to NOT NULL';
    ELSE
        RAISE WARNING 'Cannot set NOT NULL: some records still have NULL project_intervention_id';
    END IF;
END $$;

-- Step 5: Add foreign key constraint (only if it doesn't exist)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.table_constraints 
        WHERE constraint_name = 'fk_landfill_gas_intervention'
        AND table_name = 'landfill_gas_utilization_mitigation'
    ) THEN
        ALTER TABLE landfill_gas_utilization_mitigation
        ADD CONSTRAINT fk_landfill_gas_intervention 
            FOREIGN KEY (project_intervention_id) 
            REFERENCES interventions(id)
            ON DELETE RESTRICT 
            ON UPDATE CASCADE;
        RAISE NOTICE 'Foreign key constraint added';
    ELSE
        RAISE NOTICE 'Foreign key constraint already exists';
    END IF;
END $$;

-- Step 6: Create index on new foreign key for better performance
CREATE INDEX IF NOT EXISTS idx_landfill_gas_intervention 
ON landfill_gas_utilization_mitigation(project_intervention_id);

-- Step 7: Verify the migration
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'landfill_gas_utilization_mitigation' 
        AND column_name = 'project_intervention_id'
    ) THEN
        RAISE NOTICE 'Migration completed successfully!';
    ELSE
        RAISE WARNING 'Migration may not have completed correctly. Please verify manually.';
    END IF;
END $$;

