package net.atos.zac.zoeken.converter;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;

import org.flowable.idm.api.Group;
import org.flowable.idm.api.User;

import net.atos.client.vrl.VRLClientService;
import net.atos.client.vrl.model.CommunicatieKanaal;
import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Geometry;
import net.atos.client.zgw.zrc.model.GeometryType;
import net.atos.client.zgw.zrc.model.Resultaat;
import net.atos.client.zgw.zrc.model.RolOrganisatorischeEenheid;
import net.atos.client.zgw.zrc.model.Status;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Resultaattype;
import net.atos.client.zgw.ztc.model.Statustype;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.app.zaken.converter.RESTCommunicatiekanaalConverter;
import net.atos.zac.flowable.FlowableService;
import net.atos.zac.util.UriUtil;
import net.atos.zac.zoeken.model.ZaakZoekItem;

public class ZaakZoekItemConverter {

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private VRLClientService vrlClientService;

    @Inject
    private ZGWApiService zgwApiService;

    @Inject
    private FlowableService flowableService;


    public ZaakZoekItem convert(final UUID zaakUUID){
        final Zaak zaak = zrcClientService.readZaak(zaakUUID);
        return convert(zaak);
    }

    private ZaakZoekItem convert(final Zaak zaak){
        final ZaakZoekItem zoekItem = new ZaakZoekItem();
        zoekItem.setUuid(zaak.getUuid().toString());
        zoekItem.setType("ZAAK");
        zoekItem.setIdentificatie(zaak.getIdentificatie());
        zoekItem.setOmschrijving(zaak.getOmschrijving());
        zoekItem.setToelichting(zaak.getToelichting());
        zoekItem.setRegistratiedatum(toSolrDate(zaak.getRegistratiedatum()));
        zoekItem.setStartdatum(toSolrDate(zaak.getStartdatum()));
        zoekItem.setStreefdatum(toSolrDate(zaak.getEinddatumGepland()));
        zoekItem.setEinddatum(toSolrDate(zaak.getEinddatum()));
        zoekItem.setFataledatum(toSolrDate(zaak.getUiterlijkeEinddatumAfdoening()));
        zoekItem.setPublicatiedatum(toSolrDate(zaak.getPublicatiedatum()));
        zoekItem.setVertrouwelijkheidaanduiding(zaak.getVertrouwelijkheidaanduiding().toValue());
        zoekItem.setAfgehandeld(zaak.getEinddatum() != null);
        zoekItem.setInitiatorIdentificatie(zgwApiService.findInitiatorForZaak(zaak.getUrl()));
        zoekItem.setLocatie(convertToLocatie(zaak.getZaakgeometrie()));

        final CommunicatieKanaal kanaal = getCommunicatieKanaal(zaak);
        if(kanaal != null){
            zoekItem.setCommunicatiekanaal(kanaal.getNaam());
        }

        final Group groep = getGroep(zaak);
        if(groep != null){
            zoekItem.setGroepID(groep.getId());
            zoekItem.setGroepNaam(groep.getName());
        }

        final User behandelaar = getBehandelaar(zaak);
        if(behandelaar != null){
            zoekItem.setBehandelaarNaam(getVolledigeNaam(behandelaar));
            zoekItem.setBehandelaarGebruikersnaam(behandelaar.getId());
        }

        if(zaak.getVerlenging() != null){
            zoekItem.setIndicatieVerlenging(true);
            zoekItem.setDuurVerlenging(String.valueOf(zaak.getVerlenging().getDuur()));
            zoekItem.setRedenVerlenging(zaak.getVerlenging().getReden());
        }

        if(zaak.getOpschorting() != null){
            zoekItem.setRedenOpschorting(zaak.getOpschorting().getReden());
        }

        final Zaaktype zaaktype = ztcClientService.readZaaktype(zaak.getZaaktype());
        zoekItem.setZaaktypeIdentificatie(zaaktype.getIdentificatie());
        zoekItem.setZaaktypeOmschrijving(zaaktype.getOmschrijving());
        zoekItem.setZaaktypeUuid(UriUtil.uuidFromURI(zaaktype.getUrl()).toString());

        final Status status = zrcClientService.readStatus(zaak.getStatus());
        final Statustype statustype = ztcClientService.readStatustype(status.getStatustype());
        zoekItem.setStatusNaam(statustype.getOmschrijving());
        zoekItem.setStatusEindstatus(statustype.getEindstatus());
        zoekItem.setStatusToelichting(status.getStatustoelichting());
        zoekItem.setStatusToekenningsdatum(toSolrDate(status.getDatumStatusGezet()));

        if(zaak.getResultaat() != null) {
            final Resultaat resultaat = zrcClientService.readResultaat(zaak.getResultaat());
            if (resultaat != null) {
                Resultaattype resultaattype = ztcClientService.readResultaattype(resultaat.getResultaattype());
                zoekItem.setResultaatNaam(resultaattype.getOmschrijving());
                zoekItem.setResultaatToelichting(resultaat.getToelichting());
            }
        }

        return zoekItem;
    }


    private String convertToLocatie(final Geometry zaakgeometrie) {
        //todo
        return null;
    }

    private User getBehandelaar(final Zaak zaak) {
        return zgwApiService.findBehandelaarForZaak(zaak.getUrl())
                .map(betrokkene -> betrokkene.getBetrokkeneIdentificatie().getIdentificatie())
                .map(id -> flowableService.readUser(id))
                .orElse(null);
    }

    private String getVolledigeNaam(final User user) {
        if(user == null){
            return null;
        }
        if (user.getDisplayName() != null) {
            return user.getDisplayName();
        } else if (user.getFirstName() != null && user.getLastName() != null) {
            return String.format("%s %s", user.getFirstName(), user.getLastName());
        } else {
            return user.getId();
        }

    }

    private Group getGroep(final Zaak zaak) {
        return zgwApiService.findGroepForZaak(zaak.getUrl())
                .map(betrokkene -> betrokkene.getBetrokkeneIdentificatie().getIdentificatie())
                .map(id -> flowableService.readGroup(id))
                .orElse(null);
    }

    private CommunicatieKanaal getCommunicatieKanaal(final Zaak zaak){
        if (zaak.getCommunicatiekanaal() != null) {
            final UUID communicatiekanaalUUID = UriUtil.uuidFromURI(zaak.getCommunicatiekanaal());
            return vrlClientService.findCommunicatiekanaal(communicatiekanaalUUID);
        }
        return null;
    }

    private String toSolrDate(final LocalDate localDate){
        if(localDate == null){
            return null;
        }
        return localDate.format(DateTimeFormatter.ISO_DATE);
    }

    private String toSolrDate(final ZonedDateTime zonedDateTime){
        if(zonedDateTime == null){
            return null;
        }
        return zonedDateTime.format(DateTimeFormatter.ISO_INSTANT);
    }

}
