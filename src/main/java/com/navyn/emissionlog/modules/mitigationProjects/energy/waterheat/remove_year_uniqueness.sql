-- Remove unique constraint on year column in avoided_electricity_production table
-- This script will drop any unique constraint on the year column if it exists

-- For PostgreSQL
DO $$
DECLARE
    constraint_name TEXT;
BEGIN
    -- Find the unique constraint on the year column
    SELECT conname INTO constraint_name
    FROM pg_constraint
    WHERE conrelid = 'avoided_electricity_production'::regclass
      AND contype = 'u'
      AND conkey::int[] @> ARRAY(
          SELECT attnum
          FROM pg_attribute
          WHERE attrelid = 'avoided_electricity_production'::regclass
            AND attname = 'year'
      );

    -- Drop the constraint if it exists
    IF constraint_name IS NOT NULL THEN
        EXECUTE format('ALTER TABLE avoided_electricity_production DROP CONSTRAINT %I', constraint_name);
        RAISE NOTICE 'Dropped unique constraint: %', constraint_name;
    ELSE
        RAISE NOTICE 'No unique constraint found on year column';
    END IF;
END $$;

