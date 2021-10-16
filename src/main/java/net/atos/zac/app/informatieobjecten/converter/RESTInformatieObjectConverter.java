/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.informatieobjecten.converter;

import java.net.URI;

import javax.inject.Inject;

import org.apache.commons.lang3.BooleanUtils;
import org.jboss.resteasy.util.Base64;

import net.atos.client.zgw.drc.DRCClient;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieObject;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieObjectData;
import net.atos.client.zgw.drc.model.InformatieobjectStatus;
import net.atos.client.zgw.shared.model.Vertrouwelijkheidaanduiding;
import net.atos.client.zgw.zrc.model.ZaakInformatieObject;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.zac.app.informatieobjecten.model.RESTFileUpload;
import net.atos.zac.app.informatieobjecten.model.RESTInformatieObject;
import net.atos.zac.util.ConfigurationService;
import net.atos.zac.util.UriUtil;

public class RESTInformatieObjectConverter {

    @Inject
    private ZTCClientService ztcClientService;

    public RESTInformatieObject convert(final ZaakInformatieObject zaakInformatieObject) {
        final RESTInformatieObject restObject = convert(zaakInformatieObject.getInformatieobject());
        if (zaakInformatieObject.getAardRelatieWeergave() != null) {
            restObject.zaakRelatie = zaakInformatieObject.getAardRelatieWeergave().name();
        }
        return restObject;
    }

    public RESTInformatieObject convert(final URI informatieObjectURI) {
        final EnkelvoudigInformatieObject enkelvoudigInformatieObject = DRCClient.getEnkelvoudigInformatieObject(informatieObjectURI);
        return convert(enkelvoudigInformatieObject);
    }

    public RESTInformatieObject convert(final EnkelvoudigInformatieObject enkelvoudigInformatieObject) {
        final RESTInformatieObject restObject = new RESTInformatieObject();
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
        restObject.documentType = ztcClientService.getInformatieobjecttype(enkelvoudigInformatieObject.getInformatieobjecttype()).getOmschrijving();

        return restObject;
    }

    public EnkelvoudigInformatieObjectData convert(final RESTInformatieObject restInformatieObject, final RESTFileUpload bestand) {
        final EnkelvoudigInformatieObjectData data = new EnkelvoudigInformatieObjectData(
                ConfigurationService.BRON_ORGANISATIE,
                restInformatieObject.creatiedatum,
                restInformatieObject.titel,
                restInformatieObject.auteur,
                restInformatieObject.taal,
                URI.create(restInformatieObject.informatieobjectType),
                Base64.encodeBytes(bestand.file)
        );
        data.setFormaat(bestand.type);
        data.setBestandsnaam(restInformatieObject.bestandsnaam);
        data.setBeschrijving(restInformatieObject.beschrijving);
        data.setStatus(InformatieobjectStatus.valueOf(restInformatieObject.status));
        data.setIndicatieGebruiksrecht(false);
        data.setVertrouwelijkheidaanduiding(Vertrouwelijkheidaanduiding.valueOf(restInformatieObject.vertrouwelijkheidaanduiding));
        return data;
    }

}
