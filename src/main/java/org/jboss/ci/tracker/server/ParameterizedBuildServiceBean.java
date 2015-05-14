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

import org.jboss.ci.tracker.server.entity.ParameterizedBuild;
import org.jboss.ci.tracker.common.objects.BuildDto;
import org.jboss.ci.tracker.common.objects.JobDto;
import org.jboss.ci.tracker.common.objects.ParameterizedBuildDto;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author jtymel
 */
@Stateless
@Transactional(Transactional.TxType.REQUIRED)
public class ParameterizedBuildServiceBean {

    @PersistenceContext(name = "MainPU")
    private EntityManager em;

    public Long saveParamBuild(ParameterizedBuildDto paramBuildDto) {
        Session session = (Session) em.getDelegate();
        ParameterizedBuild paramBuild = new ParameterizedBuild(paramBuildDto);

        session.saveOrUpdate(paramBuild);

        return paramBuild.getId();
    }

    public Long saveParamBuild(ParameterizedBuild paramBuild) {
        Session session = (Session) em.getDelegate();

        session.saveOrUpdate(paramBuild);

        return paramBuild.getId();
    }

    public List<ParameterizedBuildDto> getParamBuilds(BuildDto buildDto) {
        if (buildDto == null) {
            return null;
        }

        Session session = (Session) em.getDelegate();
        Query query = session.createQuery("FROM ParameterizedBuild WHERE build_id = :buildId")
                .setParameter("buildId", buildDto.getId());

        List<ParameterizedBuild> paramBuilds = new ArrayList<ParameterizedBuild>(query.list());
        List<ParameterizedBuildDto> paramBuildDtos = new ArrayList<ParameterizedBuildDto>(paramBuilds != null ? paramBuilds.size() : 0);

        for (ParameterizedBuild paramBuild : paramBuilds) {
            paramBuildDtos.add(createBuildDto(paramBuild));
        }
//        session.getTransaction().commit();
        return paramBuildDtos;
    }

    private ParameterizedBuildDto createBuildDto(ParameterizedBuild paramBuild) {
        ParameterizedBuildDto paramBuildDto = new ParameterizedBuildDto();

        paramBuildDto.setId(paramBuild.getId());
        paramBuildDto.setName(paramBuild.getName());
        paramBuildDto.setUrl(paramBuild.getUrl());

        return paramBuildDto;
    }
}
