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
package gwtEntity.common.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import gwtEntity.common.objects.CategoryDto;
import gwtEntity.common.objects.JobDto;
import gwtEntity.common.objects.LabelDto;
import java.util.List;

/**
 *
 * @author jtymel
 */
@RemoteServiceRelativePath("labelService")
public interface LabelService extends RemoteService {

    public List<LabelDto> getLabels();

    public List<LabelDto> getLabels(JobDto job);

    public Long saveLabel(LabelDto label, JobDto job);

    public void deleteLabel(LabelDto label);

    public void addCategoriesToLabel(LabelDto label, List<CategoryDto> categories);

    public List<CategoryDto> getCategoriesOfLabel(LabelDto labelDto);
}
