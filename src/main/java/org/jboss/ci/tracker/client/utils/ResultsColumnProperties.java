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
package org.jboss.ci.tracker.client.utils;

import com.google.gwt.user.cellview.client.TextColumn;
import org.jboss.ci.tracker.common.objects.ResultDto;

/**
 * This class stores important properties of result column in ResultList tab
 * {@link org.jboss.ci.tracker.client.widgets.ResultList}.
 *
 * @author jtymel
 */
public class ResultsColumnProperties {
    private TextColumn<ResultDto> column;
    private Integer possibleResultId;

    public TextColumn<ResultDto> getColumn() {
        return column;
    }

    public void setColumn(TextColumn<ResultDto> column) {
        this.column = column;
    }

    public Integer getPossibleResultId() {
        return possibleResultId;
    }

    public void setPossibleResultId(Integer possibleResultId) {
        this.possibleResultId = possibleResultId;
    }

}
