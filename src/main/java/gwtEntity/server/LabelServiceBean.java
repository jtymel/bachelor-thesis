package gwtEntity.server;

import gwtEntity.client.CategorizationDto;
import gwtEntity.client.CategoryDto;
import gwtEntity.client.LabelDto;
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
public class LabelServiceBean {

    @PersistenceContext(name = "MainPU")
    private EntityManager em;

    public List<LabelDto> getLabels() {
        Session session = (Session) em.getDelegate();

        List<Label> labels = new ArrayList<Label>(session.createQuery("from Label").list());
        List<LabelDto> labelDtos = new ArrayList<LabelDto>(labels != null ? labels.size() : 0);

        for (Label label : labels) {
            labelDtos.add(createLabelDto(label));
        }

        return labelDtos;
    }
    
    private LabelDto createLabelDto(Label label) {
        return new LabelDto(label.getId(), label.getName());
    }
    
    public Long saveLabel(LabelDto labelDto) {
        Session session = (Session) em.getDelegate();
        Label label = new Label(labelDto);

        session.saveOrUpdate(label);

        return label.getId();
    }

    public Long saveLabel(Label label) {
        Session session = (Session) em.getDelegate();

        Query query = session.createQuery("from Label WHERE name = :labelName")
                .setParameter("labelName", label.getName());

        Label storedLabel = (Label) query.uniqueResult();

        if(storedLabel == null) {
            session.saveOrUpdate(label);
            return label.getId();
        }

        return storedLabel.getId();
    }

    public void deleteLabel(LabelDto labelDto) {
        Session session = (Session) em.getDelegate();
        
        Label label = new Label(labelDto);
        em.remove(em.contains(label) ? label : em.merge(label));
    }

    public void addCategoriesToLabel(LabelDto labelDto, List<CategoryDto> categoriesDto) {
        Session session = (Session) em.getDelegate();
// No label is in the table
        Query query = session.createQuery("from Label WHERE id = :labelid")
                .setParameter("labelid", labelDto.getId());
        Label label = (Label) query.uniqueResult();

        List<Category> categories = new ArrayList<Category>(categoriesDto.size());

        for (CategoryDto categoryDto : categoriesDto) {
            Query query2 = session.createQuery("from Category WHERE id = :categoryid")
                .setParameter("categoryid", categoryDto.getId());
            Category category = (Category) query2.uniqueResult();
            addLabelToCategory(label, category);
            categories.add(category);
        }

        label.setCategories(categories);

        session.saveOrUpdate(label);
    }

    private void addLabelToCategory(Label label, Category category){
        Session session = (Session) em.getDelegate();

        category.addLabel(label);
        session.saveOrUpdate(category);
    }

}
