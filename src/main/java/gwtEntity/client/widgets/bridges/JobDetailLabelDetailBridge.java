package gwtEntity.client.widgets.bridges;

import gwtEntity.client.JobDto;
import gwtEntity.client.LabelDto;

/**
 *
 * @author jtymel
 */
public interface JobDetailLabelDetailBridge {
    public void setLabelAndDisplayDetail(LabelDto label, JobDto job);
    public void cancelLabelDetailAndDisplayJobDetail();
}
