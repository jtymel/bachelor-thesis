package gwtEntity.client.widgets.bridges;

import gwtEntity.common.objects.CategorizationDto;

/**
 *
 * @author jtymel
 */
public interface CategorizationListDetailBridge {

    public void setCategorizationAndDisplayDetail(CategorizationDto categorizationDto);

    public void cancelCategorizationDetailAndDisplayCategorizationList();

    public void cancelCategorizationList();
}
