/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.ontkoppeldedocumenten.model;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

import net.atos.zac.app.identity.model.RESTUser;

public class RESTOntkoppeldDocument {

    public long id;

    public UUID documentUUID;

    public String documentID;

    public String zaakID;

    public LocalDate creatiedatum;

    public String titel;

    public String bestandsnaam;

    public RESTUser ontkoppeldDoor;

    public ZonedDateTime ontkoppeldOp;

    public String reden;
}
