/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.informatieobjecten.converter;

import java.net.URI;
import java.time.LocalDate;
import java.util.Base64;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.lang3.BooleanUtils;

import net.atos.client.zgw.drc.DRCClientService;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobjectWithInhoud;
import net.atos.client.zgw.drc.model.InformatieobjectStatus;
import net.atos.client.zgw.shared.model.Vertrouwelijkheidaanduiding;
import net.atos.client.zgw.zrc.model.ZaakInformatieobject;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.zac.app.informatieobjecten.model.RESTEnkelvoudigInformatieobject;
import net.atos.zac.app.informatieobjecten.model.RESTFileUpload;
import net.atos.zac.app.taken.model.RESTTaakDocumentData;
import net.atos.zac.authentication.IngelogdeMedewerker;
import net.atos.zac.authentication.Medewerker;
import net.atos.zac.util.ConfigurationService;
import net.atos.zac.util.UriUtil;

public class RESTInformatieobjectConverter {

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private DRCClientService drcClientService;

    @Inject
    @IngelogdeMedewerker
    private Instance<Medewerker> ingelogdeMedewerker;

    public RESTEnkelvoudigInformatieobject convert(final ZaakInformatieobject zaakInformatieObject) {
        final RESTEnkelvoudigInformatieobject restObject = convert(zaakInformatieObject.getInformatieobject());
        if (zaakInformatieObject.getAardRelatieWeergave() != null) {
            restObject.zaakRelatie = zaakInformatieObject.getAardRelatieWeergave().name();
        }
        return restObject;
    }

    public RESTEnkelvoudigInformatieobject convert(final URI informatieObjectURI) {
        final EnkelvoudigInformatieobject enkelvoudigInformatieObject = drcClientService.readEnkelvoudigInformatieobject(informatieObjectURI);
        return convert(enkelvoudigInformatieObject);
    }

    public RESTEnkelvoudigInformatieobject convert(final EnkelvoudigInformatieobject enkelvoudigInformatieObject) {
        final RESTEnkelvoudigInformatieobject restObject = new RESTEnkelvoudigInformatieobject();
        restObject.url = enkelvoudigInformatieObject.getUrl().toString();
        restObject.uuid = UriUtil.uuidFromURI(enkelvoudigInformatieObject.getUrl()).toString();
        restObject.identificatie = enkelvoudigInformatieObject.getIdentificatie();
        restObject.titel = enkelvoudigInformatieObject.getTitel();
        restObject.bronorganisatie = enkelvoudigInformatieObject.getBronorganisatie();
        restObject.creatiedatum = enkelvoudigInformatieObject.getCreatiedatum();
        if (enkelvoudigInformatieObject.getVertrouwelijkheidaanduiding() != null) {
            restObject.vertrouwelijkheidaanduiding = enkelvoudigInformatieObject.getVertrouwelijkheidaanduiding().toString();
        }
        restObject.auteur = enkelvoudigInformatieObject.getAuteur();
        if (enkelvoudigInformatieObject.getStatus() != null) {
            restObject.status = enkelvoudigInformatieObject.getStatus().toString();
        }
        restObject.formaat = enkelvoudigInformatieObject.getFormaat();
        restObject.taal = enkelvoudigInformatieObject.getTaal();
        restObject.versie = enkelvoudigInformatieObject.getVersie();
        restObject.registratiedatumTijd = enkelvoudigInformatieObject.getBeginRegistratie();
        restObject.bestandsnaam = enkelvoudigInformatieObject.getBestandsnaam();
        if (enkelvoudigInformatieObject.getLink() != null) {
            restObject.link = enkelvoudigInformatieObject.getLink().toString();
        }
        restObject.beschrijving = enkelvoudigInformatieObject.getBeschrijving();
        restObject.ontvangstdatum = enkelvoudigInformatieObject.getOntvangstdatum();
        restObject.verzenddatum = enkelvoudigInformatieObject.getVerzenddatum();
        restObject.indicatieGebruiksrecht = BooleanUtils.toBoolean(enkelvoudigInformatieObject.getIndicatieGebruiksrecht());
        restObject.locked = BooleanUtils.toBoolean(enkelvoudigInformatieObject.getLocked());
        restObject.bestandsomvang = enkelvoudigInformatieObject.getBestandsomvang();
        restObject.inhoudUrl = enkelvoudigInformatieObject.getInhoud().toString();
        restObject.documentType = ztcClientService.readInformatieobjecttype(enkelvoudigInformatieObject.getInformatieobjecttype()).getOmschrijving();
        if (enkelvoudigInformatieObject.getOndertekening() != null) {
            restObject.ondertekening = enkelvoudigInformatieObject.getOndertekening().getDatum();
        }

        return restObject;
    }

    public EnkelvoudigInformatieobjectWithInhoud convert(final RESTEnkelvoudigInformatieobject restEnkelvoudigInformatieobject, final RESTFileUpload bestand) {
        final EnkelvoudigInformatieobjectWithInhoud data = new EnkelvoudigInformatieobjectWithInhoud(
                ConfigurationService.BRON_ORGANISATIE,
                restEnkelvoudigInformatieobject.creatiedatum,
                restEnkelvoudigInformatieobject.titel,
                restEnkelvoudigInformatieobject.auteur,
                restEnkelvoudigInformatieobject.taal,
                URI.create(restEnkelvoudigInformatieobject.informatieobjectType),
                Base64.getEncoder().encodeToString(bestand.file)
        );
        data.setFormaat(bestand.type);
        data.setBestandsnaam(restEnkelvoudigInformatieobject.bestandsnaam);
        data.setBeschrijving(restEnkelvoudigInformatieobject.beschrijving);
        data.setStatus(InformatieobjectStatus.valueOf(restEnkelvoudigInformatieobject.status));
        data.setIndicatieGebruiksrecht(false);
        data.setVertrouwelijkheidaanduiding(Vertrouwelijkheidaanduiding.valueOf(restEnkelvoudigInformatieobject.vertrouwelijkheidaanduiding));
        return data;
    }

    public EnkelvoudigInformatieobjectWithInhoud convert(final RESTTaakDocumentData documentData, final RESTFileUpload bestand) {
        final EnkelvoudigInformatieobjectWithInhoud data = new EnkelvoudigInformatieobjectWithInhoud(
                ConfigurationService.BRON_ORGANISATIE,
                LocalDate.now(),
                documentData.documentTitel,
                ingelogdeMedewerker.get().getNaam(),
                ConfigurationService.DEFAULT_DOCUMENT_TAAL,
                documentData.documentType.url,
                Base64.getEncoder().encodeToString(bestand.file)
        );
        data.setFormaat(bestand.type);
        data.setBestandsnaam(bestand.filename);
        data.setStatus(InformatieobjectStatus.DEFINITIEF);
        data.setIndicatieGebruiksrecht(false);
        data.setVertrouwelijkheidaanduiding(Vertrouwelijkheidaanduiding.fromValue(documentData.documentType.vertrouwelijkheidaanduiding));
        return data;
    }

}
