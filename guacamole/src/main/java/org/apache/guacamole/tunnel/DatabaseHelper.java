/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.guacamole.tunnel;

import org.slf4j.Logger;

import java.sql.*;
import java.util.*;

public class DatabaseHelper {

    private final Logger logger;

    public DatabaseHelper(Logger logger) {
        this.logger = logger;
    }

    public int getUserIdByUsername(String username) {
        List<List<Map<String, String>>> results;

        results = this.execute(
                "SELECT *\n" +
                "FROM guacamole_entity\n" +
                "WHERE name LIKE '" + username + "'\n" +
                "AND type = 'USER'"
        );

        String entity_id = results.get(0).get(0).get("entity_id");

        results = this.execute("SELECT *\n" +
                "FROM guacamole_user\n" +
                "WHERE entity_id = " + entity_id);

        String user_id = results.get(0).get(0).get("user_id");

        return Integer.parseInt(user_id);
    }

    public List<Command> getForbiddenCommands(int user_id) {
        List<List<Map<String, String>>> results;

        results =this.execute(
                "SELECT *\n" +
                "FROM commands\n" +
                "WHERE id IN (\n" +
                "    SELECT command_id\n" +
                "    FROM command_guac_user\n" +
                "    WHERE guac_user_user_id = " + user_id + "\n" +
                ")"
        );

        List<Command> commands = new ArrayList<>();

        for (List<Map<String, String>> row : results) {
            Command command = new Command(
                    Integer.parseInt(row.get(0).get("id")),
                    row.get(1).get("name"),
                    row.get(2).get("action"),
                    Boolean.parseBoolean(row.get(3).get("vnc")),
                    Boolean.parseBoolean(row.get(4).get("rdp"))
            );

            commands.add(command);
        }

        return commands;
    }

    private List<List<Map<String, String>>> execute(String query)
    {
        try {
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://localhost:5432/guacamole_db?user=guacamole";
            Connection conn = DriverManager.getConnection(url);

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);

            //Stores properties of a ResultSet object, including column count
            ResultSetMetaData resultSetMetaData = rs.getMetaData();
            int columnCount = resultSetMetaData.getColumnCount();

            List<List<Map<String, String>>> results = new ArrayList<>();

            //ArrayList<String> results = new ArrayList<>(columnCount);
            while (rs.next()) {
                int i = 1;

                List<Map<String, String>> row = new ArrayList<>();

                while(i <= columnCount) {
                    int finalI = i;

                    Map<String, String> column = new HashMap<String, String>() {{
                        put(
                                resultSetMetaData.getColumnLabel(finalI),
                                rs.getString(finalI)
                        );
                    }};

                    row.add(column);

                    i++;
                }

                results.add(row);
            }

            rs.close();
            st.close();

            logger.info("################## CLOSED QUERY");

            return results;
        } catch (ClassNotFoundException | SQLException e) {
            logger.info("#################### " + e.getMessage() + " ############");

            return new ArrayList<>();
        }


    }

}
