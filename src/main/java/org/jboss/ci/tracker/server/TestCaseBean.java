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
import org.jboss.ci.tracker.server.entity.Test;
import org.jboss.ci.tracker.server.entity.TestCase;

/**
 *
 * @author jtymel
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class TestCaseBean {

    @PersistenceContext(name = "MainPU")
    private EntityManager em;

    @Resource
    private UserTransaction tx;

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public TestCase ensureTestCaseExists(String testCaseName) {
        try {
            return ensureTestCaseExistsEx(testCaseName);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    private TestCase ensureTestCaseExistsEx(String testCaseName) throws Exception {
        try {
            tx.begin();
            TestCase tc = getTestCase(testCaseName);

            if (tc != null) {
                tx.rollback();
                return tc;
            }

            tc = new TestCase();
            tc.setName(testCaseName);
            em.persist(tc);
            em.flush();
            tx.commit();

            return tc;
        } catch (org.hibernate.exception.ConstraintViolationException ex) {
            tx.rollback();
            return null;
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

    public TestCase getTestCase(String testCaseName) {
        final List<TestCase> res = em
          .createNamedQuery("TestCase.byName", TestCase.class)
          .setParameter("name", testCaseName)
          .getResultList();

        if (! res.isEmpty()) {
            return res.get(0);
        }

        return null;
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Test ensureTestExists(String testCase, String testName) {
        try {
            return ensureTestExistsEx(testCase, testName);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    private Test ensureTestExistsEx(String testCase, String testName) throws Exception {
        try {
            tx.begin();
            Test t = getTest(testCase, testName);

            if (t != null) {
                tx.rollback();
                return t;
            }

            Session session = (Session) em.getDelegate();
            final Integer resId = (Integer) session
              .createSQLQuery("SELECT * FROM createTest(:testCase, :name)")
              .setParameter("testCase", testCase)
              .setParameter("name", testName)
              .uniqueResult();

            em.flush();
            tx.commit();

            return resId == null ? null : em.find(Test.class, resId);
        } catch (org.hibernate.exception.ConstraintViolationException ex) {
            tx.rollback();
            return null;
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

    public Test getTest(String testCase, String testName) {
        final List<Test> res = em
          .createNamedQuery("Test.byName", Test.class)
          .setParameter("testCase", testCase)
          .setParameter("name", testName)
          .getResultList();

        if (! res.isEmpty()) {
            return res.get(0);
        }

        return null;
    }
}
