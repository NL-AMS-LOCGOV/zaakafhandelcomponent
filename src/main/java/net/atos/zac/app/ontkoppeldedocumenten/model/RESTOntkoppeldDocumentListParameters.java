/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.ontkoppeldedocumenten.model;

import net.atos.zac.app.identity.model.RESTUser;
import net.atos.zac.app.shared.RESTListParameters;
import net.atos.zac.app.zoeken.model.RESTDatumRange;

public class RESTOntkoppeldDocumentListParameters extends RESTListParameters {
    public String titel;

    public String reden;

    public RESTDatumRange creatiedatum;

    public RESTUser ontkoppeldDoor;

    public RESTDatumRange ontkoppeldOp;

    public String zaakID;
}
