package gwtEntity.server;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * Created by jtymel on 12/15/14.
 */
@Entity
public class Category implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Categorization id_categorization;
    
//    in Job causing 'org.postgresql.util.PSQLException: ERROR: relation "testdb.job_category" does not exist', hence temporarily commented 
    
//    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "categories")
//    private List<Job> jobs;
    
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "categories")
    private List<ParameterizedBuild> parameterizedBuilds;
    
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "categories")
    private List<Label> labels;
}