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

import java.util.List;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.transaction.UserTransaction;
import org.hibernate.Session;
import org.jboss.ci.tracker.server.entity.Category;
import org.jboss.ci.tracker.server.entity.Label;
import org.jboss.ci.tracker.server.entity.LabelCategory;
import org.jboss.logging.Logger;

/**
 *
 * @author jtymel
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class LabelBean {

    private static final Logger LOGGER = Logger.getLogger(LabelBean.class);

    @PersistenceContext(name = "MainPU")
    private EntityManager em;

    @Resource
    private UserTransaction tx;

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Label createOrFindLabel(String labelName, Integer jobId) {
        try {
            return createOrFindLabelEx(labelName, jobId);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    private Label createOrFindLabelEx(String labelName, Integer jobId) throws Exception {
        try {
            tx.begin();

            LOGGER.debugv("Searching for label {0}, job {1}", labelName, jobId);

            Label label = getOrCreateLabel(labelName, jobId);

            em.flush();
            tx.commit();

            return label;
        } catch (javax.persistence.PersistenceException ex) {
            if ((ex.getCause() instanceof org.hibernate.exception.ConstraintViolationException)
              || ex.getCause() instanceof javax.validation.ConstraintViolationException) {
                // It has been created by another transaction in the meantime
                tx.rollback();
                return null;
            } else {
                tx.rollback();
                throw ex;
            }
        }
    }

    private Label getOrCreateLabel(String labelName, Integer jobId) {
        Session session = (Session) em.getDelegate();

        Object[] res = (Object []) session.createSQLQuery("SELECT * FROM createLabel(:jobId, :labelName)")
          .setParameter("labelName", labelName)
          .setParameter("jobId", jobId.intValue())
          .uniqueResult()
          ;
        final Integer labelId = (Integer) res[0]; // always non-null
        final Integer existed = (Integer) res[1]; // always non-null

        Label label = em.find(Label.class, labelId);

        if (existed == null || existed == 0) {
            guessCategoriesOfLabel(label);
        }

        return label;
    }


    /**
     * Attempts to guess the categories
     *
     * @param label
     */
    private void guessCategoriesOfLabel(Label label) {
        List<Category> allCategories = (List<Category>) em.createQuery("SELECT c FROM Category c").getResultList();

        String labelString = label.getName();
        for (Category c : allCategories) {
            if (c.isInLabel(labelString)) {
                LabelCategory lc = new LabelCategory();
                lc.setLabel(label);
                lc.setCategory(c);
                label.getLabelCategories().add(lc);
            }
        }
        em.persist(label);
    }
}
