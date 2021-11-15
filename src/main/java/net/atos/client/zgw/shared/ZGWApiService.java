/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared;

import static net.atos.client.zgw.shared.util.DateTimeUtil.convertToDateTime;

import java.net.URI;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZonedDateTime;
import java.util.Optional;

import javax.cache.annotation.CacheRemove;
import javax.cache.annotation.CacheRemoveAll;
import javax.cache.annotation.CacheResult;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import net.atos.client.zgw.drc.DRCClient;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobjectWithInhoud;
import net.atos.client.zgw.drc.model.Gebruiksrechten;
import net.atos.client.zgw.shared.cache.Caching;
import net.atos.client.zgw.zrc.ZRCClient;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.BetrokkeneType;
import net.atos.client.zgw.zrc.model.Resultaat;
import net.atos.client.zgw.zrc.model.Rol;
import net.atos.client.zgw.zrc.model.RolListParameters;
import net.atos.client.zgw.zrc.model.RolMedewerker;
import net.atos.client.zgw.zrc.model.RolOrganisatorischeEenheid;
import net.atos.client.zgw.zrc.model.Status;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.ZaakInformatieobject;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.AardVanRol;
import net.atos.client.zgw.ztc.model.Resultaattype;
import net.atos.client.zgw.ztc.model.Roltype;
import net.atos.client.zgw.ztc.model.Statustype;
import net.atos.client.zgw.ztc.model.Zaaktype;

/**
 * Let op!
 * <p>
 * Methods met caching NOOIT van binnen de service aanroepen (anders werkt de caching niet).
 * En bij managed caches geen sleutels anders dan URI en UID introduceren.
 * Bij caches waarbij het resultaat null kan zijn Optional gebruiken want null wordt niet gecachet.
 */
@ApplicationScoped
public class ZGWApiService implements Caching {

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    @RestClient
    private ZRCClient zrcClient;

    @Inject
    @RestClient
    private DRCClient drcClient;

    /**
     * Create {@link Zaak} and calculate Doorlooptijden.
     *
     * @param zaak {@link Zaak}
     * @return Created {@link Zaak}
     */
    public Zaak createZaak(final Zaak zaak) {
        calculateDoorlooptijden(zaak);
        return zrcClient.zaakCreate(zaak);
    }

    /**
     * Create {@link Status} for a given {@link Zaak} based on {@link Statustype}.omschrijving and with {@link Status}.toelichting.
     *
     * @param zaak                   {@link Zaak}
     * @param statustypeOmschrijving Omschrijving of the {@link Statustype} of the required {@link Status}.
     * @param statusToelichting      Toelichting for thew {@link Status}.
     * @return Created {@link Status}.
     */
    public Status createStatusForZaak(final Zaak zaak, final String statustypeOmschrijving, final String statusToelichting) {
        final Statustype statustype = ztcClientService.getStatustype(
                ztcClientService.readStatustypen(zaak.getZaaktype()), statustypeOmschrijving, zaak.getZaaktype());
        return createStatusForZaak(zaak.getUrl(), statustype.getUrl(), statusToelichting);
    }

    /**
     * Create {@link Resultaat} for a given {@link Zaak} based on {@link Resultaattype}.omschrijving and with {@link Resultaat}.toelichting.
     *
     * @param zaak                      {@link Zaak}
     * @param resultaattypeOmschrijving Omschrijving of the {@link Resultaattype} of the required {@link Resultaat}.
     * @param resultaatToelichting      Toelichting for thew {@link Resultaat}.
     * @return Created {@link Resultaat}.
     */
    public Resultaat createResultaatForZaak(final Zaak zaak, final String resultaattypeOmschrijving, final String resultaatToelichting) {
        final Resultaattype resultaattype = ztcClientService.getResultaattype(
                ztcClientService.readResultaattypen(zaak.getZaaktype()), resultaattypeOmschrijving, zaak.getZaaktype());
        final Resultaat resultaat = new Resultaat(zaak.getUrl(), resultaattype.getUrl());
        resultaat.setToelichting(resultaatToelichting);
        return zrcClient.resultaatCreate(resultaat);
    }

    /**
     * End {@link Zaak}. Creating a new Eind {@link Status} for the {@link Zaak}.
     *
     * @param zaak                  {@link Zaak}
     * @param eindstatusToelichting Toelichting for thew Eind {@link Status}.
     * @return Created Eind {@link Status}.
     */
    public Status endZaak(final Zaak zaak, final String eindstatusToelichting) {
        final Statustype eindStatustype = ztcClientService.getStatustypeEind(
                ztcClientService.readStatustypen(zaak.getZaaktype()), zaak.getZaaktype());
        return createStatusForZaak(zaak.getUrl(), eindStatustype.getUrl(), eindstatusToelichting);
    }

    /**
     * Create {@link EnkelvoudigInformatieobjectWithInhoud} and {@link ZaakInformatieobject} for {@link Zaak}.
     *
     * @param zaak                                   {@link Zaak}.
     * @param informatieobject                       {@link EnkelvoudigInformatieobjectWithInhoud} to be created.
     * @param titel                                  Titel of the new {@link ZaakInformatieobject}.
     * @param beschrijving                           Beschrijving of the new {@link ZaakInformatieobject}.
     * @param omschrijvingVoorwaardenGebruiksrechten Used to create the {@link Gebruiksrechten} for the to be created {@link EnkelvoudigInformatieobjectWithInhoud}
     * @return Created {@link ZaakInformatieobject}.
     */
    public ZaakInformatieobject createZaakInformatieobjectForZaak(final Zaak zaak, final EnkelvoudigInformatieobjectWithInhoud informatieobject,
            final String titel, final String beschrijving, final String omschrijvingVoorwaardenGebruiksrechten) {
        final EnkelvoudigInformatieobjectWithInhoud newInformatieobject = drcClient.enkelvoudigInformatieobjectCreate(informatieobject);
        drcClient.gebruiksrechtenCreate(new Gebruiksrechten(newInformatieobject.getUrl(), convertToDateTime(newInformatieobject.getCreatiedatum()),
                                                            omschrijvingVoorwaardenGebruiksrechten));

        final ZaakInformatieobject zaakInformatieObject = new ZaakInformatieobject();
        zaakInformatieObject.setZaak(zaak.getUrl());
        zaakInformatieObject.setInformatieobject(newInformatieobject.getUrl());
        zaakInformatieObject.setTitel(titel);
        zaakInformatieObject.setBeschrijving(beschrijving);
        return zrcClient.zaakinformatieobjectCreate(zaakInformatieObject);
    }

    /**
     * Find {@link RolOrganisatorischeEenheid} for {@link Zaak} with behandelaar {@link AardVanRol}.
     *
     * @param zaakUrl {@link URI}
     * @return {@link RolOrganisatorischeEenheid} or 'null'.
     */
    @CacheResult(cacheName = ZGW_ZAAK_GROEP_MANAGED)
    public Optional<RolOrganisatorischeEenheid> findGroepForZaak(final URI zaakUrl) {
        return Optional.ofNullable((RolOrganisatorischeEenheid) findRolForZaak(zaakUrl, BetrokkeneType.ORGANISATORISCHE_EENHEID, AardVanRol.BEHANDELAAR));
    }

    /**
     * Find {@link RolMedewerker} for {@link Zaak} with behandelaar {@link AardVanRol}.
     *
     * @param zaakUrl {@link URI}
     * @return {@link RolMedewerker} or 'null'.
     */
    @CacheResult(cacheName = ZGW_ZAAK_BEHANDELAAR_MANAGED)
    public Optional<RolMedewerker> findBehandelaarForZaak(final URI zaakUrl) {
        return Optional.ofNullable((RolMedewerker) findRolForZaak(zaakUrl, BetrokkeneType.MEDEWERKER, AardVanRol.BEHANDELAAR));
    }

    private Rol<?> findRolForZaak(final URI zaakUrl, final BetrokkeneType betrokkeneType, final AardVanRol aardVanRol) {
        final URI zaakType = zrcClientService.readZaak(zaakUrl).getZaaktype();
        final Roltype roltype = ztcClientService.readRoltype(zaakType, aardVanRol);
        final RolListParameters rolListParameters = new RolListParameters();
        rolListParameters.setZaak(zaakUrl);
        rolListParameters.setBetrokkeneType(betrokkeneType);
        rolListParameters.setRoltype(roltype.getUrl());
        return zrcClientService.listRollen(rolListParameters)
                .getSingleResult()
                .orElse(null);
    }

    private Status createStatusForZaak(final URI zaakURI, final URI statustypeURI, final String toelichting) {
        // Wanneer de huidige datum en tijd gebruikt wordt geeft open zaak een validatie fout dat de datum in de toekomst ligt.
        final Status status = new Status(zaakURI, statustypeURI, ZonedDateTime.now().minusDays(1));
        status.setStatustoelichting(toelichting);
        return zrcClient.statusCreate(status);
    }

    private void calculateDoorlooptijden(final Zaak zaak) {
        final Zaaktype zaaktype = ztcClientService.readZaaktype(zaak.getZaaktype());
        final Period streefDatum = zaaktype.getServicenorm();
        final Period fataleDatum = zaaktype.getDoorlooptijd();

        if (streefDatum != null) {
            final LocalDate eindDatumGepland = zaak.getStartdatum().plus(streefDatum);
            zaak.setEinddatumGepland(eindDatumGepland);
        }
        if (fataleDatum != null) {
            final LocalDate uiterlijkeEindDatumAfdoening = zaak.getStartdatum().plus(fataleDatum);
            zaak.setUiterlijkeEinddatumAfdoening(uiterlijkeEindDatumAfdoening);
        }
    }

    @CacheRemoveAll(cacheName = ZGW_ZAAK_GROEP_MANAGED)
    public void clearZaakGroepManagedCache() {
        cleared(ZGW_ZAAK_GROEP_MANAGED);
    }

    @CacheRemove(cacheName = ZGW_ZAAK_GROEP_MANAGED)
    public void updateZaakgroepCache(final URI key) {
        removed(ZGW_ZAAK_GROEP_MANAGED, key);
    }

    @CacheRemoveAll(cacheName = ZGW_ZAAK_BEHANDELAAR_MANAGED)
    public void clearZaakBehandelaarManagedCache() {
        cleared(ZGW_ZAAK_BEHANDELAAR_MANAGED);
    }

    @CacheRemove(cacheName = ZGW_ZAAK_BEHANDELAAR_MANAGED)
    public void updateZaakbehandelaarCache(final URI key) {
        removed(ZGW_ZAAK_BEHANDELAAR_MANAGED, key);
    }
}
