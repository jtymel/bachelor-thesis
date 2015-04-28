/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package gwtEntity.server;

import gwtEntity.client.BuildDto;
import gwtEntity.client.JobDto;
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
    
    public Long saveBuild(BuildDto buildDto) {
        Session session = (Session) em.getDelegate();
        Build build = new Build(buildDto);

        session.saveOrUpdate(build);
//        session.persist(job);

//        session.save(job);
        System.out.println(build.getId());
        
        return build.getId();
    }
    
    public Long saveBuild(Build build) {
        Session session = (Session) em.getDelegate();        

        session.saveOrUpdate(build);
//        session.persist(job);

//        session.save(job);
        System.out.println(build.getId());
        
        return build.getId();
    }
    
    public List<BuildDto> getBuilds(JobDto jobDto) {
        if(jobDto == null)
            return null;
        
        Session session = (Session) em.getDelegate();
        Query query = session.createQuery("FROM Build WHERE job_id = :jobId")
                .setParameter("jobId", jobDto.getId());                
        
        List<Build> builds = new ArrayList<Build>(query.list());
        List<BuildDto> buildDtos = new ArrayList<BuildDto>(builds != null ? builds.size() : 0);

        for (Build build : builds) {
            buildDtos.add(createBuildDto(build));
        }
//        session.getTransaction().commit();
        return buildDtos;
    }
    
    private BuildDto createBuildDto(Build build){
        BuildDto buildDto = new BuildDto();
        
        buildDto.setId(build.getId());
        buildDto.setName(build.getName());
        buildDto.setUrl(build.getUrl());
        
        return buildDto;
    }
    

}
