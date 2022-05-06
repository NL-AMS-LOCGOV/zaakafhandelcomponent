/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.identity.model;

public class User {

    private String id;

    private String firstName;

    private String lastName;

    private String displayName;

    private String email;

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
