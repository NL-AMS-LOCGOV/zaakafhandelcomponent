/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.model;

import net.atos.zac.zaaksturing.model.ZaakAfzender;

public class RESTZaakAfzender {

    public Long id;

    public String mail;

    public boolean defaultMail;

    public boolean speciaal;

    public RESTZaakAfzender() {
    }

    public RESTZaakAfzender(ZaakAfzender.Speciaal mail) {
        this.mail = mail.name();
        this.speciaal = true;
    }
}
