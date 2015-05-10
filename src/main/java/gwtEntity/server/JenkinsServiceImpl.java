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

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import gwtEntity.client.BuildDto;
import gwtEntity.common.service.JenkinsService;
import gwtEntity.client.JobDto;
import gwtEntity.client.ParameterizedBuildDto;
import java.util.List;
import javax.ejb.EJB;

/**
 *
 * @author jtymel
 */
public class JenkinsServiceImpl  extends RemoteServiceServlet implements JenkinsService {
    @EJB
    JenkinsDownloader jenkinsDownloader;

    @Override
    public List<BuildDto> downloadBuilds(JobDto jobDto) {
//        return jenkinsDownloader.downloadBuilds(jobDto);
        return null;
    }
    
    @Override
    public List<ParameterizedBuildDto> downloadParameterizedBuilds(BuildDto buildDto) {
//        return jenkinsDownloader.downloadParameterizedBuilds(buildDto);
        return null;
    }   

    @Override
    public void downloadBuilds(List<JobDto> jobs) {
        jenkinsDownloader.downloadBuilds(jobs);
    }
}
