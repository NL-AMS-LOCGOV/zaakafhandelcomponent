/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.documenten.model;

import java.time.LocalDate;

import net.atos.zac.shared.model.ListParameters;


public class OntkoppeldDocumentListParameters extends ListParameters {

    private String titel;

    private String identificatie;

    private LocalDate creatiedatum;

    private String ontkoppeldDoor;

    private String zaakId;

    private LocalDate ontkoppeldOp;

    public OntkoppeldDocumentListParameters() {
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

    public LocalDate getCreatiedatum() {
        return creatiedatum;
    }

    public void setCreatiedatum(final LocalDate creatiedatum) {
        this.creatiedatum = creatiedatum;
    }

    public String getOntkoppeldDoor() {
        return ontkoppeldDoor;
    }

    public void setOntkoppeldDoor(final String ontkoppeldDoor) {
        this.ontkoppeldDoor = ontkoppeldDoor;
    }

    public String getZaakId() {
        return zaakId;
    }

    public void setZaakId(final String zaakId) {
        this.zaakId = zaakId;
    }

    public LocalDate getOntkoppeldOp() {
        return ontkoppeldOp;
    }

    public void setOntkoppeldOp(final LocalDate ontkoppeldOp) {
        this.ontkoppeldOp = ontkoppeldOp;
    }
}
