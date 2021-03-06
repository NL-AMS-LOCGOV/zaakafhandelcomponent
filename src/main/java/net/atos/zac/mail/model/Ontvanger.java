/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.mail.model;

import javax.json.bind.annotation.JsonbProperty;

public class Ontvanger {

    @JsonbProperty("Email")
    private String email;

    @JsonbProperty("Name")
    private String name;

    public Ontvanger(final String email, final String name) {
        this.email = email;
        this.name = name;
    }

    public Ontvanger(final String email) {
        this(email, null);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(final String naam) {
        this.name = naam;
    }
}
