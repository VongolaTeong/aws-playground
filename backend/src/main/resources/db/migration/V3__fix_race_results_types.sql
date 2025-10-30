-- V3: Correct race_time and performance_score column types to DOUBLE PRECISION
ALTER TABLE race_results
  ALTER COLUMN race_time TYPE DOUBLE PRECISION,
  ALTER COLUMN performance_score TYPE DOUBLE PRECISION;
