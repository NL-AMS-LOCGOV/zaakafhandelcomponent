/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.ztc.model;

import java.time.Period;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

/**
 * Specificatie voor het bepalen van de brondatum voor de start van de Archiefactietermijn (=brondatum) van het zaakdossier.
 */
public class BrondatumArchiefprocedure {

    public static final int DATUMKENMERK_MAX_LENGTH = 80;

    public static final int REGISTRATIE_MAX_LENGTH = 80;

    /**
     * Afleidingswijze brondatum
     */
    private Afleidingswijze afleidingswijze;

    /**
     * Naam van de attribuutsoort van het procesobject dat bepalend is voor het einde van de procestermijn.
     * maxLength: {@link BrondatumArchiefprocedure#DATUMKENMERK_MAX_LENGTH}
     */
    private String datumkenmerk;

    /**
     * Indicatie dat de einddatum van het procesobject gedurende de uitvoering van de zaak bekend moet worden.
     * Indien deze nog niet bekend is en deze waarde staat op `true`, dan kan de zaak (nog) niet afgesloten worden.
     */
    private Boolean einddatumBekend;

    /**
     * Het soort object in de registratie dat het procesobject representeert.
     */
    private Objecttype objecttype;

    /**
     * De naam van de registratie waarvan het procesobject deel uit maakt.
     * maxLength: {@link BrondatumArchiefprocedure#REGISTRATIE_MAX_LENGTH}
     */
    private String registratie;

    /**
     * De periode dat het zaakdossier na afronding van de zaak actief gebruikt en/of geraadpleegd wordt ter ondersteuning van de taakuitoefening van de organisatie.
     * Enkel relevant indien de afleidingswijze 'termijn' is.
     */
    private Period procestermijn;

    /**
     * Constructor with required attributes for POST and PUT requests and GET response
     */
    @JsonbCreator
    public BrondatumArchiefprocedure(@JsonbProperty("afleidingswijze") final Afleidingswijze afleidingswijze) {
        this.afleidingswijze = afleidingswijze;
    }

    public Afleidingswijze getAfleidingswijze() {
        return afleidingswijze;
    }

    public String getDatumkenmerk() {
        return datumkenmerk;
    }

    public void setDatumkenmerk(final String datumkenmerk) {
        this.datumkenmerk = datumkenmerk;
    }

    public Boolean getEinddatumBekend() {
        return einddatumBekend;
    }

    public void setEinddatumBekend(final Boolean einddatumBekend) {
        this.einddatumBekend = einddatumBekend;
    }

    public Objecttype getObjecttype() {
        return objecttype;
    }

    public void setObjecttype(final Objecttype objecttype) {
        this.objecttype = objecttype;
    }

    public String getRegistratie() {
        return registratie;
    }

    public void setRegistratie(final String registratie) {
        this.registratie = registratie;
    }

    public Period getProcestermijn() {
        return procestermijn;
    }

    public void setProcestermijn(final Period procestermijn) {
        this.procestermijn = procestermijn;
    }
}
