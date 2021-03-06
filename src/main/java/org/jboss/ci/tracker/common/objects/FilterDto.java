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
package org.jboss.ci.tracker.common.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author jtymel
 */
public class FilterDto implements Serializable {

    private List<Integer> categoryIds;
    private List<Integer> possibleResultIds;
    private Date dateFrom;
    private Date dateTo;

    public FilterDto() {
        categoryIds = new ArrayList<Integer>();
        possibleResultIds = new ArrayList<Integer>();
    }

    public List<Integer> getPossibleResultIds() {
        return possibleResultIds;
    }

    public void setPossibleResultIds(List<Integer> possibleResultIds) {
        this.possibleResultIds = possibleResultIds;
    }

    public void addPossibleResultId(Integer id) {
        possibleResultIds.add(id);
    }

    public List<Integer> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Integer> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public void addCategoryId(Integer id) {
        categoryIds.add(id);
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

}
