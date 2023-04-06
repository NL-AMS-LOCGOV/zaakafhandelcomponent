/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model.zaakobjecten;

/**
 * ObjectWoonplaats
 */
public class ObjectWoonplaats extends ObjectBAGObject {

    /**
     * De door het bevoegde gemeentelijke orgaan aan een WOONPLAATS toegekende benaming.
     * - maxLength: 80
     * - required
     */
    private String woonplaatsNaam;

    /**
     * Constructor for JSONB deserialization
     */
    public ObjectWoonplaats() {
    }

    /**
     * Constructor with required attributes
     */
    public ObjectWoonplaats(final String identificatie, final String woonplaatsNaam) {
        super(identificatie);
        this.woonplaatsNaam = woonplaatsNaam;
    }

    public String getWoonplaatsNaam() {
        return woonplaatsNaam;
    }

    public void setWoonplaatsNaam(final String woonplaatsNaam) {
        this.woonplaatsNaam = woonplaatsNaam;
    }

}
