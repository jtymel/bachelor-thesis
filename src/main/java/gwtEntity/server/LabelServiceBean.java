package gwtEntity.server;

import gwtEntity.client.CategorizationDto;
import gwtEntity.client.LabelDto;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
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
    
    public void deleteLabel(LabelDto labelDto) {
        Session session = (Session) em.getDelegate();
        
        Label label = new Label(labelDto);
        em.remove(em.contains(label) ? label : em.merge(label));
    }

}
