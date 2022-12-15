/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model;

import static net.atos.client.zgw.shared.util.DateTimeUtil.DATE_TIME_FORMAT_WITH_MILLISECONDS;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.UUID;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbTypeDeserializer;

import net.atos.client.zgw.zrc.util.RolJsonbDeserializer;
import net.atos.client.zgw.ztc.model.Roltype;

/**
 *
 */
@JsonbTypeDeserializer(RolJsonbDeserializer.class)
public abstract class Rol<BETROKKENE_IDENTIFICATIE> {

    public static final String BETROKKENE_TYPE_NAAM = "betrokkeneType";

    /**
     * URL-referentie naar dit object.
     * Dit is de unieke identificatie en locatie van dit object.
     */
    private URI url;

    /**
     * Unieke resource identifier (UUID4)
     */
    private UUID uuid;

    /**
     * URL-referentie naar de ZAAK.
     * - Required
     */
    private URI zaak;

    /**
     * URL-referentie naar een betrokkene gerelateerd aan de ZAAK.
     */
    private URI betrokkene;

    /**
     * De generieke betrokkene
     * - Required
     */
    private BETROKKENE_IDENTIFICATIE betrokkeneIdentificatie;

    /**
     * Betrokkene type
     * - Required
     */
    private BetrokkeneType betrokkeneType;

    /**
     * URL-referentie naar een roltype binnen het ZAAKTYPE van de ZAAK.
     * - Required
     */
    private URI roltype;

    /**
     * Omschrijving van de aard van de ROL, afgeleid uit het ROLTYPE.
     */
    private String omschrijving;

    /**
     * Algemeen gehanteerde benaming van de aard van de ROL, afgeleid uit het ROLTYPE.
     * Uitleg bij mogelijke waarden:
     * 'adviseur' - (Adviseur) Kennis in dienst stellen van de behandeling van (een deel van) een zaak.
     * 'behandelaar' - (Behandelaar) De vakinhoudelijke behandeling doen van (een deel van) een zaak.
     * 'belanghebbende' - (Belanghebbende) Vanuit eigen en objectief belang rechtstreeks betrokken zijn bij de behandeling en/of de uitkomst van een zaak.
     * 'beslisser' - (Beslisser) Nemen van besluiten die voor de uitkomst van een zaak noodzakelijk zijn.
     * 'initiator' - (Initiator) Aanleiding geven tot de start van een zaak
     * 'klantcontacter' - (Klantcontacter) Het eerste aanspreekpunt zijn voor vragen van burgers en bedrijven
     * 'zaakcoordinator' - (Zaakcoordinator) Er voor zorg dragen dat de behandeling van de zaak in samenhang uitgevoerd wordt conform de daarover gemaakte afspraken.
     * 'mede_initiator' - 'Mede-initiator'
     */
    private String omschrijvingGeneriek;

    /**
     * Roltoelichting
     * - Required
     */
    private String roltoelichting;

    /**
     * De datum waarop dit object is geregistreerd.
     */
    @JsonbDateFormat(DATE_TIME_FORMAT_WITH_MILLISECONDS)
    private ZonedDateTime registratiedatum;

    private IndicatieMachtiging indicatieMachtiging;

    public Rol() {
    }

    /**
     * Constructor with required attributes for POST and PUT requests
     */
    public Rol(final URI zaak, final Roltype roltype, final BetrokkeneType betrokkeneType, final BETROKKENE_IDENTIFICATIE betrokkeneIdentificatie,
            final String roltoelichting) {
        this.zaak = zaak;
        this.betrokkeneIdentificatie = betrokkeneIdentificatie;
        this.betrokkeneType = betrokkeneType;
        this.roltype = roltype.getUrl();
        this.roltoelichting = roltoelichting;
        this.omschrijving = roltype.getOmschrijving();
        this.omschrijvingGeneriek = roltype.getOmschrijvingGeneriek().toValue();
    }

    public URI getUrl() {
        return url;
    }

    public UUID getUuid() {
        return uuid;
    }

    public URI getZaak() {
        return zaak;
    }

    public URI getBetrokkene() {
        return betrokkene;
    }

    public void setBetrokkene(final URI betrokkene) {
        this.betrokkene = betrokkene;
    }

    public BetrokkeneType getBetrokkeneType() {
        return betrokkeneType;
    }

    public URI getRoltype() {
        return roltype;
    }

    public String getOmschrijving() {
        return omschrijving;
    }

    public String getOmschrijvingGeneriek() {
        return omschrijvingGeneriek;
    }

    public String getRoltoelichting() {
        return roltoelichting;
    }

    public ZonedDateTime getRegistratiedatum() {
        return registratiedatum;
    }

    public IndicatieMachtiging getIndicatieMachtiging() {
        return indicatieMachtiging;
    }

    public void setIndicatieMachtiging(final IndicatieMachtiging indicatieMachtiging) {
        this.indicatieMachtiging = indicatieMachtiging;
    }

    public BETROKKENE_IDENTIFICATIE getBetrokkeneIdentificatie() {
        return betrokkeneIdentificatie;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Rol<BETROKKENE_IDENTIFICATIE> rol = (Rol<BETROKKENE_IDENTIFICATIE>) o;
        return equalBetrokkeneRol(rol) && equalBetrokkeneIdentificatie(rol.getBetrokkeneIdentificatie());
    }

    public boolean equalBetrokkeneRol(final Rol<?> other) {
        return getBetrokkeneType() == other.getBetrokkeneType() &&
                getRoltype().equals(other.getRoltype());
    }

    protected abstract boolean equalBetrokkeneIdentificatie(final BETROKKENE_IDENTIFICATIE identificatie);

    public abstract String getNaam();

    public abstract String getIdentificatienummer();
}
