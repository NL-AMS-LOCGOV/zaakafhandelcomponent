/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model;

public class OrganisatorischeEenheid {

    /**
     * Een korte identificatie van de organisatorische eenheid.
     * - maxLength: 24
     */
    private String identificatie;

    /**
     * De feitelijke naam van de organisatorische eenheid.
     * - maxLength: 50
     */
    private String naam;

    /**
     * - maxLength: 24
     */
    private String isGehuisvestIn;

    public String getIdentificatie() {
        return identificatie;
    }

    public void setIdentificatie(final String identificatie) {
        this.identificatie = identificatie;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(final String naam) {
        this.naam = naam;
    }

    public String getIsGehuisvestIn() {
        return isGehuisvestIn;
    }

    public void setIsGehuisvestIn(final String isGehuisvestIn) {
        this.isGehuisvestIn = isGehuisvestIn;
    }
}
