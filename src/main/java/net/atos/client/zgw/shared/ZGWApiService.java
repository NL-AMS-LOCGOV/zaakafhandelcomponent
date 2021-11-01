/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared;

import java.net.URI;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZonedDateTime;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import net.atos.client.zgw.drc.DRCClient;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobjectData;
import net.atos.client.zgw.drc.model.Gebruiksrechten;
import net.atos.client.zgw.shared.util.DateTimeUtil;
import net.atos.client.zgw.zrc.ZRCClient;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.ZaakInformatieObject;
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
    @RestClient
    private ZRCClient zrcClient;

    @Inject
    @RestClient
    private DRCClient drcClient;

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private ZRCClientService zrcClientService;

    public void setZaakstatus(final UUID zaakUUID, final String status, final String statusToelichting) {
        final Zaak zaak = zrcClient.zaakRead(zaakUUID);
        setZaakstatus(zaak, status, statusToelichting);
    }

    public void setZaakstatus(final Zaak zaak, final String status, final String statusToelichting) {
        final Statustype statustype = ztcClientService.getStatustype(zaak.getZaaktype(), status);
        zrcClientService.setZaakStatus(zaak.getUrl(), statustype.getUrl(), statusToelichting);
    }

    public void setZaakResultaat(final UUID zaakUUID, final String resultaat, final String resultaatToelichting) {
        final Zaak zaak = zrcClient.zaakRead(zaakUUID);
        setZaakResultaat(zaak, resultaat, resultaatToelichting);
    }

    public void setZaakResultaat(final Zaak zaak, final String resultaat, final String resultaatToelichting) {
        final Resultaattype resultaattype = ztcClientService.getResultaattype(zaak.getZaaktype(), resultaat);
        zrcClientService.setZaakResultaat(zaak.getUrl(), resultaattype.getUrl(), resultaatToelichting);
    }

    public Zaak createZaak(final Zaak zaak) {
        final Zaak zaakMetDoorlooptijden = berekenDoorlooptijden(zaak);
        return zrcClient.zaakCreate(zaakMetDoorlooptijden);
    }

    private Zaak berekenDoorlooptijden(final Zaak zaak) {
        final Zaaktype zaaktype = ztcClientService.getZaaktype(zaak.getZaaktype());
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

        return zaak;
    }

    public void updateZaak(final UUID zaakUUID, final String status, final String statusToelichting, final String resultaat,
            final String resultaatToelichting) {
        final Zaak zaak = zrcClient.zaakRead(zaakUUID);
        if (status != null) {
            setZaakstatus(zaak, status, statusToelichting);
        }
        if (resultaat != null) {
            setZaakResultaat(zaak, resultaat, resultaatToelichting);
        }
    }

    public void endZaak(final UUID zaakUUID, final String eindstatusToelichting) {
        final Zaak zaak = zrcClient.zaakRead(zaakUUID);
        endZaak(zaak, eindstatusToelichting);
    }

    public void endZaak(final Zaak zaak, final String eindstatusToelichting, final ZonedDateTime datumEindstatusGezet) {
        final Statustype eindStatustype = ztcClientService.getEindstatustype(zaak.getZaaktype());
        zrcClientService.setZaakStatus(zaak.getUrl(), eindStatustype.getUrl(), eindstatusToelichting, datumEindstatusGezet);
    }

    public void endZaak(final Zaak zaak, final String eindstatusToelichting) {
        final Statustype eindStatustype = ztcClientService.getEindstatustype(zaak.getZaaktype());
        zrcClientService.setZaakStatus(zaak.getUrl(), eindStatustype.getUrl(), eindstatusToelichting);
    }

    public void endZaakWithResultaat(final UUID zaakUUID, final String eindstatusToelichting, final String resultaat, final String resultaatToelichting) {
        final Zaak zaak = zrcClient.zaakRead(zaakUUID);
        endZaakWithResultaat(zaak, eindstatusToelichting, resultaat, resultaatToelichting);
    }

    public void endZaakWithResultaat(final Zaak zaak, final String eindstatusToelichting, final String resultaat, final String resultaatToelichting) {
        setZaakResultaat(zaak, resultaat, resultaatToelichting);
        endZaak(zaak, eindstatusToelichting);
    }

    public void endZaakWithResultaat(final Zaak zaak, final String eindstatusToelichting, final String resultaat, final String resultaatToelichting,
            final ZonedDateTime datumEindstatusGezet) {
        setZaakResultaat(zaak, resultaat, resultaatToelichting);
        endZaak(zaak, eindstatusToelichting, datumEindstatusGezet);
    }

    public ZaakInformatieObject addInformatieObjectToZaak(final URI zaakURI, final EnkelvoudigInformatieobjectData informatieObjectData, final String titel,
            final String beschrijving, final String omschrijvingVoorwaardenGebruiksrechten) {
        final EnkelvoudigInformatieobjectData informatieobject = drcClient.enkelvoudigInformatieobjectCreate(informatieObjectData);
        drcClient.gebruiksrechtenCreate(new Gebruiksrechten(informatieobject.getUrl(), DateTimeUtil.convertToDateTime(informatieobject.getCreatiedatum()),
                                                            omschrijvingVoorwaardenGebruiksrechten));

        final ZaakInformatieObject zaakInformatieObject = new ZaakInformatieObject();
        zaakInformatieObject.setZaak(zaakURI);
        zaakInformatieObject.setInformatieobject(informatieobject.getUrl());
        zaakInformatieObject.setTitel(titel);
        zaakInformatieObject.setBeschrijving(beschrijving);
        return zrcClient.zaakinformatieobjectCreate(zaakInformatieObject);
    }

    public void removeZaakInformatieObjectCascaded(final ZaakInformatieObject zaakInformatieObject) {
        // ToDo: ESUITEDEV-22935 Delete geeft een server 500 response
        // zrcClient.zaakinformatieobjectDelete(zaakInformatieObject.getUuid());
        // drcClient.enkelvoudiginformatieobjectDelete(URIUtil.parseUUIDFromResourceURI(zaakInformatieObject.getInformatieobject()));
    }

}
