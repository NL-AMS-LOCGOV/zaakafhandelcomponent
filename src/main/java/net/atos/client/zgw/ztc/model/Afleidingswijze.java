/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.ztc.model;

import javax.json.bind.annotation.JsonbTypeAdapter;

import net.atos.client.zgw.shared.model.AbstractEnum;

/**
 *
 */
@JsonbTypeAdapter(Afleidingswijze.Adapter.class)
public enum Afleidingswijze implements AbstractEnum<Afleidingswijze> {
    /**
     * De termijn start op de datum waarop de zaak is afgehandeld (ZAAK.Einddatum in het RGBZ).
     */
    AFGEHANDELD("afgehandeld"),

    /**
     * De termijn start op de datum die is vastgelegd in een ander datumveld dan de datumvelden waarop de overige waarden
     * (van deze attribuutsoort) betrekking hebben. `Objecttype`, `Registratie` en `Datumkenmerk` zijn niet leeg.
     */
    ANDER_DATUMKENMERK("ander_datumkenmerk"),

    /**
     * De termijn start op de datum die vermeld is in een zaaktype-specifieke eigenschap (zijnde een `datumveld`).
     * `Resultaattype.ZaakType` heeft een `Eigenschap`; `Objecttype`, en `Datumkenmerk` zijn niet leeg.
     */
    EIGENSCHAP("eigenschap"),

    /**
     * De termijn start op de datum waarop de gerelateerde zaak is afgehandeld (`ZAAK.Einddatum` of `ZAAK.Gerelateerde_zaak.Einddatum` in het RGBZ).
     * `Resultaattype.ZaakType` heeft gerelateerd `ZaakType
     */
    GERELATEERDE_ZAAK("gerelateerde_zaak"),

    /**
     * De termijn start op de datum waarop de gerelateerde zaak is afgehandeld, waarvan de zaak een deelzaak is (`ZAAK.Einddatum` van de hoofdzaak in het RGBZ).
     * Resultaattype.ZaakType is deelzaaktype van ZaakType.
     */
    HOOFDZAAK("hoofdzaak"),

    /**
     * De termijn start op de datum waarop het besluit van kracht wordt (`BESLUIT.Ingangsdatum` in het RGBZ).\tResultaatType.ZaakType heeft relevant BesluitTyp
     */
    INGANGSDATUM_BESLUIT("ingangsdatum_besluit"),

    /**
     * De termijn start een vast aantal jaren na de datum waarop de zaak is afgehandeld (`ZAAK.Einddatum` in het RGBZ).
     */
    TERMIJN("termijn"),

    /**
     * De termijn start op de dag na de datum waarop het besluit vervalt (`BESLUIT.Vervaldatum` in het RGBZ).
     * Resultaattype.ZaakType heeft relevant BesluitType
     */
    VERVALDATUM_BESLUIT("vervaldatum_besluit"),

    /**
     * De termijn start op de einddatum geldigheid van het zaakobject waarop de zaak betrekking heeft (bijvoorbeeld de overlijdendatum van een Persoon).
     * M.b.v. de attribuutsoort `Objecttype` wordt vastgelegd om welke zaakobjecttype het gaat;
     * m.b.v. de attribuutsoort `Datumkenmerk` wordt vastgelegd welke datum-attribuutsoort van het zaakobjecttype het betreft.
     */
    ZAAKOBJECT("zaakobject");

    private final String value;

    Afleidingswijze(final String value) {
        this.value = value;
    }

    @Override
    public String toValue() {
        return value;
    }

    public static Afleidingswijze fromValue(final String value) {
        return AbstractEnum.fromValue(values(), value);
    }

    static class Adapter extends AbstractEnum.Adapter<Afleidingswijze> {

        @Override
        protected Afleidingswijze[] getEnums() {
            return values();
        }
    }
}
