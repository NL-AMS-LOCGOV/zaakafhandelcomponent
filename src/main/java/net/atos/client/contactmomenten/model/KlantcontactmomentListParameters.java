/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.contactmomenten.model;

import java.net.URI;

import javax.ws.rs.QueryParam;

public class KlantcontactmomentListParameters {

    @QueryParam("contactmoment")
    private URI contactmoment;

    @QueryParam("klant")
    private URI klant;

    @QueryParam("rol")
    private String rol;

    @QueryParam("page")
    private Integer page;

    public URI getContactmoment() {
        return contactmoment;
    }

    public void setContactmoment(final URI contactmoment) {
        this.contactmoment = contactmoment;
    }

    public URI getKlant() {
        return klant;
    }

    public void setKlant(final URI klant) {
        this.klant = klant;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(final String rol) {
        this.rol = rol;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(final Integer page) {
        this.page = page;
    }
}
