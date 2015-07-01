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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transactional;
import javax.transaction.UserTransaction;
import org.jboss.ci.tracker.server.entity.PossibleResult;
import org.jboss.ci.tracker.server.entity.TestCase;

/**
 *
 * @author jtymel
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class PossibleResultBean {

    @PersistenceContext(name = "MainPU")
    private EntityManager em;

    @Resource
    private UserTransaction tx;

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public PossibleResult ensurePossibleResultExists(String possibleResultName) {
        try {
            return ensurePossibleResultExistsEx(possibleResultName);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private PossibleResult ensurePossibleResultExistsEx(String possibleResultName) throws Exception {
        try {
            tx.begin();
            PossibleResult pr = getPossibleResult(possibleResultName);

            if (pr != null) {
                tx.rollback();
                return pr;
            }

            pr = new PossibleResult();
            pr.setName(possibleResultName);
            em.persist(pr);
            em.flush();
            tx.commit();

            return pr;
        } catch (javax.persistence.PersistenceException ex) {
            if ((ex.getCause() instanceof org.hibernate.exception.ConstraintViolationException)
              || ex.getCause() instanceof javax.validation.ConstraintViolationException) {
                tx.rollback();
                return null;
            } else {
                tx.rollback();
                throw ex;
            }
        }
    }

    public PossibleResult getPossibleResult(String possibleResultName) {
        final List<PossibleResult> res = em
          .createNamedQuery("PossibleResult.byName", PossibleResult.class)
          .setParameter("name", possibleResultName)
          .getResultList();

        if (! res.isEmpty()) {
            return res.get(0);
        }

        return null;
    }

}
