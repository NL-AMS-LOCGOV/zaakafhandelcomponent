/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.bag.model;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

public class BevraagAdressenParameters {

    @QueryParam("zoekresultaatIdentificatie")
    String zoekresultaatIdentificatie;

    @QueryParam("postcode")
    String postcode;

    @QueryParam("huisnummer")
    Integer huisnummer;

    @QueryParam("huisnummertoevoeging")
    String huisnummertoevoeging;

    @QueryParam("huisletter")
    String huisletter;

    @QueryParam("exacteMatch")
    @DefaultValue("false")
    Boolean exacteMatch;

    @QueryParam("adresseerbaarObjectIdentificatie")
    String adresseerbaarObjectIdentificatie;

    @QueryParam("woonplaatsNaam")
    String woonplaatsNaam;

    @QueryParam("openbareRuimteNaam")
    String openbareRuimteNaam;

    @QueryParam("pandIdentificatie")
    String pandIdentificatie;

    @QueryParam("expand")
    String expand;

    @QueryParam("page")
    @DefaultValue("1")
    Integer page;

    @QueryParam("pageSize")
    @DefaultValue("20")
    Integer pageSize;

    @QueryParam("q")
    String q;

    @QueryParam("inclusiefEindStatus")
    @DefaultValue("false")
    Boolean inclusiefEindStatus;

    public String getZoekresultaatIdentificatie() {
        return zoekresultaatIdentificatie;
    }

    public void setZoekresultaatIdentificatie(final String zoekresultaatIdentificatie) {
        this.zoekresultaatIdentificatie = zoekresultaatIdentificatie;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(final String postcode) {
        this.postcode = postcode;
    }

    public Integer getHuisnummer() {
        return huisnummer;
    }

    public void setHuisnummer(final Integer huisnummer) {
        this.huisnummer = huisnummer;
    }

    public String getHuisnummertoevoeging() {
        return huisnummertoevoeging;
    }

    public void setHuisnummertoevoeging(final String huisnummertoevoeging) {
        this.huisnummertoevoeging = huisnummertoevoeging;
    }

    public String getHuisletter() {
        return huisletter;
    }

    public void setHuisletter(final String huisletter) {
        this.huisletter = huisletter;
    }

    public Boolean getExacteMatch() {
        return exacteMatch;
    }

    public void setExacteMatch(final Boolean exacteMatch) {
        this.exacteMatch = exacteMatch;
    }

    public String getAdresseerbaarObjectIdentificatie() {
        return adresseerbaarObjectIdentificatie;
    }

    public void setAdresseerbaarObjectIdentificatie(final String adresseerbaarObjectIdentificatie) {
        this.adresseerbaarObjectIdentificatie = adresseerbaarObjectIdentificatie;
    }

    public String getWoonplaatsNaam() {
        return woonplaatsNaam;
    }

    public void setWoonplaatsNaam(final String woonplaatsNaam) {
        this.woonplaatsNaam = woonplaatsNaam;
    }

    public String getOpenbareRuimteNaam() {
        return openbareRuimteNaam;
    }

    public void setOpenbareRuimteNaam(final String openbareRuimteNaam) {
        this.openbareRuimteNaam = openbareRuimteNaam;
    }

    public String getPandIdentificatie() {
        return pandIdentificatie;
    }

    public void setPandIdentificatie(final String pandIdentificatie) {
        this.pandIdentificatie = pandIdentificatie;
    }

    public String getExpand() {
        return expand;
    }

    public void setExpand(final String expand) {
        this.expand = expand;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(final Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(final Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getQ() {
        return q;
    }

    public void setQ(final String q) {
        this.q = q;
    }

    public Boolean getInclusiefEindStatus() {
        return inclusiefEindStatus;
    }

    public void setInclusiefEindStatus(final Boolean inclusiefEindStatus) {
        this.inclusiefEindStatus = inclusiefEindStatus;
    }
}
