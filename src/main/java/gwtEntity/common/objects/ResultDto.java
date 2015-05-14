package gwtEntity.common.objects;

import gwtEntity.common.objects.PossibleResultDto;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author jtymel
 */
public class ResultDto extends PossibleResultDto {

    private String test;
    private String testCase;
    private Map<Long, Integer> results = new HashMap<Long, Integer>();
    private Long testId;

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

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

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.test);
        hash = 29 * hash + Objects.hashCode(this.testCase);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ResultDto other = (ResultDto) obj;
        if (!Objects.equals(this.test, other.test)) {
            return false;
        }
        if (!Objects.equals(this.testCase, other.testCase)) {
            return false;
        }
        return true;
    }

}
