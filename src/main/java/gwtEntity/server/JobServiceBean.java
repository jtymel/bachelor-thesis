/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gwtEntity.server;

import gwtEntity.client.CategoryDto;
import gwtEntity.client.JobDto;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
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
        Session session = (Session) em.getDelegate();

        List<Job> jobs = new ArrayList<Job>(session.createQuery("from Job").list());
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

    public void deleteJob(JobDto jobDTO) {
        Session session = (Session) em.getDelegate();

        Job job = new Job(jobDTO);
        em.remove(em.contains(job) ? job : em.merge(job));
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
        Session session = (Session) em.getDelegate();
        Query query = session.createQuery("FROM Job WHERE id = :jobId")
                .setParameter("jobId", jobDto.getId());
        Job job = (Job) query.uniqueResult();

        List<Build> builds = job.getBuilds();
        List<ParameterizedBuild> paramBuilds = new ArrayList<ParameterizedBuild>();

        for (Build build : builds) {
            paramBuilds.addAll(build.getParameterizedBuilds());
        }

        for (ParameterizedBuild paramBuild : paramBuilds) {
            storeParamBuildCategoriesBean.saveTestResult(paramBuild);
        }

    }

    private void addParamBuildToCategory(ParameterizedBuild pb, List<Category> categories) {
        Session session = (Session) em.getDelegate();

        for (Category category : categories) {
            category.addParamBuild(pb);
            session.saveOrUpdate(category);
        }

    }

}
