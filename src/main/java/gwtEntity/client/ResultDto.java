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
    private Map<Long, Integer> results = new HashMap<Long, Integer>();

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

    public void setResults(Map<Long, Integer> results) {
        this.results = results;
    }

    public Map<Long, Integer> getResults() {
        return results;
    }

}
