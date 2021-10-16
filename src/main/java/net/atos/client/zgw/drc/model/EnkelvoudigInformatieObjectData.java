/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.drc.model;

import static net.atos.client.zgw.shared.util.DateTimeUtil.DATE_TIME_FORMAT_WITH_MILLISECONDS;

import java.net.URI;
import java.time.LocalDate;
import java.time.ZonedDateTime;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbProperty;

/**
 * Representation of EnkelvoudigInformatieObject for POST request and response only
 */
public class EnkelvoudigInformatieObjectData extends AbstractEnkelvoudigInformatieObject {

    /**
     * Binaire inhoud, in base64 ge-encodeerd.
     */
    private final String inhoud;

    /**
     * Constructor with required attributes for POST request
     */
    public EnkelvoudigInformatieObjectData(final String bronorganisatie, final LocalDate creatiedatum, final String titel, final String auteur,
            final String taal, final URI informatieobjecttype, final String inhoud) {
        super(bronorganisatie, creatiedatum, titel, auteur, taal, informatieobjecttype);
        this.inhoud = inhoud;
    }

    /**
     * Constructor with readOnly attributes for POST response
     */
    @JsonbCreator
    public EnkelvoudigInformatieObjectData(@JsonbProperty("url") final URI url,
            @JsonbProperty("versie") final Integer versie,
            @JsonbProperty("beginRegistratie") @JsonbDateFormat(DATE_TIME_FORMAT_WITH_MILLISECONDS) final ZonedDateTime beginRegistratie,
            @JsonbProperty("bestandsomvang") final Long bestandsomvang,
            @JsonbProperty("locked") final Boolean locked,
            @JsonbProperty("inhoud") final String inhoud) {
        super(url, versie, beginRegistratie, bestandsomvang, locked);
        this.inhoud = inhoud;
    }

    public String getInhoud() {
        return inhoud;
    }
}
