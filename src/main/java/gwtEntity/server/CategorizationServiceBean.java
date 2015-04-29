package gwtEntity.server;

import gwtEntity.client.CategorizationDto;
import gwtEntity.client.JobDto;
import java.util.ArrayList;
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
public class CategorizationServiceBean {

    @PersistenceContext(name = "MainPU")
    private EntityManager em;

    public List<CategorizationDto> getCategorizations() {
        Session session = (Session) em.getDelegate();

        List<Categorization> categorizations = new ArrayList<Categorization>(session.createQuery("from Categorization").list());
        List<CategorizationDto> categorizationDtos = new ArrayList<CategorizationDto>(categorizations != null ? categorizations.size() : 0);

        for (Categorization categorization : categorizations) {
            categorizationDtos.add(createCategorizationDto(categorization));
        }

        return categorizationDtos;
    }

    private CategorizationDto createCategorizationDto(Categorization categorization) {
        return new CategorizationDto(categorization.getId(), categorization.getName());
    }

    public Long saveCategorization(CategorizationDto categorizationDto) {
        Session session = (Session) em.getDelegate();
        Categorization categorization = new Categorization(categorizationDto);

        session.saveOrUpdate(categorization);

        return categorization.getId();
    }

    public void deleteCategorization(CategorizationDto categorizationDto) {
        Session session = (Session) em.getDelegate();
        Query query = session.createQuery("FROM Categorization WHERE id = :categorizationId")
                .setParameter("categorizationId", categorizationDto.getId());
        Categorization categorization = (Categorization) query.uniqueResult();

        session.delete(categorization);

        // Not running cascade deletion
//        Session session = (Session) em.getDelegate();
//        
//        Categorization categorization = new Categorization(categorizationDto);
//        em.remove(em.contains(categorization) ? categorization : em.merge(categorization));

        // Not running cascade deletion
//        Session session = (Session) em.getDelegate();
//        Query query = session.createQuery("DELETE Categorization WHERE id = :categorizationId")
//                .setParameter("categorizationId", categorizationDto.getId()); 
//        query.executeUpdate();
    }
}
