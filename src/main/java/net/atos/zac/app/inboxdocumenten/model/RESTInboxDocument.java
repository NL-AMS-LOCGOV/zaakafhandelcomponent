/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.inboxdocumenten.model;

import java.time.LocalDate;
import java.util.UUID;

public class RESTInboxDocument {

    public long id;

    public UUID enkelvoudiginformatieobjectUUID;

    public String enkelvoudiginformatieobjectID;

    public LocalDate creatiedatum;

    public String titel;

    public String bestandsnaam;
}
