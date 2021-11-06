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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import net.atos.client.zgw.drc.DRCClient;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobjectWithInhoud;
import net.atos.client.zgw.drc.model.Gebruiksrechten;
import net.atos.client.zgw.zrc.ZRCClient;
import net.atos.client.zgw.zrc.model.Resultaat;
import net.atos.client.zgw.zrc.model.Status;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.ZaakInformatieobject;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Resultaattype;
import net.atos.client.zgw.ztc.model.Statustype;
import net.atos.client.zgw.ztc.model.Zaaktype;

/**
 *
 */
@ApplicationScoped
public class ZGWApiService {

    @Inject
    private ZTCClientService ztcClientService;

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
        final Statustype statustype = ztcClientService.readStatustype(zaak.getZaaktype(), statustypeOmschrijving);
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
        final Resultaattype resultaattype = ztcClientService.readResultaattype(zaak.getZaaktype(), resultaattypeOmschrijving);
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
        final Statustype eindStatustype = ztcClientService.readStatustypeEind(zaak.getZaaktype());
        return createStatusForZaak(zaak.getUrl(), eindStatustype.getUrl(), eindstatusToelichting);
    }

    /**
     * Create {@link EnkelvoudigInformatieobjectWithInhoud} and {@link ZaakInformatieobject} for {@link Zaak}.
     *
     * @param zaak                                {@link Zaak}.
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
}
