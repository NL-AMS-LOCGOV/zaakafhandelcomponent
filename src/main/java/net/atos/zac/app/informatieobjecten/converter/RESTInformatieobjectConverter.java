/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.informatieobjecten.converter;

import static net.atos.zac.aanvraag.ProductAanvraagService.ZAAK_INFORMATIEOBJECT_TITEL;

import java.net.URI;
import java.time.LocalDate;
import java.util.Base64;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobjectWithLockAndInhoud;

import net.atos.zac.app.informatieobjecten.model.RESTEnkelvoudigInformatieObjectVersieGegevens;

import org.apache.commons.lang3.BooleanUtils;

import net.atos.client.zgw.drc.DRCClientService;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobjectWithInhoud;
import net.atos.client.zgw.drc.model.InformatieobjectStatus;
import net.atos.client.zgw.shared.model.Vertrouwelijkheidaanduiding;
import net.atos.client.zgw.zrc.model.ZaakInformatieobject;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.zac.app.configuratie.converter.RESTTaalConverter;
import net.atos.zac.app.configuratie.model.RESTTaal;
import net.atos.zac.app.informatieobjecten.model.RESTEnkelvoudigInformatieobject;
import net.atos.zac.app.informatieobjecten.model.RESTFileUpload;
import net.atos.zac.app.taken.model.RESTTaakDocumentData;
import net.atos.zac.authentication.LoggedInUser;
import net.atos.zac.configuratie.ConfiguratieService;
import net.atos.zac.util.UriUtil;

public class RESTInformatieobjectConverter {

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private DRCClientService drcClientService;

    @Inject
    private RESTTaalConverter restTaalConverter;

    @Inject
    private Instance<LoggedInUser> loggedInUserInstance;

    public RESTEnkelvoudigInformatieobject convert(final ZaakInformatieobject zaakInformatieObject) {
        final RESTEnkelvoudigInformatieobject restObject = convert(zaakInformatieObject.getInformatieobject());
        if (zaakInformatieObject.getAardRelatieWeergave() != null) {
            restObject.zaakRelatie = zaakInformatieObject.getAardRelatieWeergave().name();
        }
        restObject.startformulier = ZAAK_INFORMATIEOBJECT_TITEL.equals(zaakInformatieObject.getTitel());
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
        restObject.bronorganisatie = enkelvoudigInformatieObject.getBronorganisatie()
                .equals(ConfiguratieService.BRON_ORGANISATIE) ? null : enkelvoudigInformatieObject.getBronorganisatie();
        restObject.creatiedatum = enkelvoudigInformatieObject.getCreatiedatum();
        if (enkelvoudigInformatieObject.getVertrouwelijkheidaanduiding() != null) {
            restObject.vertrouwelijkheidaanduiding = enkelvoudigInformatieObject.getVertrouwelijkheidaanduiding().toString();
        }
        restObject.auteur = enkelvoudigInformatieObject.getAuteur();
        if (enkelvoudigInformatieObject.getStatus() != null) {
            restObject.status = enkelvoudigInformatieObject.getStatus().toString();
        }
        restObject.formaat = enkelvoudigInformatieObject.getFormaat();
        final RESTTaal taal = restTaalConverter.convert(enkelvoudigInformatieObject.getTaal());
        if (taal != null) {
            restObject.taal = taal.naam;
        }
        restObject.versie = enkelvoudigInformatieObject.getVersie();
        restObject.registratiedatumTijd = enkelvoudigInformatieObject.getBeginRegistratie();
        restObject.bestandsnaam = enkelvoudigInformatieObject.getBestandsnaam();
        if (enkelvoudigInformatieObject.getLink() != null) {
            restObject.link = enkelvoudigInformatieObject.getLink().toString();
        }
        restObject.beschrijving = enkelvoudigInformatieObject.getBeschrijving();
        restObject.ontvangstdatum = enkelvoudigInformatieObject.getOntvangstdatum();
        restObject.verzenddatum = enkelvoudigInformatieObject.getVerzenddatum();
        restObject.locked = BooleanUtils.toBoolean(enkelvoudigInformatieObject.getLocked());
        restObject.bestandsomvang = enkelvoudigInformatieObject.getBestandsomvang();
        restObject.inhoudUrl = enkelvoudigInformatieObject.getInhoud().toString();
        restObject.documentType = ztcClientService.readInformatieobjecttype(enkelvoudigInformatieObject.getInformatieobjecttype()).getOmschrijving();
        if (enkelvoudigInformatieObject.getOndertekening() != null) {
            restObject.ondertekening = enkelvoudigInformatieObject.getOndertekening().getDatum();
        }
        restObject.informatieobjectType = enkelvoudigInformatieObject.getInformatieobjecttype().toString();

        return restObject;
    }

    public EnkelvoudigInformatieobjectWithInhoud convert(final RESTEnkelvoudigInformatieobject restEnkelvoudigInformatieobject, final RESTFileUpload bestand) {
        final EnkelvoudigInformatieobjectWithInhoud data = new EnkelvoudigInformatieobjectWithInhoud(
                ConfiguratieService.BRON_ORGANISATIE,
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
        data.setVertrouwelijkheidaanduiding(Vertrouwelijkheidaanduiding.fromValue(restEnkelvoudigInformatieobject.vertrouwelijkheidaanduiding));
        return data;
    }

    public EnkelvoudigInformatieobjectWithInhoud convert(final RESTTaakDocumentData documentData, final RESTFileUpload bestand) {
        final EnkelvoudigInformatieobjectWithInhoud data = new EnkelvoudigInformatieobjectWithInhoud(
                ConfiguratieService.BRON_ORGANISATIE,
                LocalDate.now(),
                documentData.documentTitel,
                loggedInUserInstance.get().getFullName(),
                ConfiguratieService.TAAL_NEDERLANDS,
                documentData.documentType.url,
                Base64.getEncoder().encodeToString(bestand.file)
        );
        data.setFormaat(bestand.type);
        data.setBestandsnaam(bestand.filename);
        data.setStatus(InformatieobjectStatus.DEFINITIEF);
        data.setVertrouwelijkheidaanduiding(Vertrouwelijkheidaanduiding.fromValue(documentData.documentType.vertrouwelijkheidaanduiding));
        return data;
    }

    public RESTEnkelvoudigInformatieobject convert(final EnkelvoudigInformatieobjectWithLockAndInhoud enkelvoudigInformatieobjectWithLockAndInhoud) {
        final RESTEnkelvoudigInformatieobject restObject = new RESTEnkelvoudigInformatieobject();
        restObject.url = enkelvoudigInformatieobjectWithLockAndInhoud.getUrl().toString();
        restObject.uuid = UriUtil.uuidFromURI(enkelvoudigInformatieobjectWithLockAndInhoud.getUrl()).toString();
        restObject.identificatie = enkelvoudigInformatieobjectWithLockAndInhoud.getIdentificatie();
        restObject.titel = enkelvoudigInformatieobjectWithLockAndInhoud.getTitel();
        restObject.bronorganisatie = enkelvoudigInformatieobjectWithLockAndInhoud.getBronorganisatie()
                .equals(ConfiguratieService.BRON_ORGANISATIE) ? null : enkelvoudigInformatieobjectWithLockAndInhoud.getBronorganisatie();
        restObject.creatiedatum = enkelvoudigInformatieobjectWithLockAndInhoud.getCreatiedatum();
        if (enkelvoudigInformatieobjectWithLockAndInhoud.getVertrouwelijkheidaanduiding() != null) {
            restObject.vertrouwelijkheidaanduiding = enkelvoudigInformatieobjectWithLockAndInhoud.getVertrouwelijkheidaanduiding().toString();
        }
        restObject.auteur = enkelvoudigInformatieobjectWithLockAndInhoud.getAuteur();
        if (enkelvoudigInformatieobjectWithLockAndInhoud.getStatus() != null) {
            restObject.status = enkelvoudigInformatieobjectWithLockAndInhoud.getStatus().toString();
        }
        restObject.formaat = enkelvoudigInformatieobjectWithLockAndInhoud.getFormaat();
        final RESTTaal taal = restTaalConverter.convert(enkelvoudigInformatieobjectWithLockAndInhoud.getTaal());
        if (taal != null) {
            restObject.taal = taal.naam;
        }
        restObject.versie = enkelvoudigInformatieobjectWithLockAndInhoud.getVersie();
        restObject.registratiedatumTijd = enkelvoudigInformatieobjectWithLockAndInhoud.getBeginRegistratie();
        restObject.bestandsnaam = enkelvoudigInformatieobjectWithLockAndInhoud.getBestandsnaam();
        if (enkelvoudigInformatieobjectWithLockAndInhoud.getLink() != null) {
            restObject.link = enkelvoudigInformatieobjectWithLockAndInhoud.getLink().toString();
        }
        restObject.beschrijving = enkelvoudigInformatieobjectWithLockAndInhoud.getBeschrijving();
        restObject.ontvangstdatum = enkelvoudigInformatieobjectWithLockAndInhoud.getOntvangstdatum();
        restObject.verzenddatum = enkelvoudigInformatieobjectWithLockAndInhoud.getVerzenddatum();
        restObject.locked = BooleanUtils.toBoolean(enkelvoudigInformatieobjectWithLockAndInhoud.getLocked());
        restObject.bestandsomvang = enkelvoudigInformatieobjectWithLockAndInhoud.getBestandsomvang();
        restObject.inhoudUrl = enkelvoudigInformatieobjectWithLockAndInhoud.getInhoud();
        restObject.documentType = ztcClientService.readInformatieobjecttype(enkelvoudigInformatieobjectWithLockAndInhoud.getInformatieobjecttype()).getOmschrijving();
        if (enkelvoudigInformatieobjectWithLockAndInhoud.getOndertekening() != null) {
            restObject.ondertekening = enkelvoudigInformatieobjectWithLockAndInhoud.getOndertekening().getDatum();
        }

        return restObject;
    }

    public RESTEnkelvoudigInformatieObjectVersieGegevens convertHuidigeVersie(final EnkelvoudigInformatieobject informatieobject) {
        final RESTEnkelvoudigInformatieObjectVersieGegevens data = new RESTEnkelvoudigInformatieObjectVersieGegevens();

        data.uuid = UriUtil.uuidFromURI(informatieobject.getUrl()).toString();

        if (informatieobject.getStatus() != null) {
            data.status = informatieobject.getStatus().toValue();
        }
        if (informatieobject.getVertrouwelijkheidaanduiding() != null) {
            data.vertrouwelijkheidaanduiding = informatieobject.getVertrouwelijkheidaanduiding().toValue();
        }

        data.beschrijving = informatieobject.getBeschrijving();
        data.verzenddatum = informatieobject.getVerzenddatum();
        data.titel = informatieobject.getTitel();
        data.auteur = informatieobject.getAuteur();
        data.taal = restTaalConverter.convert(informatieobject.getTaal());

        return data;
    }

    public EnkelvoudigInformatieobjectWithLockAndInhoud convert(final RESTEnkelvoudigInformatieObjectVersieGegevens restEnkelvoudigInformatieObjectVersieGegevens,
            final String lock) {
        final EnkelvoudigInformatieobjectWithLockAndInhoud data = new EnkelvoudigInformatieobjectWithLockAndInhoud(lock);

        if (restEnkelvoudigInformatieObjectVersieGegevens.status != null) {
            data.setStatus(InformatieobjectStatus.valueOf(restEnkelvoudigInformatieObjectVersieGegevens.status));
        }
        if (restEnkelvoudigInformatieObjectVersieGegevens.vertrouwelijkheidaanduiding != null) {
            data.setVertrouwelijkheidaanduiding(Vertrouwelijkheidaanduiding.valueOf(
                    restEnkelvoudigInformatieObjectVersieGegevens.vertrouwelijkheidaanduiding));
        }
        if (restEnkelvoudigInformatieObjectVersieGegevens.beschrijving != null) {
            data.setBeschrijving(restEnkelvoudigInformatieObjectVersieGegevens.beschrijving);
        }
        if (restEnkelvoudigInformatieObjectVersieGegevens.verzenddatum != null) {
            data.setVerzenddatum(restEnkelvoudigInformatieObjectVersieGegevens.verzenddatum);
        }
        if (restEnkelvoudigInformatieObjectVersieGegevens.titel != null) {
            data.setTitel(restEnkelvoudigInformatieObjectVersieGegevens.titel);
        }
        if (restEnkelvoudigInformatieObjectVersieGegevens.taal != null) {
            data.setTaal(restEnkelvoudigInformatieObjectVersieGegevens.taal.code);
        }
        if (restEnkelvoudigInformatieObjectVersieGegevens.auteur != null) {
            data.setAuteur(restEnkelvoudigInformatieObjectVersieGegevens.auteur);
        }

        return data;
    }
}
