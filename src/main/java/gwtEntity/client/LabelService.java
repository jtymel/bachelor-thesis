package gwtEntity.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import java.util.List;

/**
 *
 * @author jtymel
 */
@RemoteServiceRelativePath("labelService")
public interface LabelService extends RemoteService {

    public List<LabelDto> getLabels();

    public List<LabelDto> getLabels(JobDto job);

    public Long saveLabel(LabelDto label, JobDto job);

    public void deleteLabel(LabelDto label);

    public void addCategoriesToLabel(LabelDto label, List<CategoryDto> categories);

    public List<CategoryDto> getCategoriesOfLabel(LabelDto labelDto);
}
