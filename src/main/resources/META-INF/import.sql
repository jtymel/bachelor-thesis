CREATE OR REPLACE FUNCTION addFromImport(
    integer,
    integer)
  RETURNS integer AS
'select $1 + $2'
  LANGUAGE SQL VOLATILE
  COST 100;

CREATE OR REPLACE FUNCTION my_first_imported_proc()
  RETURNS integer AS
'SELECT 17 AS result;'
LANGUAGE SQL
IMMUTABLE

