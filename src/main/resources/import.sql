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





CREATE OR REPLACE FUNCTION storeTestResult(id_paramBuild BIGINT, result TEXT, p_test TEXT, p_testCase TEXT, duration REAL)
RETURNS VOID AS

$BODY$
DECLARE v_posRes BIGINT;
DECLARE v_testCaseId BIGINT;
DECLARE v_testId BIGINT;

BEGIN

SELECT id INTO v_posRes FROM POSSIBLERESULT WHERE name = result LIMIT 1;
IF v_posRes IS NULL THEN
    INSERT INTO POSSIBLERESULT (id, name) VALUES (DEFAULT, result) returning id into v_posRes;
END IF;

SELECT id INTO v_testCaseId FROM TESTCASE WHERE name = p_testCase LIMIT 1;
IF v_testCaseId IS NULL THEN
    INSERT INTO TESTCASE (name) VALUES (p_testCase) returning id into v_testCaseId;
END IF;

SELECT id INTO v_testId FROM TEST WHERE name = p_test AND id_testcase_id = v_testCaseId LIMIT 1;
IF v_testId IS NULL THEN
    INSERT INTO TEST (name, id_testcase_id) VALUES (p_test, v_testCaseId) returning id into v_testId;
END IF;

INSERT INTO RESULT(id_possibleresult_id, id_parameterizedbuild_id, id_test_id, duration)
VALUES (v_posRes, id_paramBuild, v_testId, duration);

-- RETURNING id INTO newId;

-- RETURN newId;

END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100^
