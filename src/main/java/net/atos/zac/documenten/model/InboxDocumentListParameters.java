/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.documenten.model;

import net.atos.zac.shared.model.ListParameters;
import net.atos.zac.zoeken.model.DatumRange;


public class InboxDocumentListParameters extends ListParameters {

    private String titel;

    private String identificatie;

    private DatumRange creatiedatum;

    public InboxDocumentListParameters() {
        super();
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(final String titel) {
        this.titel = titel;
    }

    public String getIdentificatie() {
        return identificatie;
    }

    public void setIdentificatie(final String identificatie) {
        this.identificatie = identificatie;
    }

    public DatumRange getCreatiedatum() {
        return creatiedatum;
    }

    public void setCreatiedatum(final DatumRange creatiedatum) {
        this.creatiedatum = creatiedatum;
    }
}
