package gwtEntity.client.widgets.bridges;

import gwtEntity.client.CategorizationDto;

/**
 *
 * @author jtymel
 */
public interface CategorizationListDetailBridge {

    public void setCategorizationAndDisplayDetail(CategorizationDto categorizationDto);

    public void cancelCategorizationDetailAndDisplayCategorizationList();

    public void cancelCategorizationList();
}
