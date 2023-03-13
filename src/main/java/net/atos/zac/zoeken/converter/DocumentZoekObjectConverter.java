package net.atos.zac.zoeken.converter;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import net.atos.client.zgw.brc.BRCClientService;
import net.atos.client.zgw.drc.DRCClientService;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.ZaakInformatieobject;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Informatieobjecttype;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.enkelvoudiginformatieobject.EnkelvoudigInformatieObjectLockService;
import net.atos.zac.enkelvoudiginformatieobject.model.EnkelvoudigInformatieObjectLock;
import net.atos.zac.identity.IdentityService;
import net.atos.zac.util.DateTimeConverterUtil;
import net.atos.zac.zoeken.model.DocumentIndicatie;
import net.atos.zac.zoeken.model.index.ZoekObjectType;
import net.atos.zac.zoeken.model.zoekobject.DocumentZoekObject;

public class DocumentZoekObjectConverter extends AbstractZoekObjectConverter<DocumentZoekObject> {

    @Inject
    private IdentityService identityService;

    @Inject
    private BRCClientService brcClientService;

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private DRCClientService drcClientService;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private EnkelvoudigInformatieObjectLockService enkelvoudigInformatieObjectLockService;


    @Override
    public DocumentZoekObject convert(final String documentUUID) {
        final EnkelvoudigInformatieobject document = drcClientService.readEnkelvoudigInformatieobject(
                UUID.fromString(documentUUID));
        final List<ZaakInformatieobject> zaakInformatieobjecten = zrcClientService.listZaakinformatieobjecten(document);
        if (zaakInformatieobjecten.isEmpty()) {
            return null;
        }
        return convert(document, zaakInformatieobjecten.get(0));
    }

    private DocumentZoekObject convert(final EnkelvoudigInformatieobject informatieobject,
            final ZaakInformatieobject gekoppeldeZaakInformatieobject) {
        final Zaak zaak = zrcClientService.readZaak(gekoppeldeZaakInformatieobject.getZaakUUID());
        final Zaaktype zaaktype = ztcClientService.readZaaktype(zaak.getZaaktype());
        final Informatieobjecttype informatieobjecttype = ztcClientService.readInformatieobjecttype(
                informatieobject.getInformatieobjecttype());
        final DocumentZoekObject documentZoekObject = new DocumentZoekObject();
        documentZoekObject.setType(ZoekObjectType.DOCUMENT);
        documentZoekObject.setUuid(informatieobject.getUUID().toString());
        documentZoekObject.setIdentificatie(informatieobject.getIdentificatie());
        documentZoekObject.setTitel(informatieobject.getTitel());
        documentZoekObject.setBeschrijving(informatieobject.getBeschrijving());
        documentZoekObject.setZaaktypeOmschrijving(zaaktype.getOmschrijving());
        documentZoekObject.setZaaktypeUuid(zaaktype.getUUID().toString());
        documentZoekObject.setZaaktypeIdentificatie(zaaktype.getIdentificatie());
        documentZoekObject.setZaakIdentificatie(zaak.getIdentificatie());
        documentZoekObject.setZaakUuid(zaak.getUuid().toString());
        if (gekoppeldeZaakInformatieobject.getAardRelatieWeergave() != null) {
            documentZoekObject.setZaakRelatie(gekoppeldeZaakInformatieobject.getAardRelatieWeergave().toValue());
        }
        documentZoekObject.setZaakAfgehandeld(zaak.isOpen());
        documentZoekObject.setCreatiedatum(DateTimeConverterUtil.convertToDate(informatieobject.getCreatiedatum()));
        documentZoekObject.setRegistratiedatum(
                DateTimeConverterUtil.convertToDate(informatieobject.getBeginRegistratie()));
        documentZoekObject.setOntvangstdatum(DateTimeConverterUtil.convertToDate(informatieobject.getOntvangstdatum()));
        documentZoekObject.setVerzenddatum(DateTimeConverterUtil.convertToDate(informatieobject.getVerzenddatum()));
        documentZoekObject.setOndertekeningDatum(
                DateTimeConverterUtil.convertToDate(informatieobject.getOntvangstdatum()));
        documentZoekObject.setVertrouwelijkheidaanduiding(informatieobject.getVertrouwelijkheidaanduiding().toString());
        documentZoekObject.setAuteur(informatieobject.getAuteur());
        if (informatieobject.getStatus() != null) {
            documentZoekObject.setStatus(informatieobject.getStatus());
        }
        documentZoekObject.setFormaat(informatieobject.getFormaat());
        documentZoekObject.setVersie(informatieobject.getVersie());
        documentZoekObject.setBestandsnaam(informatieobject.getBestandsnaam());
        documentZoekObject.setBestandsomvang(documentZoekObject.getBestandsomvang());
        documentZoekObject.setInhoudUrl(documentZoekObject.getInhoudUrl());
        documentZoekObject.setDocumentType(informatieobjecttype.getOmschrijving());
        if (informatieobject.getOndertekening() != null) {
            if (informatieobject.getOndertekening().getSoort() != null) {
                documentZoekObject.setOndertekeningSoort(informatieobject.getOndertekening().getSoort().toValue());
            }
            documentZoekObject.setOndertekeningDatum(
                    DateTimeConverterUtil.convertToDate(informatieobject.getOndertekening().getDatum()));
            documentZoekObject.setIndicatie(DocumentIndicatie.ONDERTEKEND, true);
        }
        documentZoekObject.setIndicatie(DocumentIndicatie.VERGRENDELD, informatieobject.getLocked());
        documentZoekObject.setIndicatie(DocumentIndicatie.GEBRUIKSRECHT, informatieobject.getIndicatieGebruiksrecht());
        documentZoekObject.setIndicatie(DocumentIndicatie.BESLUIT, brcClientService.isInformatieObjectGekoppeldAanBesluit(informatieobject.getUrl()));
        documentZoekObject.setIndicatie(DocumentIndicatie.VERZONDEN, informatieobject.getVerzenddatum() != null);
        if (informatieobject.getLocked()) {
            final EnkelvoudigInformatieObjectLock lock = enkelvoudigInformatieObjectLockService.readLock(
                    informatieobject.getUUID());
            documentZoekObject.setVergrendeldDoorGebruikersnaam(lock.getUserId());
            documentZoekObject.setVergrendeldDoorNaam(identityService.readUser(lock.getUserId()).getFullName());
        }
        return documentZoekObject;
    }

    @Override
    public boolean supports(final ZoekObjectType objectType) {
        return objectType == ZoekObjectType.DOCUMENT;
    }
}
