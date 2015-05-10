package gwtEntity.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import gwtEntity.client.CategoryDto;
import gwtEntity.client.JobDto;
import gwtEntity.client.LabelDto;
import gwtEntity.common.service.LabelService;
import java.util.List;
import javax.ejb.EJB;

/**
 *
 * @author jtymel
 */
public class LabelServiceImpl extends RemoteServiceServlet implements LabelService {

    @EJB
    private LabelServiceBean labelServiceBean;

    @Override
    public List<LabelDto> getLabels() {
        return labelServiceBean.getLabels();
    }

    @Override
    public Long saveLabel(LabelDto label, JobDto job) {
        return labelServiceBean.saveLabel(label, job);
    }

    @Override
    public void deleteLabel(LabelDto label) {
        labelServiceBean.deleteLabel(label);
    }

    @Override
    public void addCategoriesToLabel(LabelDto label, List<CategoryDto> categories) {
        labelServiceBean.addCategoriesToLabel(label, categories);
    }

    @Override
    public List<LabelDto> getLabels(JobDto job) {
        return labelServiceBean.getLabels(job);
    }

    @Override
    public List<CategoryDto> getCategoriesOfLabel(LabelDto labelDto) {
        return labelServiceBean.getCategoriesOfLabel(labelDto);
    }

}
