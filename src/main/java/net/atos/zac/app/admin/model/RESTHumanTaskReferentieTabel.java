/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.model;

import net.atos.zac.zaaksturing.model.FormulierVeldDefinitie;

public class RESTHumanTaskReferentieTabel {

    public Long id;

    public String veld;

    public RESTReferentieTabel tabel;

    public RESTHumanTaskReferentieTabel() {
    }

    public RESTHumanTaskReferentieTabel(final FormulierVeldDefinitie veldDefinitie) {
        veld = veldDefinitie.name();
    }
}
