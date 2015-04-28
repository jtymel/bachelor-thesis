package gwtEntity.server;

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
    private PossibleResult id_possibleResult;

    @Id
    @ManyToOne
    private Test id_test;

    @Id
    @ManyToOne
    private ParameterizedBuild id_parameterizedBuild;

    private float duration;

    public Result() {
    }

    public PossibleResult getId_possibleResult() {
        return id_possibleResult;
    }

    public void setId_possibleResult(PossibleResult id_possibleResult) {
        this.id_possibleResult = id_possibleResult;
    }

    public Test getId_test() {
        return id_test;
    }

    public void setId_test(Test id_test) {
        this.id_test = id_test;
    }

    public ParameterizedBuild getId_parameterizedBuild() {
        return id_parameterizedBuild;
    }

    public void setId_parameterizedBuild(ParameterizedBuild id_parameterizedBuild) {
        this.id_parameterizedBuild = id_parameterizedBuild;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

//@Embeddable
    public static class ResultId implements Serializable {

        private PossibleResult id_possibleResult;
        private Test id_test;
        private ParameterizedBuild id_parameterizedBuild;

        public ResultId() {
        }

        public ResultId(PossibleResult id_possibleResult, Test id_test, ParameterizedBuild id_parameterizedBuild) {
            this.id_possibleResult = id_possibleResult;
            this.id_test = id_test;
            this.id_parameterizedBuild = id_parameterizedBuild;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 53 * hash + Objects.hashCode(this.id_possibleResult);
            hash = 53 * hash + Objects.hashCode(this.id_test);
            hash = 53 * hash + Objects.hashCode(this.id_parameterizedBuild);
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
            if (!Objects.equals(this.id_possibleResult, other.id_possibleResult)) {
                return false;
            }
            if (!Objects.equals(this.id_test, other.id_test)) {
                return false;
            }
            if (!Objects.equals(this.id_parameterizedBuild, other.id_parameterizedBuild)) {
                return false;
            }
            return true;
        }

    }
}
