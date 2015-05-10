package gwtEntity.server;

import gwtEntity.server.entity.ParameterizedBuild;
import gwtEntity.client.ParameterizedBuildDto;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author jtymel
 */

@Stateless
@Transactional(Transactional.TxType.REQUIRED)
public class StoreResultBean {
    @PersistenceContext(name = "MainPU")
    private EntityManager em;
    
    public void saveTestResult(TestResult testResult, ParameterizedBuild paramBuild) {
        Session session = (Session) em.getDelegate();
        Query query = session.getNamedQuery("storeTestResultProcedure")
            .setParameter("id_paramBuild", paramBuild)
            .setParameter("result", testResult.getResult())
            .setParameter("test", testResult.getTest())
            .setParameter("testCase", testResult.getTestCase())
            .setParameter("duration", testResult.getDuration());

        List result = query.list();
    }
}

