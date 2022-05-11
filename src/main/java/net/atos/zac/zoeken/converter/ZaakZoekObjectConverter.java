package net.atos.zac.zoeken.converter;

import java.util.UUID;

import javax.inject.Inject;

import net.atos.client.vrl.VRLClientService;
import net.atos.client.vrl.model.CommunicatieKanaal;
import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Geometry;
import net.atos.client.zgw.zrc.model.Resultaat;
import net.atos.client.zgw.zrc.model.Status;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Resultaattype;
import net.atos.client.zgw.ztc.model.Statustype;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.identity.IdentityService;
import net.atos.zac.identity.model.Group;
import net.atos.zac.identity.model.User;
import net.atos.zac.util.DateTimeConverterUtil;
import net.atos.zac.util.UriUtil;
import net.atos.zac.zoeken.model.ZaakZoekObject;

public class ZaakZoekObjectConverter {

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private VRLClientService vrlClientService;

    @Inject
    private ZGWApiService zgwApiService;

    @Inject
    private IdentityService identityService;


    public ZaakZoekObject convert(final UUID zaakUUID) {
        final Zaak zaak = zrcClientService.readZaak(zaakUUID);
        return convert(zaak);
    }

    private ZaakZoekObject convert(final Zaak zaak) {
        final ZaakZoekObject zaakZoekObject = new ZaakZoekObject();
        zaakZoekObject.setUuid(zaak.getUuid().toString());
        zaakZoekObject.setType("ZAAK");
        zaakZoekObject.setIdentificatie(zaak.getIdentificatie());
        zaakZoekObject.setOmschrijving(zaak.getOmschrijving());
        zaakZoekObject.setToelichting(zaak.getToelichting());
        zaakZoekObject.setRegistratiedatum(DateTimeConverterUtil.convertToDate(zaak.getRegistratiedatum()));
        zaakZoekObject.setStartdatum(DateTimeConverterUtil.convertToDate(zaak.getStartdatum()));
        zaakZoekObject.setEinddatumGepland(DateTimeConverterUtil.convertToDate(zaak.getEinddatumGepland()));
        zaakZoekObject.setEinddatum(DateTimeConverterUtil.convertToDate(zaak.getEinddatum()));
        zaakZoekObject.setUiterlijkeEinddatumAfdoening(DateTimeConverterUtil.convertToDate(zaak.getUiterlijkeEinddatumAfdoening()));
        zaakZoekObject.setPublicatiedatum(DateTimeConverterUtil.convertToDate(zaak.getPublicatiedatum()));
        zaakZoekObject.setVertrouwelijkheidaanduiding(zaak.getVertrouwelijkheidaanduiding().toValue());
        zaakZoekObject.setAfgehandeld(zaak.getEinddatum() != null);
        zaakZoekObject.setInitiatorIdentificatie(zgwApiService.findInitiatorForZaak(zaak.getUrl()));
        zaakZoekObject.setLocatie(convertToLocatie(zaak.getZaakgeometrie()));

        final CommunicatieKanaal kanaal = findCommunicatieKanaal(zaak);
        if (kanaal != null) {
            zaakZoekObject.setCommunicatiekanaal(kanaal.getNaam());
        }

        final Group groep = findGroep(zaak);
        if (groep != null) {
            zaakZoekObject.setGroepID(groep.getId());
            zaakZoekObject.setGroepNaam(groep.getName());
        }

        final User behandelaar = findBehandelaar(zaak);
        if(behandelaar != null) {
            zaakZoekObject.setBehandelaarNaam(getVolledigeNaam(behandelaar));
            zaakZoekObject.setBehandelaarGebruikersnaam(behandelaar.getId());
        }

        if(zaak.getVerlenging() != null) {
            zaakZoekObject.setIndicatieVerlenging(true);
            zaakZoekObject.setDuurVerlenging(String.valueOf(zaak.getVerlenging().getDuur()));
            zaakZoekObject.setRedenVerlenging(zaak.getVerlenging().getReden());
        }

        if (zaak.getOpschorting() != null) {
            zaakZoekObject.setRedenOpschorting(zaak.getOpschorting().getReden());
        }

        final Zaaktype zaaktype = ztcClientService.readZaaktype(zaak.getZaaktype());
        zaakZoekObject.setZaaktypeIdentificatie(zaaktype.getIdentificatie());
        zaakZoekObject.setZaaktypeOmschrijving(zaaktype.getOmschrijving());
        zaakZoekObject.setZaaktypeUuid(UriUtil.uuidFromURI(zaaktype.getUrl()).toString());

        final Status status = zrcClientService.readStatus(zaak.getStatus());
        final Statustype statustype = ztcClientService.readStatustype(status.getStatustype());
        zaakZoekObject.setStatustypeOmschrijving(statustype.getOmschrijving());
        zaakZoekObject.setStatusEindstatus(statustype.getEindstatus());
        zaakZoekObject.setStatusToelichting(status.getStatustoelichting());
        zaakZoekObject.setStatusDatumGezet(DateTimeConverterUtil.convertToDate(status.getDatumStatusGezet()));

        if (zaak.getResultaat() != null) {
            final Resultaat resultaat = zrcClientService.readResultaat(zaak.getResultaat());
            if (resultaat != null) {
                final Resultaattype resultaattype = ztcClientService.readResultaattype(resultaat.getResultaattype());
                zaakZoekObject.setResultaattypeOmschrijving(resultaattype.getOmschrijving());
                zaakZoekObject.setResultaatToelichting(resultaat.getToelichting());
            }
        }
        return zaakZoekObject;
    }


    private String convertToLocatie(final Geometry zaakgeometrie) {
        //todo
        return null;
    }

    private User findBehandelaar(final Zaak zaak) {
        return zgwApiService.findBehandelaarForZaak(zaak.getUrl())
                .map(betrokkene -> betrokkene.getBetrokkeneIdentificatie().getIdentificatie())
                .map(id -> identityService.readUser(id))
                .orElse(null);
    }

    private String getVolledigeNaam(final User user) {
        if (user == null) {
            return null;
        }
        if (user.getFullName() != null) {
            return user.getFullName();
        } else if (user.getFirstName() != null && user.getLastName() != null) {
            return String.format("%s %s", user.getFirstName(), user.getLastName());
        } else {
            return user.getId();
        }
    }

    private Group findGroep(final Zaak zaak) {
        return zgwApiService.findGroepForZaak(zaak.getUrl())
                .map(betrokkene -> betrokkene.getBetrokkeneIdentificatie().getIdentificatie())
                .map(id -> identityService.readGroup(id))
                .orElse(null);
    }

    private CommunicatieKanaal findCommunicatieKanaal(final Zaak zaak) {
        if (zaak.getCommunicatiekanaal() != null) {
            final UUID communicatiekanaalUUID = UriUtil.uuidFromURI(zaak.getCommunicatiekanaal());
            return vrlClientService.findCommunicatiekanaal(communicatiekanaalUUID);
        }
        return null;
    }

}