/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.model;

import net.atos.zac.zaaksturing.model.ZaakAfzender;

public class RESTZaakAfzender {

    public Long id;

    public boolean defaultMail;

    public String mail;

    public String suffix;

    public String replyTo;

    public boolean speciaal;

    public RESTZaakAfzender() {
    }

    public RESTZaakAfzender(ZaakAfzender.Speciaal speciaal) {
        this.mail = speciaal.name();
        this.speciaal = true;
    }
}
