/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.documentcreatie.model;

import java.net.URI;
import java.time.LocalDate;

import net.atos.client.zgw.drc.model.InformatieobjectStatus;

public class Registratie {

    public URI zaak;

    public InformatieobjectStatus informatieobjectStatus;

    public String bronorganisatie;

    public LocalDate creatiedatum;

    public String auditToelichting;
}
