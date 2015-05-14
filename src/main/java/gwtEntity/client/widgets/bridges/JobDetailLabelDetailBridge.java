package gwtEntity.client.widgets.bridges;

import gwtEntity.common.objects.JobDto;
import gwtEntity.common.objects.LabelDto;

/**
 *
 * @author jtymel
 */
public interface JobDetailLabelDetailBridge {

    public void setLabelAndDisplayDetail(LabelDto label, JobDto job);

    public void cancelLabelDetailAndDisplayJobDetail();
}
