/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.aanvraag.model;

import net.atos.zac.shared.model.ListParameters;
import net.atos.zac.zoeken.model.DatumRange;


public class InboxProductaanvraagListParameters extends ListParameters {

    private DatumRange ontvangstdatum;

    private String type;

    private String initiatorID;


    public InboxProductaanvraagListParameters() {
        super();
    }

    public DatumRange getOntvangstdatum() {
        return ontvangstdatum;
    }

    public void setOntvangstdatum(final DatumRange ontvangstdatum) {
        this.ontvangstdatum = ontvangstdatum;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getInitiatorID() {
        return initiatorID;
    }

    public void setInitiatorID(final String initiatorID) {
        this.initiatorID = initiatorID;
    }
}
