DROP FUNCTION IF EXISTS storeTestResult(BIGINT, TEXT, TEXT, TEXT, REAL)^
DROP FUNCTION IF EXISTS storeTestResult(BIGINT, BIGINT, BIGINT, REAL)^
CREATE OR REPLACE FUNCTION storeTestResult(p_id_paramBuild ParameterizedBuild.ID%TYPE, p_id_possible_result PossibleResult.ID%TYPE, p_test Test.ID%TYPE, p_duration REAL)
RETURNS VOID AS

$BODY$
DECLARE v_testCaseId TestCase.ID%TYPE;
DECLARE v_testId Test.ID%TYPE;

BEGIN
    INSERT INTO "result"(possibleresult_id, parameterizedbuild_id, test_id, duration)
      VALUES (p_id_possible_result, p_id_paramBuild, p_test, p_duration);
END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100^



CREATE OR REPLACE FUNCTION addCategoriesToParamBuild(p_paramBuildId ParameterizedBuild.ID%TYPE)
RETURNS VOID AS

$BODY$
BEGIN

    INSERT INTO parameterizedbuild_category(category_id, parambuild_id)
    SELECT jc.category_id, pb.id FROM
      Build b,
      ParameterizedBuild pb,
      Job j,
      job_category jc

      WHERE
      pb.id = p_paramBuildId
      AND b.id=pb.build_id
      AND j.id = b.job_id
      AND jc.job_id = j.id
      AND NOT EXISTS (
        SELECT * FROM parameterizedbuild_category pbc WHERE
        pbc.parambuild_id = pb.id
        AND pbc.category_id = jc.category_id
      )
      ;

    INSERT INTO parameterizedbuild_category(category_id, parambuild_id)
    SELECT lc.category_id, pb.id FROM
      Build b,
      ParameterizedBuild pb,
      Job j,
      Label l,
      label_category lc

      WHERE
      pb.id = p_paramBuildId
      AND b.id=pb.build_id
      AND j.id = b.job_id
      AND j.id = l.job_id
      AND lc.label_id = l.id
      AND pb.cachedLabel = l.name
      AND NOT EXISTS (
        SELECT * FROM parameterizedbuild_category pbc WHERE
        pbc.parambuild_id = pb.id
        AND pbc.category_id = lc.category_id
      )
      ;

END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100^


DROP FUNCTION IF EXISTS createLabel(BIGINT, TEXT)^
CREATE OR REPLACE FUNCTION createLabel(p_job_id Job.ID%TYPE, p_name TEXT)
RETURNS TABLE (o_label_id Label.ID%TYPE, o_exists INTEGER) AS

$BODY$
BEGIN
    SELECT id INTO o_label_id FROM LABEL WHERE job_id = p_job_id AND name = p_name LIMIT 1;

    IF o_label_id IS NULL THEN
        INSERT INTO LABEL (job_id, name) VALUES (p_job_id, p_name) returning id into o_label_id;
        o_exists = 0;
    ELSE
        o_exists = 1;
    END IF;

    RETURN NEXT;
END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100^

DROP FUNCTION IF EXISTS createTest(TEXT, TEXT)^
CREATE OR REPLACE FUNCTION createTest(p_test_case_name TEXT, p_test_name TEXT)
RETURNS Test.ID%TYPE AS

$BODY$
DECLARE v_testCaseId TestCase.ID%TYPE;
DECLARE v_testId Test.ID%TYPE;
BEGIN
    SELECT id INTO v_testCaseId FROM TestCase WHERE name = p_test_case_name LIMIT 1;
    IF v_testCaseId IS NULL THEN
        INSERT INTO TestCase (name) VALUES (p_test_case_name) returning id into v_testCaseId;
    END IF;

    SELECT id INTO v_testId FROM Test WHERE name = p_test_name LIMIT 1;
    IF v_testId IS NULL THEN
        INSERT INTO Test (testcase_id, name) VALUES (v_testCaseId, p_test_name) returning id into v_testId;
    END IF;

    RETURN v_testId;
END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100^



