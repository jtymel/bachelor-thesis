INSERT INTO job (id, NAME, URL) values (1007, 'My job from import.sql without any meaning', 'www.alik.cz');
-- job is successfully added

-- CREATE OR REPLACE FUNCTION addFromImport(
--     integer,
--     integer)
--   RETURNS integer AS
-- 'select $1 + $2'
--   LANGUAGE SQL VOLATILE
--   COST 100;

CREATE FUNCTION my_first_imported_proc()
  RETURNS integer AS
$BODY$
BEGIN
SELECT 17;
END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;
