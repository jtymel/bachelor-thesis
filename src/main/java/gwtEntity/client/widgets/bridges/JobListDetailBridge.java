package gwtEntity.client.widgets.bridges;

import gwtEntity.common.objects.JobDto;

/**
 *
 * @author jtymel
 */
public interface JobListDetailBridge {

    public void setJobAndDisplayDetail(JobDto jobDTO);

    public void cancelJobDetailAndDisplayJobList();
}
