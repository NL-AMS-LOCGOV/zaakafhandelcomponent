/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.inboxdocumenten.model;

import net.atos.zac.app.shared.RESTListParameters;
import net.atos.zac.app.zoeken.model.RESTDatumRange;

public class RESTInboxDocumentListParameters extends RESTListParameters {
    public String titel;

    public String identificatie;

    public RESTDatumRange creatiedatum;

}
