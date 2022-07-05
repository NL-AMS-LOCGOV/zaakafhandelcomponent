/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.documenten.model;

import net.atos.zac.shared.model.ListParameters;
import net.atos.zac.zoeken.model.DatumRange;


public class OntkoppeldDocumentListParameters extends ListParameters {

    private String titel;

    private DatumRange creatiedatum;

    private String zaakID;

    private String ontkoppeldDoor;

    private DatumRange ontkoppeldOp;

    private String reden;

    public OntkoppeldDocumentListParameters() {
        super();
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(final String titel) {
        this.titel = titel;
    }

    public String getReden() {
        return reden;
    }

    public void setReden(final String reden) {
        this.reden = reden;
    }

    public DatumRange getCreatiedatum() {
        return creatiedatum;
    }

    public void setCreatiedatum(final DatumRange creatiedatum) {
        this.creatiedatum = creatiedatum;
    }

    public String getOntkoppeldDoor() {
        return ontkoppeldDoor;
    }

    public void setOntkoppeldDoor(final String ontkoppeldDoor) {
        this.ontkoppeldDoor = ontkoppeldDoor;
    }

    public String getZaakID() {
        return zaakID;
    }

    public void setZaakID(final String zaakID) {
        this.zaakID = zaakID;
    }

    public DatumRange getOntkoppeldOp() {
        return ontkoppeldOp;
    }

    public void setOntkoppeldOp(final DatumRange ontkoppeldOp) {
        this.ontkoppeldOp = ontkoppeldOp;
    }
}
