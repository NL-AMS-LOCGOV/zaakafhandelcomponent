/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zoeken.model;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Stream;

public enum FilterVeld {

    TYPE("type"),
    ZAAKTYPE("zaaktypeOmschrijving"),
    BEHANDELAAR("behandelaarNaam"),
    GROEP("groepNaam"),
    TOEGEKEND("isToegekend"),

    ZAAK_STATUS("zaak_statustypeOmschrijving"),
    ZAAK_ZAAKTYPE("zaak_zaaktypeOmschrijving"),
    ZAAK_ZAAKTYPE_UUID("zaak_zaaktypeUuid"),
    ZAAK_BEHANDELAAR("zaak_behandelaarNaam"),
    ZAAK_GROEP("zaak_groepNaam"),
    ZAAK_RESULTAAT("zaak_resultaattypeOmschrijving"),
    ZAAK_INDICATIES("zaak_indicaties"),
    ZAAK_COMMUNICATIEKANAAL("zaak_communicatiekanaal"),
    ZAAK_VERTROUWELIJKHEIDAANDUIDING("zaak_vertrouwelijkheidaanduiding"),
    ZAAK_ARCHIEF_NOMINATIE("zaak_archiefNominatie"),

    TAAK_NAAM("taak_naam"),
    TAAK_STATUS("taak_status"),
    TAAK_ZAAKTYPE("taak_zaaktypeOmschrijving"),
    TAAK_BEHANDELAAR("taak_behandelaarNaam"),
    TAAK_GROEP("taak_groepNaam"),

    DOCUMENT_STATUS("informatieobject_status"),
    DOCUMENT_TYPE("informatieobject_documentType"),
    DOCUMENT_VERGRENDELD_DOOR("informatieobject_vergrendeldDoorNaam"),
    DOCUMENT_INDICATIES("informatieobject_indicaties");

    public static final Set<FilterVeld> ZAAK_FACETTEN = Collections.unmodifiableSet(
            EnumSet.of(ZAAKTYPE, ZAAK_STATUS, BEHANDELAAR, GROEP, ZAAK_RESULTAAT, ZAAK_VERTROUWELIJKHEIDAANDUIDING, ZAAK_COMMUNICATIEKANAAL,
                       ZAAK_ARCHIEF_NOMINATIE, ZAAK_INDICATIES));

    public static final Set<FilterVeld> DOCUMENT_FACETTEN = Collections.unmodifiableSet(
            EnumSet.of(DOCUMENT_STATUS, DOCUMENT_TYPE, DOCUMENT_VERGRENDELD_DOOR, ZAAKTYPE, DOCUMENT_INDICATIES));

    public static final Set<FilterVeld> TAAK_FACETTEN = Collections.unmodifiableSet(
            EnumSet.of(TAAK_NAAM, TAAK_STATUS, GROEP, BEHANDELAAR, ZAAKTYPE));

    public static final Set<FilterVeld> FACETTEN = Collections.unmodifiableSet(
            EnumSet.of(TYPE, ZAAKTYPE, TOEGEKEND, BEHANDELAAR, GROEP, ZAAK_STATUS, ZAAK_INDICATIES, ZAAK_RESULTAAT,
                       ZAAK_VERTROUWELIJKHEIDAANDUIDING, ZAAK_COMMUNICATIEKANAAL, ZAAK_ARCHIEF_NOMINATIE,
                       TAAK_NAAM, TAAK_STATUS, DOCUMENT_STATUS, DOCUMENT_INDICATIES, DOCUMENT_TYPE,
                       DOCUMENT_VERGRENDELD_DOOR));

    private final String veld;

    FilterVeld(final String veld) {
        this.veld = veld;
    }

    public String getVeld() {
        return veld;
    }

    public static FilterVeld fromValue(final String veld) {
        return Stream.of(FilterVeld.values())
                .filter(filter -> String.valueOf(filter.veld).equals(veld))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Onbekend Filterveld '%s'", veld)));
    }

    public static Set<FilterVeld> getFacetten() {
        return FACETTEN;
    }

    public static Set<FilterVeld> getTaakFacetten() {
        return TAAK_FACETTEN;
    }

    public static Set<FilterVeld> getDocumentFacetten() {
        return DOCUMENT_FACETTEN;
    }

    public static Set<FilterVeld> getZaakFacetten() {
        return ZAAK_FACETTEN;
    }
}
