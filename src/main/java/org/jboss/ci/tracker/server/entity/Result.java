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
package org.jboss.ci.tracker.server.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;

/**
 * Created by jtymel on 12/15/14.
 */
// 2015-02-18 tiny "hack" (count (*) FROM) added, otherwise causes 'org.hibernate.MappingException: No Dialect mapping for JDBC type: 1111'
@NamedNativeQueries({
    @NamedNativeQuery(
            name = "storeTestResultProcedure",
            query = "SELECT count (*) FROM storeTestResult(:p_id_paramBuild, :p_id_possible_result, :p_id_test, :duration)"
    )
})
@Entity
public class Result implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "serial")
    private Integer id;

    @ManyToOne(cascade = { CascadeType.REFRESH, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST })
    private PossibleResult possibleResult;

    @ManyToOne(cascade = { CascadeType.REFRESH, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST })
    private Test test;

    @ManyToOne(cascade = { CascadeType.REFRESH, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST })
    private ParameterizedBuild parameterizedBuild;

    private float duration;

    public Result() {
    }

    public PossibleResult getPossibleResult() {
        return possibleResult;
    }

    public void setPossibleResult(PossibleResult possibleResult) {
        this.possibleResult = possibleResult;
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ParameterizedBuild getParameterizedBuild() {
        return parameterizedBuild;
    }

    public void setParameterizedBuild(ParameterizedBuild parameterizedBuild) {
        this.parameterizedBuild = parameterizedBuild;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }
}
