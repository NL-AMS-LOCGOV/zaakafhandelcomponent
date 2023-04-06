/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model.zaakobjecten;

/**
 * ObjectOpenbareRuimte
 */
public class ObjectOpenbareRuimte extends ObjectBAGObject {

    /**
     * De door het bevoegde gemeentelijke orgaan aan een WOONPLAATS toegekende benaming.
     * - maxLength: 80
     * - required
     */
    private String wplWoonplaatsNaam;

    /**
     * Een door het bevoegde gemeentelijke orgaan aan een OPENBARE RUIMTE toegekende benaming
     * - maxLength: 80
     * - required
     */
    private String gorOpenbareRuimteNaam;

    /**
     * Constructor for JSONB deserialization
     */
    public ObjectOpenbareRuimte() {
    }

    /**
     * Constructor with required attributes
     */
    public ObjectOpenbareRuimte(final String identificatie, final String gorOpenbareRuimteNaam, final String wplWoonplaatsNaam) {
        super(identificatie);
        this.wplWoonplaatsNaam = wplWoonplaatsNaam;
        this.gorOpenbareRuimteNaam = gorOpenbareRuimteNaam;
    }

    public String getWplWoonplaatsNaam() {
        return wplWoonplaatsNaam;
    }

    public void setWplWoonplaatsNaam(final String wplWoonplaatsNaam) {
        this.wplWoonplaatsNaam = wplWoonplaatsNaam;
    }

    public String getGorOpenbareRuimteNaam() {
        return gorOpenbareRuimteNaam;
    }

    public void setGorOpenbareRuimteNaam(final String gorOpenbareRuimteNaam) {
        this.gorOpenbareRuimteNaam = gorOpenbareRuimteNaam;
    }
}
