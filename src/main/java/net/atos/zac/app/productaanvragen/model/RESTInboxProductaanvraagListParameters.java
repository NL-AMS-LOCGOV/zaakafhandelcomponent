/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.productaanvragen.model;

import net.atos.zac.app.shared.RESTListParameters;
import net.atos.zac.app.zoeken.model.RESTDatumRange;

public class RESTInboxProductaanvraagListParameters extends RESTListParameters {

    public String type;

    public RESTDatumRange ontvangstdatum;

    public String initiatorID;
}
