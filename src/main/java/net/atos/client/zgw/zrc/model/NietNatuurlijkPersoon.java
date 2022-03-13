/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model;

public class NietNatuurlijkPersoon {

    /**
     * Het door een kamer toegekend uniek nummer voor de INGESCHREVEN NIET-NATUURLIJK PERSOON
     * - maxLength: 9
     */
    private String innNnpId;

    /**
     * Het door de gemeente uitgegeven unieke nummer voor een ANDER NIET-NATUURLIJK PERSOON
     * - maxLength: 17
     */
    private String annIdentificatie;

    /**
     * Naam van de niet-natuurlijke persoon zoals deze is vastgelegd in de statuten (rechtspersoon) of in de vennootschapsovereenkomst is overeengekomen
     * (Vennootschap onder firma of Commanditaire vennootschap).
     * - maxLength: 500
     */
    private String statutaireNaam;

    /**
     * De juridische vorm van de NIET-NATUURLIJK PERSOON.
     */
    private Rechtsvorm innRechtsvorm;

    /**
     * De gegevens over het adres van de NIET-NATUURLIJK PERSOON
     * - maxLength: 1000
     */
    private String bezoekadres;

    private SubVerblijfBuitenland subVerblijfBuitenland;

    public NietNatuurlijkPersoon() {
    }

    public NietNatuurlijkPersoon(final String innNnpId) {
        this.innNnpId = innNnpId;
    }

    public String getInnNnpId() {
        return innNnpId;
    }

    public void setInnNnpId(final String innNnpId) {
        this.innNnpId = innNnpId;
    }

    public String getAnnIdentificatie() {
        return annIdentificatie;
    }

    public void setAnnIdentificatie(final String annIdentificatie) {
        this.annIdentificatie = annIdentificatie;
    }

    public String getStatutaireNaam() {
        return statutaireNaam;
    }

    public void setStatutaireNaam(final String statutaireNaam) {
        this.statutaireNaam = statutaireNaam;
    }

    public Rechtsvorm getInnRechtsvorm() {
        return innRechtsvorm;
    }

    public void setInnRechtsvorm(final Rechtsvorm innRechtsvorm) {
        this.innRechtsvorm = innRechtsvorm;
    }

    public String getBezoekadres() {
        return bezoekadres;
    }

    public void setBezoekadres(final String bezoekadres) {
        this.bezoekadres = bezoekadres;
    }

    public SubVerblijfBuitenland getSubVerblijfBuitenland() {
        return subVerblijfBuitenland;
    }

    public void setSubVerblijfBuitenland(final SubVerblijfBuitenland subVerblijfBuitenland) {
        this.subVerblijfBuitenland = subVerblijfBuitenland;
    }
}
