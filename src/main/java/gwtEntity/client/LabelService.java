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
    public Long saveLabel(LabelDto label);    
    public void deleteLabel(LabelDto label);
}
