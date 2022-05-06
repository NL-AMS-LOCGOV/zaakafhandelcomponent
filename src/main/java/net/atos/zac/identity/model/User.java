/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.identity.model;

public class User {

    private final String id;

    private final String firstName;

    private final String lastName;

    private final String displayName;

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
        this.displayName = id;
        this.email = null;
    }

    public User(final String id, final String firstName, final String lastName, final String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.displayName = firstName != null ? firstName + " " + lastName : lastName;
        this.email = email;
    }

    public User(final String id, final String firstName, final String lastName, final String displayName, final String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.displayName = displayName;
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

    public String getDisplayName() {
        return displayName;
    }
}
