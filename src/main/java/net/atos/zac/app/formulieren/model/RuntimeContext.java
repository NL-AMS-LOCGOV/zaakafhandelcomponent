/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */


package net.atos.zac.app.formulieren.model;

import java.util.UUID;

import net.atos.zac.app.taken.model.RESTTaak;
import net.atos.zac.app.zaken.model.RESTZaak;

public class RuntimeContext {

    public String formulierSysteemnaam;

    public UUID zaakUUID;

    public String taakID;

    public RESTZaak zaak;

    public RESTTaak taak;
}
