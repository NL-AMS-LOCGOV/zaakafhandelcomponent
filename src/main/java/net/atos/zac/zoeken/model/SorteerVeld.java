/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zoeken.model;

public enum SorteerVeld {
    IDENTIFICATIE("identificatie"),
    ZAAK_ZAAKTYPE("zaak_zaaktypeOmschrijving"),
    ZAAK_BEHANDELAAR("zaak_behandelaarNaam"),
    ZAAK_GROEP("zaak_groepNaam"),
    ZAAK_REGISTRATIEDATUM("zaak_registratiedatum"),
    ZAAK_STARTDATUM("zaak_startdatum"),
    ZAAK_EINDDATUMGEPLAND("zaak_einddatumGepland"),
    ZAAK_EINDDATUM("zaak_einddatum"),
    ZAAK_UITERLIJKEEINDDATUMAFDOENING("zaak_uiterlijkeEinddatumAfdoening"),
    ZAAK_COMMUNICATIEKANAAL("zaak_communicatiekanaal"),
    ZAAK_STATUSTYPEOMSCHRIJVING("zaak_statustypeOmschrijving"),
    ZAAK_RESULTAATTYPEOMSCHRIJVING("zaak_resultaattypeOmschrijving"),
    INFORMATIEOBJECT_TITEL("informatieobject_titel_sort"),
    INFORMATIEOBJECT_ZAAKIDENTIFICATIE("informatieobject_zaakIdentificatie"),
    INFORMATIEOBJECT_CREATIEDATUM("informatieobject_creatiedatum"),
    INFORMATIEOBJECT_AUTEUR("informatieobject_auteur_sort"),
    INFORMATIEOBJECT_STATUS("informatieobject_status"),
    INFORMATIEOBJECT_REGISTRATIEDATUM("informatieobject_registratiedatum"),
    INFORMATIEOBJECT_DOCUMENTTYPE("informatieobject_documentType"),
    INFORMATIEOBJECT_ONTVANGSTDATUM("informatieobject_ontvangstdatum"),
    INFORMATIEOBJECT_VEZENDDATUM("informatieobject_vezenddatum");

    private final String veld;

    SorteerVeld(final String veld) {
        this.veld = veld;
    }

    public String getVeld() {
        return veld;
    }

    public static SorteerVeld fromValue(final String veld) {
        for (final SorteerVeld sv : SorteerVeld.values()) {
            if (String.valueOf(sv.veld).equals(veld)) {
                return sv;
            }
        }
        throw new IllegalArgumentException("Onbekend SorteerVeld '" + veld + "'");
    }
}
