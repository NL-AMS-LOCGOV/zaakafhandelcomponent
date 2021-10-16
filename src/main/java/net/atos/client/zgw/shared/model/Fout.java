/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared.model;

import static java.lang.String.format;

import java.net.URI;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

/**
 *
 */
public class Fout {

    // URI referentie naar het type fout, bedoeld voor developers
    private final URI type;

    // Systeemcode die het type fout aangeeft
    private final String code;

    // Generieke titel voor het type fout
    private final String title;

    // De HTTP status code
    private final int status;

    // Extra informatie bij de fout, indien beschikbaar
    private final String detail;

    // URI met referentie naar dit specifiek voorkomen van de fout.
    // Deze kan gebruikt worden in combinatie met server logs, bijvoorbeeld.
    private final URI instance;

    @JsonbCreator
    public Fout(@JsonbProperty("type") final URI type,
            @JsonbProperty("code") final String code,
            @JsonbProperty("title") final String title,
            @JsonbProperty("status") final int status,
            @JsonbProperty("detail") final String detail,
            @JsonbProperty("instance") final URI instance) {
        this.type = type;
        this.code = code;
        this.title = title;
        this.status = status;
        this.detail = detail;
        this.instance = instance;
    }

    public URI getType() {
        return type;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public int getStatus() {
        return status;
    }

    public String getDetail() {
        return detail;
    }

    public URI getInstance() {
        return instance;
    }

    @Override
    public String toString() {
        return format("(%d) Title: %s, Detail: %s", status, title, detail);
    }
}
