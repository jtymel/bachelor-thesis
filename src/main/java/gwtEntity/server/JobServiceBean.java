/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gwtEntity.server;

import gwtEntity.client.CategoryDto;
import gwtEntity.client.JobDto;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
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
//@NamedNativeQueries({
//	@NamedNativeQuery(
//	name = JobServiceBean.STR,
//	query = "CALL GetStocks(:stockCode)"
//	)
//})
public class JobServiceBean {

    public static final String STR = "nazdar";
    
    @PersistenceContext(name = "MainPU")
    private EntityManager em;

    private JobDto createJobDTO(Job job) {
        return new JobDto(job.getId(), job.getName(), job.getUrl());
    }

    public List<JobDto> getJobs() {
        Session session = (Session) em.getDelegate();
//        session.beginTransaction();
        List<Job> jobs = new ArrayList<Job>(session.createQuery("from Job").list());
        List<JobDto> jobDTOs = new ArrayList<JobDto>(jobs != null ? jobs.size() : 0);

        for (Job job : jobs) {
            jobDTOs.add(createJobDTO(job));
        }
//        session.getTransaction().commit();
        return jobDTOs;
    }

    public Long saveJob(JobDto jobDTO) {
        Session session = (Session) em.getDelegate();
        Job job = new Job(jobDTO);

        session.saveOrUpdate(job);
//        session.persist(job);

//        session.save(job);
        System.out.println(job.getId());
//        session.persist(jobDTO);
//        session.save(jobDTO);




//        memberEventSrc.fire(job);



//        Job job = new Job(jobDTO);
////        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("MainPU");
////        EntityManager entityManager = entityManagerFactory.createEntityManager();
//        em.getTransaction().begin();
//        em.persist(job);
//        em.getTransaction().commit();
//        em.close();

        System.out.println("##### #######: job: " + job.getId());

        // SNAHA O ZOBRAZENI ZAZNAMU (KONTROLA ULOZENI)
//        List<Job> jobs = new ArrayList<Job>(session.createQuery("from Job").list());
//        List<JobDTO> jobDTOs = new ArrayList<JobDTO>(jobs != null ? jobs.size() : 0);
//        if (jobs != null) {
//          for (Job job2 : jobs) {
//            jobDTOs.add(createJobDTO(job2));
//          }
//        }
//
//        System.out.println(jobDTOs);


//        Job job = new Job(jobDTO);
//        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
//        session.beginTransaction();
//        session.save(job);
//        session.getTransaction().commit();
        return job.getId();
    }
    
    public void deleteJob(JobDto jobDTO) {
        Session session = (Session) em.getDelegate();
        
        Job job = new Job(jobDTO);
        em.remove(em.contains(job) ? job : em.merge(job));
//        session.delete(job);
    }
    
    @PostConstruct
    public void tempProcTest() {
        Session session = (Session) em.getDelegate();
//        Query query = session.createSQLQuery(
//	"SELECT add(:number1, :number2)")
//          .setParameter("number1", 15)
//          .setParameter("number2", 17);
//        
//        System.out.println(query.uniqueResult());
        
         Query query2 = session.createSQLQuery(
	"SELECT my_first_imported_proc() " );
         
//         List result = query2.list();
//        for(int i=0; i<result.size(); i++){
//                Integer stock = (Integer) result.get(i);
//                System.out.println("%%%%% %%%% %%%" + stock);
//        }
        
//        System.out.println("^^^^^^^ ^^^^^^ ^^^^^^^ " + query2.uniqueResult());
        
//        Query query = session.createSQLQuery(
//	"SELECT addFromImport(:number1, :number2)")
//          .setParameter("number1", 4)
//          .setParameter("number2", 7);
//        
//        System.out.println(query.uniqueResult());
//        
//        
////
//        List result = query.list();
//        for(int i=0; i<result.size(); i++){
//                Integer stock = (Integer) result.get(i);
//                System.out.println("%%%%% %%%% %%%" + stock);
//        }
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

    private void addJobToCategory(Job job, Category category){
        Session session = (Session) em.getDelegate();
        category.addJob(job);
        session.saveOrUpdate(category);
    }

}
