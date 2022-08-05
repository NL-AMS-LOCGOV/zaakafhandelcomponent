/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.identity.model;

public class Group {

    private final String id;

    private final String name;

    private final String email;

    /**
     * Constructor for creating an unknown Group, a group with a given group id which is not known in the identity system.
     *
     * @param id Id of the group which is unknown
     */
    public Group(final String id) {
        this.id = id;
        this.name = id;
        this.email = null;
    }

    public Group(final String id, final String name, final String email) {
        this.id = id;
        this.name = name != null ? name : id;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
