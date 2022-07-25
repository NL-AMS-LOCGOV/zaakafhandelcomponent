/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.bag.model;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

public class RaadpleegAdressenParameters {

    @QueryParam("pandIdentificatie")
    private String pandIdentificatie;

    @QueryParam("adresseerbaarObjectIdentificatie")
    private String adresseerbaarObjectIdentificatie;

    @QueryParam("zoekresultaatIdentificatie")
    private String zoekresultaatIdentificatie;

    @QueryParam("expand")
    private String expand;

    @QueryParam("fields")
    private String fields;

    @QueryParam("page")
    @DefaultValue("1")
    private Integer page;

    @QueryParam("pageSize")
    @DefaultValue("20")
    private Integer pageSize;

    @QueryParam("postcode")
    private String postcode;

    @QueryParam("huisnummer")
    private Integer huisnummer;

    @QueryParam("huisletter")
    private String huisletter;

    @QueryParam("huisnummertoevoeging")
    private String huisnummertoevoeging;

    @QueryParam("exacteMatch")
    @DefaultValue("false")
    private Boolean exacteMatch;

    @QueryParam("q")
    private String q;

    public String getPandIdentificatie() {
        return pandIdentificatie;
    }

    public void setPandIdentificatie(final String pandIdentificatie) {
        this.pandIdentificatie = pandIdentificatie;
    }

    public String getAdresseerbaarObjectIdentificatie() {
        return adresseerbaarObjectIdentificatie;
    }

    public void setAdresseerbaarObjectIdentificatie(final String adresseerbaarObjectIdentificatie) {
        this.adresseerbaarObjectIdentificatie = adresseerbaarObjectIdentificatie;
    }

    public String getZoekresultaatIdentificatie() {
        return zoekresultaatIdentificatie;
    }

    public void setZoekresultaatIdentificatie(final String zoekresultaatIdentificatie) {
        this.zoekresultaatIdentificatie = zoekresultaatIdentificatie;
    }

    public String getExpand() {
        return expand;
    }

    public void setExpand(final String expand) {
        this.expand = expand;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(final String fields) {
        this.fields = fields;
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

    public String getHuisletter() {
        return huisletter;
    }

    public void setHuisletter(final String huisletter) {
        this.huisletter = huisletter;
    }

    public String getHuisnummertoevoeging() {
        return huisnummertoevoeging;
    }

    public void setHuisnummertoevoeging(final String huisnummertoevoeging) {
        this.huisnummertoevoeging = huisnummertoevoeging;
    }

    public Boolean getExacteMatch() {
        return exacteMatch;
    }

    public void setExacteMatch(final Boolean exacteMatch) {
        this.exacteMatch = exacteMatch;
    }

    public String getQ() {
        return q;
    }

    public void setQ(final String q) {
        this.q = q;
    }
}
