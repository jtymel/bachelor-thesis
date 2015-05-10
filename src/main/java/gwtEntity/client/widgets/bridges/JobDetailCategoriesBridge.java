package gwtEntity.client.widgets.bridges;

import gwtEntity.client.JobDto;

/**
 *
 * @author jtymel
 */
public interface JobDetailCategoriesBridge {
    public void setJobAndDisplayCategories(JobDto job);
    public void cancelJobCategoriesAndDisplayJobDetail();
}
