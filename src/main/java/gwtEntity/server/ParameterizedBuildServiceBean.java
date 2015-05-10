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

import gwtEntity.server.entity.ParameterizedBuild;
import gwtEntity.common.objects.BuildDto;
import gwtEntity.common.objects.JobDto;
import gwtEntity.common.objects.ParameterizedBuildDto;
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
        if(buildDto == null)
            return null;
        
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
    
    private ParameterizedBuildDto createBuildDto(ParameterizedBuild paramBuild){
        ParameterizedBuildDto paramBuildDto = new ParameterizedBuildDto();
        
        paramBuildDto.setId(paramBuild.getId());
        paramBuildDto.setName(paramBuild.getName());
        paramBuildDto.setUrl(paramBuild.getUrl());
        
        return paramBuildDto;
    }
}
