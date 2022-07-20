/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.informatieobjecten.model;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

import net.atos.client.zgw.drc.model.OndertekeningSoort;
import net.atos.zac.app.identity.model.RESTUser;

/**
 *
 */
public class RESTOndertekening {

    public LocalDate datum;

    public OndertekeningSoort soort;
}
