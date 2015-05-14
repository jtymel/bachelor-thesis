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
import org.jboss.ci.tracker.common.objects.ParameterizedBuildDto;
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
public class StoreResultBean {

    @PersistenceContext(name = "MainPU")
    private EntityManager em;

    public void saveTestResult(TestResult testResult, ParameterizedBuild paramBuild) {
        Session session = (Session) em.getDelegate();
        Query query = session.getNamedQuery("storeTestResultProcedure")
                .setParameter("id_paramBuild", paramBuild)
                .setParameter("result", testResult.getResult())
                .setParameter("test", testResult.getTest())
                .setParameter("testCase", testResult.getTestCase())
                .setParameter("duration", testResult.getDuration());

        List result = query.list();
    }
}
