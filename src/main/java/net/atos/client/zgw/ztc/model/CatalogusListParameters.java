/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.ztc.model;

import javax.ws.rs.QueryParam;

public class CatalogusListParameters {

    /**
     * Een afkorting waarmee wordt aangegeven voor welk domein in een CATALOGUS ZAAKTYPEn zijn uitgewerkt.
     */
    @QueryParam("domein")
    private String domein;

    /**
     * Multiple values may be separated by commas.
     */
    @QueryParam("domein__in")
    private String domeinIn;

    /**
     * Het door een kamer toegekend uniek nummer voor de INGESCHREVEN NIET-NATUURLIJK PERSOON die de eigenaar is van een CATALOGUS.
     */
    @QueryParam("rsin")
    private String rsin;

    /**
     * Multiple values may be separated by commas.
     */
    @QueryParam("rsin__in")
    private String rsinIn;

    /**
     * en pagina binnen de gepagineerde set resultaten.
     */
    @QueryParam("page")
    private Integer page;

    public String getDomein() {
        return domein;
    }

    public void setDomein(final String domein) {
        this.domein = domein;
    }

    public String getDomeinIn() {
        return domeinIn;
    }

    public void setDomeinIn(final String domeinIn) {
        this.domeinIn = domeinIn;
    }

    public String getRsin() {
        return rsin;
    }

    public void setRsin(final String rsin) {
        this.rsin = rsin;
    }

    public String getRsinIn() {
        return rsinIn;
    }

    public void setRsinIn(final String rsinIn) {
        this.rsinIn = rsinIn;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(final Integer page) {
        this.page = page;
    }
}
