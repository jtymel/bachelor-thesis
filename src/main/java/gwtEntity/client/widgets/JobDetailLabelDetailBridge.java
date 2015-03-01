package gwtEntity.client.widgets;

import gwtEntity.client.JobDto;
import gwtEntity.client.LabelDto;

/**
 *
 * @author jtymel
 */
public interface JobDetailLabelDetailBridge {
    public void setLabelAndDisplayDetail(LabelDto label, JobDto job);
    public void displayLabelList();
}
