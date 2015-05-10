package gwtEntity.client.widgets.bridges;

import gwtEntity.client.JobDto;

/**
 *
 * @author jtymel
 */
public interface JobListDetailBridge {
    public void setJobAndDisplayDetail(JobDto jobDTO);
    public void cancelJobDetailAndDisplayJobList();
}
