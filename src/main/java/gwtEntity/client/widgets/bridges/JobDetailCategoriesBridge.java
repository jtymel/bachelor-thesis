package gwtEntity.client.widgets.bridges;

import gwtEntity.common.objects.JobDto;

/**
 *
 * @author jtymel
 */
public interface JobDetailCategoriesBridge {
    public void setJobAndDisplayCategories(JobDto job);
    public void cancelJobCategoriesAndDisplayJobDetail();
}
