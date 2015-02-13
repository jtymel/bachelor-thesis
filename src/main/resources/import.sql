INSERT INTO job (id, NAME, URL) values (1007, 'My job from import.sql without any meaning', 'www.alik.cz')^
-- job is successfully added

-- CREATE OR REPLACE FUNCTION addFromImport(
--     integer,
--     integer)
--   RETURNS integer AS
-- 'select $1 + $2'
--   LANGUAGE SQL VOLATILE
--   COST 100;

-- CREATE OR REPLACE FUNCTION my_first_imported_proc() RETURNS INTEGER AS $BODY$ DECLARE seventeen INTEGER; BEGIN SELECT 4 INTO seventeen; RETURN seventeen; END; $BODY$ LANGUAGE plpgsql VOLATILE COST 100^

CREATE OR REPLACE FUNCTION my_first_imported_proc()
  RETURNS integer AS
$BODY$
BEGIN
SELECT 4;
END;
$BODY$
LANGUAGE plpgsql
COST 100^
