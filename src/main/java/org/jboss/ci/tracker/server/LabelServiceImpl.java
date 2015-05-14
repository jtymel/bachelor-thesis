/* 
 * Copyright (C) 2015 Jan Tymel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jboss.ci.tracker.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.jboss.ci.tracker.common.objects.CategoryDto;
import org.jboss.ci.tracker.common.objects.JobDto;
import org.jboss.ci.tracker.common.objects.LabelDto;
import org.jboss.ci.tracker.common.services.LabelService;
import java.util.List;
import javax.ejb.EJB;

/**
 *
 * @author jtymel
 */
public class LabelServiceImpl extends RemoteServiceServlet implements LabelService {

    @EJB
    private LabelServiceBean labelServiceBean;

    @Override
    public List<LabelDto> getLabels() {
        return labelServiceBean.getLabels();
    }

    @Override
    public Long saveLabel(LabelDto label, JobDto job) {
        return labelServiceBean.saveLabel(label, job);
    }

    @Override
    public void deleteLabel(LabelDto label) {
        labelServiceBean.deleteLabel(label);
    }

    @Override
    public void addCategoriesToLabel(LabelDto label, List<CategoryDto> categories) {
        labelServiceBean.addCategoriesToLabel(label, categories);
    }

    @Override
    public List<LabelDto> getLabels(JobDto job) {
        return labelServiceBean.getLabels(job);
    }

    @Override
    public List<CategoryDto> getCategoriesOfLabel(LabelDto labelDto) {
        return labelServiceBean.getCategoriesOfLabel(labelDto);
    }

}
