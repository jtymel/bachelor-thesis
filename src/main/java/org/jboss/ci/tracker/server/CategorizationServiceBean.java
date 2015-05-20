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

import org.jboss.ci.tracker.server.entity.Categorization;
import org.jboss.ci.tracker.common.objects.CategorizationDto;
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
public class CategorizationServiceBean {

    @PersistenceContext(name = "MainPU")
    private EntityManager em;

    public List<CategorizationDto> getCategorizations() {
        Session session = (Session) em.getDelegate();

        List<Categorization> categorizations = new ArrayList<Categorization>(session.createQuery("FROM Categorization ORDER BY name").list());
        List<CategorizationDto> categorizationDtos = new ArrayList<CategorizationDto>(categorizations != null ? categorizations.size() : 0);

        for (Categorization categorization : categorizations) {
            categorizationDtos.add(createCategorizationDto(categorization));
        }

        return categorizationDtos;
    }

    private CategorizationDto createCategorizationDto(Categorization categorization) {
        return new CategorizationDto(categorization.getId(), categorization.getName());
    }

    public Long saveCategorization(CategorizationDto categorizationDto) {
        Session session = (Session) em.getDelegate();
        Categorization categorization = new Categorization(categorizationDto);

        session.saveOrUpdate(categorization);

        return categorization.getId();
    }

    public void deleteCategorization(CategorizationDto categorizationDto) {
        Session session = (Session) em.getDelegate();
        Query query = session.createQuery("FROM Categorization WHERE id = :categorizationId")
                .setParameter("categorizationId", categorizationDto.getId());
        Categorization categorization = (Categorization) query.uniqueResult();

        session.delete(categorization);
    }
}
