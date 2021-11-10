/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.notities.model;

import static net.atos.zac.util.FlywayIntegrator.SCHEMA;

import java.time.ZonedDateTime;
import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Een Notitie kan worden gekoppeld aan een zaak.
 */
@Entity
@Table(schema = SCHEMA, name = "notitie")
@SequenceGenerator(schema = SCHEMA, name = "notitie_sq", sequenceName = "notitie_sq", allocationSize = 1)
public class Notitie {

    /**
     * Naam van property: {@link Notitie#zaakUUID}
     */
    public static final String ZAAK_UUID = "zaakUUID";

    @Id
    @GeneratedValue(generator = "notitie_sq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Basic
    @Column(name = "zaak_uuid", updatable = false)
    private UUID zaakUUID;

    @NotBlank
    @Column(nullable = false)
    private String tekst;

    @NotNull
    @Column(name = "tijdstip_laatste_wijziging", nullable = false)
    private ZonedDateTime tijdstipLaatsteWijziging;

    @NotBlank
    @Column(name = "gebruikersnaam_medewerker", nullable = false, updatable = false)
    private String gebruikersnaamMedewerker;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public UUID getZaakUUID() {
        return zaakUUID;
    }

    public void setZaakUUID(final UUID zaakUUID) {
        this.zaakUUID = zaakUUID;
    }

    public String getTekst() {
        return tekst;
    }

    public void setTekst(final String text) {
        this.tekst = text;
    }

    public String getGebruikersnaamMedewerker() {
        return gebruikersnaamMedewerker;
    }

    public void setGebruikersnaamMedewerker(final String gebruikersnaamMedewerker) {
        this.gebruikersnaamMedewerker = gebruikersnaamMedewerker;
    }

    public void setTijdstipLaatsteWijziging(final ZonedDateTime tijdstipLaatsteWijziging) {
        this.tijdstipLaatsteWijziging = tijdstipLaatsteWijziging;
    }

    public ZonedDateTime getTijdstipLaatsteWijziging() {
        return tijdstipLaatsteWijziging;
    }

}
