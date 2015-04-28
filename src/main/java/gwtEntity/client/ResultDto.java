package gwtEntity.client;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jtymel
 */
public class ResultDto extends PossibleResultDto {
    private String test;
    private String testCase;
    private Map<String, Integer> results = new HashMap<String, Integer>();

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public String getTestCase() {
        return testCase;
    }

    public void setTestCase(String testCase) {
        this.testCase = testCase;
    }

    public void addResult(String result, int count) {
        results.put(result, new Integer(count));
    }

    public Map<String, Integer> getResults() {
        return results;
    }

}
