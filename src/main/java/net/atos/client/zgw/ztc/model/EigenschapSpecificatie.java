/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.ztc.model;

import java.util.List;

/**
 *
 */
public class EigenschapSpecificatie {

    /**
     * Benaming van het object of groepattribuut waarvan de EIGENSCHAP een inhoudelijk gegeven specificeert.
     * maxLength 32
     */
    private String groep;

    /**
     * Het soort tekens waarmee waarden van de EIGENSCHAP kunnen worden vastgelegd.
     * required
     */
    private Formaat formaat;

    /**
     * Het aantal karakters (lengte) waarmee waarden van de EIGENSCHAP worden vastgelegd.
     */
    private int lengte;

    /**
     * Het aantal mogelijke voorkomens van waarden van deze EIGENSCHAP bij een zaak van het ZAAKTYPE.
     */
    private int kardinaliteit;

    /**
     * Waarden die deze EIGENSCHAP kan hebben.
     * maxLength: 100
     */
    private List<String> waardenverzameling;

    public String getGroep() {
        return groep;
    }

    public void setGroep(final String groep) {
        this.groep = groep;
    }

    public Formaat getFormaat() {
        return formaat;
    }

    public void setFormaat(final Formaat formaat) {
        this.formaat = formaat;
    }

    public int getLengte() {
        return lengte;
    }

    public void setLengte(final int lengte) {
        this.lengte = lengte;
    }

    public int getKardinaliteit() {
        return kardinaliteit;
    }

    public void setKardinaliteit(final int kardinaliteit) {
        this.kardinaliteit = kardinaliteit;
    }

    public List<String> getWaardenverzameling() {
        return waardenverzameling;
    }

    public void setWaardenverzameling(final List<String> waardenverzameling) {
        this.waardenverzameling = waardenverzameling;
    }
}
