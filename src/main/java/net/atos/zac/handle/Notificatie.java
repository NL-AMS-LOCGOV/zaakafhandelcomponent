/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.handle;

import static net.atos.client.zgw.shared.util.DateTimeUtil.DATE_TIME_FORMAT_WITH_MILLISECONDS;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Map;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbProperty;

/**
 *
 */
public class Notificatie {

    private final String kanaal;

    private final URI hoofdObject;

    private final String resource;

    private final URI resourceUrl;

    private final String actie;

    @JsonbDateFormat(DATE_TIME_FORMAT_WITH_MILLISECONDS)
    private final ZonedDateTime aanmaakdatum;

    private Map<String, String> kenmerken;

    @JsonbCreator
    public Notificatie(@JsonbProperty("kanaal") final String kanaal,
            @JsonbProperty("hoofdObject") final URI hoofdObject,
            @JsonbProperty("resource") final String resource,
            @JsonbProperty("resourceUrl") final URI resourceUrl,
            @JsonbProperty("actie") final String actie,
            @JsonbProperty("aanmaakdatum") final ZonedDateTime aanmaakdatum) {
        this.kanaal = kanaal;
        this.hoofdObject = hoofdObject;
        this.resource = resource;
        this.resourceUrl = resourceUrl;
        this.actie = actie;
        this.aanmaakdatum = aanmaakdatum;
    }

    public String getKanaal() {
        return kanaal;
    }

    public URI getHoofdObject() {
        return hoofdObject;
    }

    public String getResource() {
        return resource;
    }

    public URI getResourceUrl() {
        return resourceUrl;
    }

    public String getActie() {
        return actie;
    }

    public ZonedDateTime getAanmaakdatum() {
        return aanmaakdatum;
    }

    public Map<String, String> getKenmerken() {
        return kenmerken;
    }

    public void setKenmerken(final Map<String, String> kenmerken) {
        this.kenmerken = kenmerken;
    }
}
