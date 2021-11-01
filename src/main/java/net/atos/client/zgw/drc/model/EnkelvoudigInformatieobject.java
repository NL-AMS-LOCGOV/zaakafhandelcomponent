/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.drc.model;

import static net.atos.client.zgw.shared.util.DateTimeUtil.DATE_TIME_FORMAT_WITH_MILLISECONDS;

import java.net.URI;
import java.time.ZonedDateTime;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbProperty;

/**
 * Representation of EnkelvoudigInformatieobject for GET response only
 */
public class EnkelvoudigInformatieobject extends AbstractEnkelvoudigInformatieobject {

    /**
     * Download URL van de binaire inhoud.
     */
    private final URI inhoud;

    /**
     * Constructor with readOnly attributes for GET response
     */
    @JsonbCreator
    public EnkelvoudigInformatieobject(@JsonbProperty("url") final URI url,
            @JsonbProperty("versie") final Integer versie,
            @JsonbProperty("beginRegistratie") @JsonbDateFormat(DATE_TIME_FORMAT_WITH_MILLISECONDS) final ZonedDateTime beginRegistratie,
            @JsonbProperty("bestandsomvang") final Long bestandsomvang,
            @JsonbProperty("locked") final Boolean locked,
            @JsonbProperty("inhoud") final URI inhoud) {
        super(url, versie, beginRegistratie, bestandsomvang, locked);
        this.inhoud = inhoud;
    }

    public URI getInhoud() {
        return inhoud;
    }
}
