package gwtEntity.server.entity;

import gwtEntity.server.entity.ParameterizedBuild;
import gwtEntity.server.entity.Test;
import gwtEntity.server.entity.PossibleResult;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;

/**
 * Created by jtymel on 12/15/14.
 */
// 2015-02-18 tiny "hack" (count (*) FROM) added, otherwise causes 'org.hibernate.MappingException: No Dialect mapping for JDBC type: 1111'
@NamedNativeQueries({
    @NamedNativeQuery(
            name = "storeTestResultProcedure",
            query = "SELECT count (*) FROM storeTestResult(:id_paramBuild, :result, :test, :testCase, :duration)"
    )
})
@Entity
@IdClass(Result.ResultId.class)
public class Result implements Serializable {
//    @EmbeddedId
//    private ResultID resultID;

    @Id
    @ManyToOne
    private PossibleResult possibleResult;

    @Id
    @ManyToOne
    private Test test;

    @Id
    @ManyToOne
    private ParameterizedBuild parameterizedBuild;

    private float duration;

    public Result() {
    }

    public PossibleResult getPossibleResult() {
        return possibleResult;
    }

    public void setPossibleResult(PossibleResult possibleResult) {
        this.possibleResult = possibleResult;
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public ParameterizedBuild getParameterizedBuild() {
        return parameterizedBuild;
    }

    public void setParameterizedBuild(ParameterizedBuild parameterizedBuild) {
        this.parameterizedBuild = parameterizedBuild;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

//@Embeddable
    public static class ResultId implements Serializable {

        private PossibleResult possibleResult;
        private Test test;
        private ParameterizedBuild parameterizedBuild;

        public ResultId() {
        }

        public ResultId(PossibleResult possibleResult, Test test, ParameterizedBuild parameterizedBuild) {
            this.possibleResult = possibleResult;
            this.test = test;
            this.parameterizedBuild = parameterizedBuild;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 53 * hash + Objects.hashCode(this.possibleResult);
            hash = 53 * hash + Objects.hashCode(this.test);
            hash = 53 * hash + Objects.hashCode(this.parameterizedBuild);
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
            final ResultId other = (ResultId) obj;
            if (!Objects.equals(this.possibleResult, other.possibleResult)) {
                return false;
            }
            if (!Objects.equals(this.test, other.test)) {
                return false;
            }
            if (!Objects.equals(this.parameterizedBuild, other.parameterizedBuild)) {
                return false;
            }
            return true;
        }

    }
}
