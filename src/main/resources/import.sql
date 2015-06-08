CREATE OR REPLACE FUNCTION storeTestResult(id_paramBuild BIGINT, p_result TEXT, p_test TEXT, p_testCase TEXT, p_duration REAL)
RETURNS VOID AS

$BODY$
DECLARE v_posRes BIGINT;
DECLARE v_testCaseId BIGINT;
DECLARE v_testId BIGINT;

BEGIN

    SELECT id INTO v_posRes FROM POSSIBLERESULT WHERE name = p_result LIMIT 1;
    IF v_posRes IS NULL THEN
        INSERT INTO POSSIBLERESULT (id, name) VALUES (DEFAULT, p_result) returning id into v_posRes;
    END IF;

    SELECT id INTO v_testCaseId FROM TESTCASE WHERE name = p_testCase LIMIT 1;
    IF v_testCaseId IS NULL THEN
        INSERT INTO TESTCASE (name) VALUES (p_testCase) returning id into v_testCaseId;
    END IF;

    SELECT id INTO v_testId FROM TEST WHERE name = p_test AND testcase_id = v_testCaseId LIMIT 1;
    IF v_testId IS NULL THEN
        INSERT INTO TEST (name, testcase_id) VALUES (p_test, v_testCaseId) returning id into v_testId;
    END IF;

    INSERT INTO "result"(possibleresult_id, parameterizedbuild_id, test_id, duration)
    VALUES (v_posRes, id_paramBuild, v_testId, p_duration);

END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100^



CREATE OR REPLACE FUNCTION addCategoriesToParamBuild(p_paramBuildId BIGINT)
RETURNS VOID AS

$BODY$
BEGIN

    INSERT INTO parambuild_category(category_id, parambuild_id)
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
        SELECT * FROM ParamBuild_Category pbc WHERE
        pbc.parambuild_id = pb.id
        AND pbc.category_id = jc.category_id
      )
      ;

    INSERT INTO parambuild_category(category_id, parambuild_id)
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
        SELECT * FROM ParamBuild_Category pbc WHERE
        pbc.parambuild_id = pb.id
        AND pbc.category_id = lc.category_id
      )
      ;

END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100^