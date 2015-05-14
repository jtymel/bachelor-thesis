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
package gwtEntity.server;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author jtymel
 */
@Singleton
@Startup
public class GetStoredProceduresBean {

    @PersistenceContext(name = "MainPU")
    private EntityManager em;

    @PostConstruct
    public void getStoredProcedures() {
        InputStream in = GetStoredProceduresBean.class.getResourceAsStream("/import.sql");

        try {
            Reader reader = new InputStreamReader(in, "UTF-8");

            String[] storedProcedures = new CustomMultipleLinesSqlCommandExtractor().extractCommands(reader);

            Session session = (Session) em.getDelegate();
            for (String storedProcedure : storedProcedures) {
                Query query = session.createSQLQuery(storedProcedure);
                query.executeUpdate();
            }

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(GetStoredProceduresBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
