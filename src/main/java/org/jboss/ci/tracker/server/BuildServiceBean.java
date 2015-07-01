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

import org.jboss.ci.tracker.server.entity.Build;
import org.jboss.ci.tracker.common.objects.BuildDto;
import org.jboss.ci.tracker.common.objects.JobDto;
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
public class BuildServiceBean {

    @PersistenceContext(name = "MainPU")
    private EntityManager em;

    public List<BuildDto> getBuilds(JobDto jobDto) {
        if (jobDto == null) {
            return null;
        }

        Session session = (Session) em.getDelegate();
        Query query = session.createQuery("FROM Build WHERE job_id = :jobId")
                .setParameter("jobId", jobDto.getId());

        List<Build> builds = new ArrayList<Build>(query.list());
        List<BuildDto> buildDtos = new ArrayList<BuildDto>(builds != null ? builds.size() : 0);

        for (Build build : builds) {
            buildDtos.add(createBuildDto(build));
        }

        return buildDtos;
    }

    private BuildDto createBuildDto(Build build) {
        BuildDto buildDto = new BuildDto();

        buildDto.setId(build.getId());
        buildDto.setName(build.getName());
        buildDto.setUrl(build.getUrl());

        return buildDto;
    }

}
