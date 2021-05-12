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

public class Command {

    public int id;
    public String name;
    public String action;
    public boolean vnc;
    public boolean rdp;

    public Command(int id, String name, String action, boolean vnc, boolean rdp) {
        this.id = id;
        this.name = name;
        this.action = action;
        this.vnc = vnc;
        this.rdp = rdp;
    }

    public String toString() {
        return "id: " + this.id +
                " name: " + this.name +
                " action: " + this.action +
                " vnc: " + (this.vnc ? "true" : "false") +
                " rdp: " + (this.rdp ? "true" : "false");
    }
}
