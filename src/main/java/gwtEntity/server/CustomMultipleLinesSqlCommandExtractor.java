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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.common.util.StringHelper;
import org.hibernate.tool.hbm2ddl.ImportScriptException;

import org.hibernate.tool.hbm2ddl.ImportSqlCommandExtractor;

/**
 *
 * @author jtymel
 */
public class CustomMultipleLinesSqlCommandExtractor implements ImportSqlCommandExtractor {

    @Override
    public String[] extractCommands(Reader reader) {
        BufferedReader bufferedReader = new BufferedReader(reader);
        List<String> statementList = new ArrayList<String>();

        try {
            String statement = "";
            for (String sql = bufferedReader.readLine(); sql != null; sql = bufferedReader.readLine()) {
                String trimmedSql = sql.trim();

                if (StringHelper.isEmpty(trimmedSql) || isComment(trimmedSql)) {
                    continue;
                }

                if (trimmedSql.endsWith("^")) {
                    trimmedSql = trimmedSql.substring(0, trimmedSql.length() - 1);
                    statement += " " + trimmedSql;
                    statementList.add(statement);
                    statement = "";
                } else {
                    statement += " " + trimmedSql;
                }
            }

            return statementList.toArray(new String[statementList.size()]);
        } catch (IOException e) {
            throw new ImportScriptException("Error during import script parsing.", e);
        }
    }

    private boolean isComment(final String line) {
        return line.startsWith("--");
    }

}
