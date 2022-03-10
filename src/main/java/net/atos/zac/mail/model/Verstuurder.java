/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.mail.model;

import org.eclipse.microprofile.config.ConfigProvider;

import javax.json.bind.annotation.JsonbProperty;

public class Verstuurder {

    private static final String MAIL_DOMEIN = ConfigProvider.getConfig().getValue("mail.domein", String.class);

    @JsonbProperty("Email")
    private String email;

    @JsonbProperty("Name")
    private String name;

    public Verstuurder() {
        // Voor de eerste opzet zijn deze waarden hardcoded
        this.email = "zaakafhandelcomponent@" + MAIL_DOMEIN;
        this.name = "Zaakafhandelcomponent";
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

    public void setName(final String name) {
        this.name = name;
    }
}
