/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared;

import static net.atos.client.zgw.shared.util.DateTimeUtil.convertToDateTime;
import static net.atos.zac.websocket.event.ScreenEventType.ZAAK_INFORMATIEOBJECTEN;

import java.net.URI;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import net.atos.client.zgw.drc.DRCClientService;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobjectWithInhoud;
import net.atos.client.zgw.drc.model.Gebruiksrechten;
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
import net.atos.client.zgw.ztc.model.BrondatumArchiefprocedure;
import net.atos.client.zgw.ztc.model.Resultaattype;
import net.atos.client.zgw.ztc.model.Roltype;
import net.atos.client.zgw.ztc.model.Statustype;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.authentication.LoggedInUser;
import net.atos.zac.event.EventingService;
import net.atos.zac.signalering.event.SignaleringEventUtil;
import net.atos.zac.signalering.model.SignaleringType;

/**
 * Careful!
 * <p>
 * Never call methods with caching annotations from within the service (or it will not work).
 * Do not introduce caches with keys other than URI and UUID.
 * Use Optional for caches that need to hold nulls (Infinispan does not cache nulls).
 */
@ApplicationScoped
public class ZGWApiService {

    private static final Logger LOG = Logger.getLogger(ZGWApiService.class.getName());

    // Page numbering in ZGW Api's starts with 1
    public static final int FIRST_PAGE_NUMBER_ZGW_APIS = 1;

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private DRCClientService drcClientService;

    @Inject
    private EventingService eventingService;

    @Inject
    private Instance<LoggedInUser> loggedInUserInstance;

    /**
     * Create {@link Zaak} and calculate Doorlooptijden.
     *
     * @param zaak {@link Zaak}
     * @return Created {@link Zaak}
     */
    public Zaak createZaak(final Zaak zaak) {
        calculateDoorlooptijden(zaak);
        return zrcClientService.createZaak(zaak);
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
        final Statustype statustype = readStatustype(ztcClientService.readStatustypen(zaak.getZaaktype()), statustypeOmschrijving, zaak.getZaaktype());
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
        final List<Resultaattype> resultaattypen = ztcClientService.readResultaattypen(zaak.getZaaktype());
        final Resultaattype resultaattype = filterResultaattype(resultaattypen, resultaattypeOmschrijving, zaak.getZaaktype());
        return createResultaat(zaak.getUrl(), resultaattype.getUrl(), resultaatToelichting);
    }

    /**
     * Create {@link Resultaat} for a given {@link Zaak} based on {@link Resultaattype}.UUID and with {@link Resultaat}.toelichting.
     *
     * @param zaak                 {@link Zaak}
     * @param resultaattypeUUID    UUID of the {@link Resultaattype} of the required {@link Resultaat}.
     * @param resultaatToelichting Toelichting for thew {@link Resultaat}.
     * @return Created {@link Resultaat}.
     */
    public Resultaat createResultaatForZaak(final Zaak zaak, final UUID resultaattypeUUID, final String resultaatToelichting) {
        final Resultaattype resultaattype = ztcClientService.readResultaattype(resultaattypeUUID);
        return createResultaat(zaak.getUrl(), resultaattype.getUrl(), resultaatToelichting);
    }

    /**
     * Update {@link Resultaat} for a given {@link Zaak} based on {@link Resultaattype}.UUID and with {@link Resultaat}
     * .toelichting.
     *
     * @param zaak              {@link Zaak}
     * @param resultaatTypeUuid Containing the UUID of the {@link Resultaattype} of the required {@link Resultaat}.
     * @param reden             Reason of setting the {@link Resultaattype}
     * @return Created {@link Resultaat}.
     */
    public Resultaat updateResultaatForZaak(final Zaak zaak, final UUID resultaatTypeUuid, final String reden) {
        final Resultaat resultaat = zrcClientService.readResultaat(zaak.getResultaat());
        zrcClientService.deleteResultaat(resultaat.getUuid());
        return createResultaatForZaak(zaak, resultaatTypeUuid, reden);
    }

    /**
     * End {@link Zaak}.
     * Creating a new Eind {@link Status} for the {@link Zaak}.
     * And calculating the archiverings parameters
     *
     * @param zaak                  {@link Zaak}
     * @param eindstatusToelichting Toelichting for thew Eind {@link Status}.
     */
    public void endZaak(final Zaak zaak, final String eindstatusToelichting) {
        closeZaak(zaak, eindstatusToelichting);
        berekenArchiveringsparameters(zaak.getUuid());
    }

    /**
     * End {@link Zaak}.
     * Creating a new Eind {@link Status} for the {@link Zaak}.
     * And calculating the archiverings parameters
     *
     * @param zaakUUID              UUID of the {@link Zaak}
     * @param eindstatusToelichting Toelichting for thew Eind {@link Status}.
     */
    public void endZaak(final UUID zaakUUID, final String eindstatusToelichting) {
        final Zaak zaak = zrcClientService.readZaak(zaakUUID);
        endZaak(zaak, eindstatusToelichting);
    }

    /**
     * Close {@link Zaak}.
     * Creating a new Eind {@link Status} for the {@link Zaak}.
     *
     * @param zaak                  {@link Zaak} to be closed
     * @param eindstatusToelichting Toelichting for thew Eind {@link Status}.
     */
    public void closeZaak(final Zaak zaak, final String eindstatusToelichting) {
        final Statustype eindStatustype = readStatustypeEind(ztcClientService.readStatustypen(zaak.getZaaktype()), zaak.getZaaktype());
        createStatusForZaak(zaak.getUrl(), eindStatustype.getUrl(), eindstatusToelichting);
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
        final EnkelvoudigInformatieobjectWithInhoud newInformatieobject = drcClientService.createEnkelvoudigInformatieobject(informatieobject);
        drcClientService.createGebruiksrechten(new Gebruiksrechten(newInformatieobject.getUrl(), convertToDateTime(newInformatieobject.getCreatiedatum()),
                                                                   omschrijvingVoorwaardenGebruiksrechten));
        final ZaakInformatieobject zaakInformatieObject = new ZaakInformatieobject();
        zaakInformatieObject.setZaak(zaak.getUrl());
        zaakInformatieObject.setInformatieobject(newInformatieobject.getUrl());
        zaakInformatieObject.setTitel(titel);
        zaakInformatieObject.setBeschrijving(beschrijving);
        final ZaakInformatieobject created = zrcClientService.createZaakInformatieobject(zaakInformatieObject, StringUtils.EMPTY);
        eventingService.send(ZAAK_INFORMATIEOBJECTEN.updated(zaak));
        eventingService.send(SignaleringEventUtil.event(SignaleringType.Type.ZAAK_DOCUMENT_TOEGEVOEGD, zaak, loggedInUserInstance.get()));
        return created;
    }

    /**
     * Delete {@link ZaakInformatieobject} which relates {@link EnkelvoudigInformatieobject} and {@link Zaak} with zaakUUID.
     * When the {@link EnkelvoudigInformatieobject} has no other related {@link ZaakInformatieobject}s then it is also deleted.
     *
     * @param enkelvoudigInformatieobject {@link EnkelvoudigInformatieobject}
     * @param zaakUUID                    UUID of a {@link Zaak}
     * @param toelichting                 Explanation why the {@link EnkelvoudigInformatieobject} is to be removed.
     */
    public void removeEnkelvoudigInformatieObjectFromZaak(final EnkelvoudigInformatieobject enkelvoudigInformatieobject, final UUID zaakUUID,
            final String toelichting) {
        final List<ZaakInformatieobject> zaakInformatieobjecten = zrcClientService.listZaakinformatieobjecten(enkelvoudigInformatieobject);
        // Delete the relationship of the EnkelvoudigInformatieobject with the zaak.
        zaakInformatieobjecten.stream()
                .filter(zaakInformatieobject -> zaakInformatieobject.getZaakUUID().equals(zaakUUID))
                .forEach(zaakInformatieobject -> zrcClientService.deleteZaakInformatieobject(zaakInformatieobject.getUuid(), toelichting, "Verwijderd"));

        // If the EnkelvoudigInformatieobject has no relationship(s) with other zaken it can be deleted.
        if (!zaakInformatieobjecten.stream().anyMatch(zaakInformatieobject -> !zaakInformatieobject.getZaakUUID().equals(zaakUUID))) {
            drcClientService.deleteEnkelvoudigInformatieobject(enkelvoudigInformatieobject.getUUID());
        }
    }

    /**
     * Find {@link RolOrganisatorischeEenheid} for {@link Zaak} with behandelaar {@link AardVanRol}.
     *
     * @param zaak {@link Zaak}
     * @return {@link RolOrganisatorischeEenheid} or 'null'.
     */
    public RolOrganisatorischeEenheid findGroepForZaak(final Zaak zaak) {
        return (RolOrganisatorischeEenheid) findRolForZaak(zaak, AardVanRol.BEHANDELAAR, BetrokkeneType.ORGANISATORISCHE_EENHEID);
    }

    /**
     * Find {@link RolMedewerker} for {@link Zaak} with behandelaar {@link AardVanRol}.
     *
     * @param zaak {@link Zaak}
     * @return {@link RolMedewerker} or 'null'.
     */
    public RolMedewerker findBehandelaarForZaak(final Zaak zaak) {
        return (RolMedewerker) findRolForZaak(zaak, AardVanRol.BEHANDELAAR, BetrokkeneType.MEDEWERKER);
    }

    public Rol<?> findInitiatorForZaak(final Zaak zaak) {
        return findRolForZaak(zaak, AardVanRol.INITIATOR);
    }

    public Rol<?> findRolForZaak(final Zaak zaak, final AardVanRol aardVanRol) {
        final Roltype roltype = ztcClientService.findRoltype(zaak.getZaaktype(), aardVanRol);
        if (roltype != null) {
            return zrcClientService.listRollen(new RolListParameters(zaak.getUrl(), roltype.getUrl())).getSingleResult().orElse(null);
        } else {
            return null;
        }
    }

    public Rol<?> findRolForZaak(final Zaak zaak, final AardVanRol aardVanRol, final BetrokkeneType betrokkeneType) {
        final Roltype roltype = ztcClientService.findRoltype(zaak.getZaaktype(), aardVanRol);
        if (roltype != null) {
            return zrcClientService.listRollen(new RolListParameters(zaak.getUrl(), roltype.getUrl(), betrokkeneType)).getSingleResult().orElse(null);
        } else {
            return null;
        }
    }

    private Status createStatusForZaak(final URI zaakURI, final URI statustypeURI, final String toelichting) {
        final Status status = new Status(zaakURI, statustypeURI, ZonedDateTime.now());
        status.setStatustoelichting(toelichting);
        return zrcClientService.createStatus(status);
    }

    private void calculateDoorlooptijden(final Zaak zaak) {
        final Zaaktype zaaktype = ztcClientService.readZaaktype(zaak.getZaaktype());

        if (zaaktype.getServicenorm() != null) {
            zaak.setEinddatumGepland(zaak.getStartdatum().plus(zaaktype.getServicenorm()));
        }

        zaak.setUiterlijkeEinddatumAfdoening(zaak.getStartdatum().plus(zaaktype.getDoorlooptijd()));
    }

    private Resultaat createResultaat(final URI zaakURI, final URI resultaattypeURI, final String resultaatToelichting) {
        final Resultaat resultaat = new Resultaat(zaakURI, resultaattypeURI);
        resultaat.setToelichting(resultaatToelichting);
        return zrcClientService.createResultaat(resultaat);
    }

    private Resultaattype filterResultaattype(List<Resultaattype> resultaattypes, final String omschrijving, final URI zaaktypeURI) {
        return resultaattypes.stream()
                .filter(resultaattype -> StringUtils.equals(resultaattype.getOmschrijving(), omschrijving))
                .findAny()
                .orElseThrow(
                        () -> new RuntimeException(
                                String.format("Zaaktype '%s': Resultaattype with omschrijving '%s' not found", zaaktypeURI, omschrijving)));
    }

    private void berekenArchiveringsparameters(final UUID zaakUUID) {
        final Zaak zaak = zrcClientService.readZaak(zaakUUID); // refetch to get the einddatum (the archiefnominatie has also been set)
        final Resultaattype resultaattype = ztcClientService.readResultaattype(zrcClientService.readResultaat(zaak.getResultaat()).getResultaattype());
        if (resultaattype.getArchiefactietermijn() != null) { // no idea what it means when there is no archiefactietermijn
            final LocalDate brondatum = bepaalBrondatum(zaak, resultaattype);
            if (brondatum != null) {
                final Zaak zaakPatch = new Zaak();
                zaakPatch.setArchiefactiedatum(brondatum.plus(resultaattype.getArchiefactietermijn()));
                zrcClientService.updateZaak(zaakUUID, zaakPatch);
            }
        }
    }

    private LocalDate bepaalBrondatum(final Zaak zaak, final Resultaattype resultaattype) {
        final BrondatumArchiefprocedure brondatumArchiefprocedure = resultaattype.getBrondatumArchiefprocedure();
        if (brondatumArchiefprocedure != null) {
            switch (brondatumArchiefprocedure.getAfleidingswijze()) {
                case AFGEHANDELD:
                    return zaak.getEinddatum();
                default:
                    LOG.warning(String.format("De brondatum bepaling voor afleidingswijze %s is nog niet geimplementeerd",
                                              brondatumArchiefprocedure.getAfleidingswijze()));
            }
        }
        return null;
    }

    private Statustype readStatustype(final List<Statustype> statustypes, final String omschrijving, final URI zaaktypeURI) {
        return statustypes.stream()
                .filter(statustype -> omschrijving.equals(statustype.getOmschrijving()))
                .findAny()
                .orElseThrow(() -> new RuntimeException(
                        String.format("Zaaktype '%s': Statustype with omschrijving '%s' not found", zaaktypeURI, omschrijving)));
    }

    private Statustype readStatustypeEind(final List<Statustype> statustypes, final URI zaaktypeURI) {
        return statustypes.stream()
                .filter(Statustype::getEindstatus)
                .findAny()
                .orElseThrow(() -> new RuntimeException(String.format("Zaaktype '%s': No eind Status found for Zaaktype.", zaaktypeURI)));
    }
}
