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
import org.jboss.ci.tracker.server.entity.Job;
import org.jboss.ci.tracker.server.entity.Category;
import org.jboss.ci.tracker.server.entity.Build;
import org.jboss.ci.tracker.common.objects.CategoryDto;
import org.jboss.ci.tracker.common.objects.JobDto;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.hibernate.Query;
import org.hibernate.Session;

@Stateless
@Transactional(Transactional.TxType.REQUIRED)

public class JobServiceBean {

    @EJB
    private StoreParamBuildCategoriesBean storeParamBuildCategoriesBean;

    @PersistenceContext(name = "MainPU")
    private EntityManager em;

    private JobDto createJobDTO(Job job) {
        return new JobDto(job.getId(), job.getName(), job.getUrl());
    }

    public List<JobDto> getJobs() {
        List<Job> jobs = getPlainJobs();
        List<JobDto> jobDTOs = new ArrayList<JobDto>(jobs != null ? jobs.size() : 0);

        for (Job job : jobs) {
            jobDTOs.add(createJobDTO(job));
        }

        return jobDTOs;
    }

    public Long saveJob(JobDto jobDTO) {
        Session session = (Session) em.getDelegate();
        Job job = new Job(jobDTO);

        session.saveOrUpdate(job);

        return job.getId();
    }

    public void deleteJob(JobDto jobDto) {
        Session session = (Session) em.getDelegate();
        Query query = session.createQuery("FROM Job WHERE id = :jobId")
                .setParameter("jobId", jobDto.getId());
        Job job = (Job) query.uniqueResult();
        job.getCategories().clear();

        session.delete(job);
    }

    public void addCategoriesToLabel(JobDto jobDto, List<CategoryDto> categoriesDto) {
        Session session = (Session) em.getDelegate();
        Query query = session.createQuery("FROM Job WHERE id = :jobId")
                .setParameter("jobId", jobDto.getId());
        Job job = (Job) query.uniqueResult();

        List<Category> categories = new ArrayList<Category>(categoriesDto.size());

        for (CategoryDto categoryDto : categoriesDto) {
            Query query2 = session.createQuery("from Category WHERE id = :categoryid")
                    .setParameter("categoryid", categoryDto.getId());
            Category category = (Category) query2.uniqueResult();
            addJobToCategory(job, category);
            categories.add(category);
        }

        job.setCategories(categories);

        session.saveOrUpdate(job);
    }

    public Job getPlainJob(JobDto jobDto) {
        Session session = (Session) em.getDelegate();
        Query query = session.createQuery("FROM Job WHERE id = :jobId")
                .setParameter("jobId", jobDto.getId());
        return (Job) query.uniqueResult();
    }

    private void addJobToCategory(Job job, Category category) {
        Session session = (Session) em.getDelegate();
        category.addJob(job);
        session.saveOrUpdate(category);
    }

    public void addCategoriesToParamBuild(JobDto jobDto) {
        if (jobDto == null) {
            return;
        }

        Session session = (Session) em.getDelegate();
        Query query = session.createQuery("FROM Job WHERE id = :jobId")
                .setParameter("jobId", jobDto.getId());
        Job job = (Job) query.uniqueResult();

        List<Build> builds = job.getBuilds();
        addCategoriesToParamBuildOfBuilds(builds);
    }

    public void addCategoriesToParamBuildOfBuilds(List<Build> builds) {
        if (builds == null) {
            return;
        }

        List<ParameterizedBuild> paramBuilds = new ArrayList<ParameterizedBuild>();

        for (Build build : builds) {
            paramBuilds.addAll(build.getParameterizedBuilds());
        }

        addCategoriesToParamBuild(paramBuilds);

    }

    public void addCategoriesToParamBuild(List<ParameterizedBuild> paramBuilds) {
        if (paramBuilds == null) {
            return;
        }

        for (ParameterizedBuild paramBuild : paramBuilds) {
            storeParamBuildCategoriesBean.addCategoriesToParamBuild(paramBuild);
        }
    }

    public List<Job> getPlainJobs() {
        Session session = (Session) em.getDelegate();
        return new ArrayList<Job>(session.createQuery("from Job").list());
    }

}
