/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.drc.model;

import javax.ws.rs.QueryParam;

import net.atos.client.zgw.shared.model.AbstractListParameters;

/**
 *
 */
public class EnkelvoudigInformatieObjectListParameters extends AbstractListParameters {

    /**
     * Een binnen een gegeven context ondubbelzinnige referentie naar het INFORMATIEOBJECT.
     */
    @QueryParam("identificatie")
    private String identificatie;

    /**
     * et RSIN van de Niet-natuurlijk persoon zijnde de organisatie die het informatieobject heeft gecreeerd of heeft ontvangen
     * en als eerste in een samenwerkingsketen heeft vastgelegd.
     */
    @QueryParam("bronorganisatie")
    private String bronorganisatie;


    public EnkelvoudigInformatieObjectListParameters(final String identificatie) {
        this.identificatie = identificatie;
    }

    public EnkelvoudigInformatieObjectListParameters() {
    }

    public String getIdentificatie() {
        return identificatie;
    }

    public void setIdentificatie(final String identificatie) {
        this.identificatie = identificatie;
    }

    public String getBronorganisatie() {
        return bronorganisatie;
    }

    public void setBronorganisatie(final String bronorganisatie) {
        this.bronorganisatie = bronorganisatie;
    }
}
