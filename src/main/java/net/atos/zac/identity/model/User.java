/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.identity.model;

import org.apache.commons.lang3.StringUtils;

public class User {

    private final String id;

    private final String firstName;

    private final String lastName;

    private final String fullName;

    private final String email;

    /**
     * Constructor for creating an unknown User, a user with a given user id which is not known in the identity system.
     *
     * @param id Id of the user who is unknown
     */
    public User(final String id) {
        this.id = id;
        this.firstName = null;
        this.lastName = id;
        this.fullName = id;
        this.email = null;
    }

    public User(final String id, final String firstName, final String lastName, final String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = null;
        this.email = email;
    }

    protected User(final String id, final String firstName, final String lastName, final String fullName, final String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        if (StringUtils.isNotBlank(fullName)) {
            return fullName;
        } else if (StringUtils.isNotBlank(firstName) && StringUtils.isNotBlank(lastName)) {
            return String.format("%s %s", firstName, lastName);
        } else if (StringUtils.isNotBlank(lastName)) {
            return lastName;
        } else {
            return id;
        }
    }
}
