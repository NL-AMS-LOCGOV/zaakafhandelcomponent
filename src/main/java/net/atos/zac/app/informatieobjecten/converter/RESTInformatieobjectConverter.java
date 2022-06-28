/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.informatieobjecten.converter;

import static net.atos.zac.configuratie.ConfiguratieService.OMSCHRIJVING_TAAK_DOCUMENT;

import java.net.URI;
import java.time.LocalDate;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import net.atos.zac.app.identity.converter.RESTUserConverter;

import net.atos.zac.enkelvoudiginformatieobject.EnkelvoudigInformatieObjectLockService;

import net.atos.zac.enkelvoudiginformatieobject.model.EnkelvoudigInformatieObjectLock;

import net.atos.zac.identity.IdentityService;

import org.apache.commons.lang3.BooleanUtils;

import net.atos.client.zgw.drc.DRCClientService;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobjectWithInhoud;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobjectWithInhoudAndLock;
import net.atos.client.zgw.drc.model.InformatieobjectStatus;
import net.atos.client.zgw.shared.model.Vertrouwelijkheidaanduiding;
import net.atos.client.zgw.zrc.model.ZaakInformatieobject;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.zac.app.configuratie.converter.RESTTaalConverter;
import net.atos.zac.app.configuratie.model.RESTTaal;
import net.atos.zac.app.informatieobjecten.model.RESTEnkelvoudigInformatieObjectVersieGegevens;
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

    @Inject
    private RESTUserConverter restUserConverter;

    @Inject
    private EnkelvoudigInformatieObjectLockService enkelvoudigInformatieObjectLockService;

    @Inject
    private IdentityService identityService;

    public RESTEnkelvoudigInformatieobject convert(final ZaakInformatieobject zaakInformatieObject) {
        final RESTEnkelvoudigInformatieobject restEnkelvoudigInformatieobject = convert(zaakInformatieObject.getInformatieobject());
        return restEnkelvoudigInformatieobject;
    }

    public RESTEnkelvoudigInformatieobject convert(final URI informatieObjectURI) {
        final EnkelvoudigInformatieobject enkelvoudigInformatieObject = drcClientService.readEnkelvoudigInformatieobject(informatieObjectURI);
        return convert(enkelvoudigInformatieObject);
    }

    public RESTEnkelvoudigInformatieobject convert(final EnkelvoudigInformatieobject enkelvoudigInformatieObject) {
        final RESTEnkelvoudigInformatieobject restEnkelvoudigInformatieobject
                = new RESTEnkelvoudigInformatieobject();
        restEnkelvoudigInformatieobject.uuid = UriUtil.uuidFromURI(enkelvoudigInformatieObject.getUrl()).toString();
        restEnkelvoudigInformatieobject.identificatie = enkelvoudigInformatieObject.getIdentificatie();
        restEnkelvoudigInformatieobject.titel = enkelvoudigInformatieObject.getTitel();
        restEnkelvoudigInformatieobject.bronorganisatie = enkelvoudigInformatieObject.getBronorganisatie()
                .equals(ConfiguratieService.BRON_ORGANISATIE) ? null : enkelvoudigInformatieObject.getBronorganisatie();
        restEnkelvoudigInformatieobject.creatiedatum = enkelvoudigInformatieObject.getCreatiedatum();
        if (enkelvoudigInformatieObject.getVertrouwelijkheidaanduiding() != null) {
            restEnkelvoudigInformatieobject.vertrouwelijkheidaanduiding = enkelvoudigInformatieObject.getVertrouwelijkheidaanduiding().toString();
        }
        restEnkelvoudigInformatieobject.auteur = enkelvoudigInformatieObject.getAuteur();
        if (enkelvoudigInformatieObject.getStatus() != null) {
            restEnkelvoudigInformatieobject.status = enkelvoudigInformatieObject.getStatus().toString();
        }
        restEnkelvoudigInformatieobject.formaat = enkelvoudigInformatieObject.getFormaat();
        final RESTTaal taal = restTaalConverter.convert(enkelvoudigInformatieObject.getTaal());
        if (taal != null) {
            restEnkelvoudigInformatieobject.taal = taal.naam;
        }
        restEnkelvoudigInformatieobject.versie = enkelvoudigInformatieObject.getVersie();
        restEnkelvoudigInformatieobject.registratiedatumTijd = enkelvoudigInformatieObject.getBeginRegistratie();
        restEnkelvoudigInformatieobject.bestandsnaam = enkelvoudigInformatieObject.getBestandsnaam();
        if (enkelvoudigInformatieObject.getLink() != null) {
            restEnkelvoudigInformatieobject.link = enkelvoudigInformatieObject.getLink().toString();
        }
        restEnkelvoudigInformatieobject.beschrijving = enkelvoudigInformatieObject.getBeschrijving();
        restEnkelvoudigInformatieobject.ontvangstdatum = enkelvoudigInformatieObject.getOntvangstdatum();
        restEnkelvoudigInformatieobject.verzenddatum = enkelvoudigInformatieObject.getVerzenddatum();
        if (enkelvoudigInformatieObject.getLocked()) {
            final EnkelvoudigInformatieObjectLock enkelvoudigInformatieObjectLock =
            enkelvoudigInformatieObjectLockService.findLock(enkelvoudigInformatieObject.getUUID());
            restEnkelvoudigInformatieobject.gelockedDoor =
                    restUserConverter.convertUser(identityService.readUser(enkelvoudigInformatieObjectLock.getUserId()));
        }
        restEnkelvoudigInformatieobject.bestandsomvang = enkelvoudigInformatieObject.getBestandsomvang();
        restEnkelvoudigInformatieobject.informatieobjectTypeOmschrijving = ztcClientService.readInformatieobjecttype(
                enkelvoudigInformatieObject.getInformatieobjecttype()).getOmschrijving();
        restEnkelvoudigInformatieobject.informatieobjectTypeUUID = enkelvoudigInformatieObject.getInformatieobjectTypeUUID();

        return restEnkelvoudigInformatieobject;
    }

    public EnkelvoudigInformatieobjectWithInhoud convertZaakObject(final RESTEnkelvoudigInformatieobject restEnkelvoudigInformatieobject, final RESTFileUpload bestand) {

        final EnkelvoudigInformatieobjectWithInhoud enkelvoudigInformatieobjectWithInhoud = new EnkelvoudigInformatieobjectWithInhoud();
        enkelvoudigInformatieobjectWithInhoud.setBronorganisatie(ConfiguratieService.BRON_ORGANISATIE);
        enkelvoudigInformatieobjectWithInhoud.setCreatiedatum(restEnkelvoudigInformatieobject.creatiedatum);
        enkelvoudigInformatieobjectWithInhoud.setTitel(restEnkelvoudigInformatieobject.titel);
        enkelvoudigInformatieobjectWithInhoud.setAuteur(restEnkelvoudigInformatieobject.auteur);
        enkelvoudigInformatieobjectWithInhoud.setTaal(restEnkelvoudigInformatieobject.taal);
        enkelvoudigInformatieobjectWithInhoud.setInformatieobjecttype(
                ztcClientService.readInformatieobjecttype(restEnkelvoudigInformatieobject.informatieobjectTypeUUID).getUrl());
        enkelvoudigInformatieobjectWithInhoud.setInhoud(bestand.file);
        enkelvoudigInformatieobjectWithInhoud.setFormaat(bestand.type);
        enkelvoudigInformatieobjectWithInhoud.setBestandsnaam(restEnkelvoudigInformatieobject.bestandsnaam);
        enkelvoudigInformatieobjectWithInhoud.setBeschrijving(restEnkelvoudigInformatieobject.beschrijving);
        enkelvoudigInformatieobjectWithInhoud.setStatus(InformatieobjectStatus.valueOf(restEnkelvoudigInformatieobject.status));
        enkelvoudigInformatieobjectWithInhoud.setVerzenddatum(restEnkelvoudigInformatieobject.verzenddatum);
        enkelvoudigInformatieobjectWithInhoud.setOntvangstdatum(restEnkelvoudigInformatieobject.ontvangstdatum);
        enkelvoudigInformatieobjectWithInhoud.setVertrouwelijkheidaanduiding(
                Vertrouwelijkheidaanduiding.fromValue(restEnkelvoudigInformatieobject.vertrouwelijkheidaanduiding));
        return enkelvoudigInformatieobjectWithInhoud;
    }

    public EnkelvoudigInformatieobjectWithInhoud convertTaakObject(
            final RESTEnkelvoudigInformatieobject restEnkelvoudigInformatieobject, final RESTFileUpload bestand) {
        final EnkelvoudigInformatieobjectWithInhoud enkelvoudigInformatieobjectWithInhoud = new EnkelvoudigInformatieobjectWithInhoud();
        enkelvoudigInformatieobjectWithInhoud.setBronorganisatie(ConfiguratieService.BRON_ORGANISATIE);
        enkelvoudigInformatieobjectWithInhoud.setCreatiedatum(LocalDate.now());
        enkelvoudigInformatieobjectWithInhoud.setTitel(restEnkelvoudigInformatieobject.titel);
        enkelvoudigInformatieobjectWithInhoud.setAuteur(loggedInUserInstance.get().getFullName());
        enkelvoudigInformatieobjectWithInhoud.setTaal(ConfiguratieService.TAAL_NEDERLANDS);
        enkelvoudigInformatieobjectWithInhoud.setInformatieobjecttype(
                ztcClientService.readInformatieobjecttype(restEnkelvoudigInformatieobject.informatieobjectTypeUUID).getUrl());
        enkelvoudigInformatieobjectWithInhoud.setInhoud(bestand.file);
        enkelvoudigInformatieobjectWithInhoud.setFormaat(bestand.type);
        enkelvoudigInformatieobjectWithInhoud.setBestandsnaam(bestand.filename);
        enkelvoudigInformatieobjectWithInhoud.setBeschrijving(OMSCHRIJVING_TAAK_DOCUMENT);
        enkelvoudigInformatieobjectWithInhoud.setStatus(InformatieobjectStatus.DEFINITIEF);
        enkelvoudigInformatieobjectWithInhoud.setVerzenddatum(restEnkelvoudigInformatieobject.verzenddatum);
        enkelvoudigInformatieobjectWithInhoud.setOntvangstdatum(restEnkelvoudigInformatieobject.ontvangstdatum);
        enkelvoudigInformatieobjectWithInhoud.setVertrouwelijkheidaanduiding(Vertrouwelijkheidaanduiding.OPENBAAR);
        return enkelvoudigInformatieobjectWithInhoud;
    }

    public EnkelvoudigInformatieobjectWithInhoud convert(final RESTTaakDocumentData documentData, final RESTFileUpload bestand) {
        final EnkelvoudigInformatieobjectWithInhoud enkelvoudigInformatieobjectWithInhoud = new EnkelvoudigInformatieobjectWithInhoud();
        enkelvoudigInformatieobjectWithInhoud.setBronorganisatie(ConfiguratieService.BRON_ORGANISATIE);
        enkelvoudigInformatieobjectWithInhoud.setCreatiedatum(LocalDate.now());
        enkelvoudigInformatieobjectWithInhoud.setTitel(documentData.documentTitel);
        enkelvoudigInformatieobjectWithInhoud.setAuteur(loggedInUserInstance.get().getFullName());
        enkelvoudigInformatieobjectWithInhoud.setTaal(ConfiguratieService.TAAL_NEDERLANDS);
        enkelvoudigInformatieobjectWithInhoud.setInformatieobjecttype(ztcClientService.readInformatieobjecttype(documentData.documentType.uuid).getUrl());
        enkelvoudigInformatieobjectWithInhoud.setInhoud(bestand.file);
        enkelvoudigInformatieobjectWithInhoud.setFormaat(bestand.type);
        enkelvoudigInformatieobjectWithInhoud.setBestandsnaam(bestand.filename);
        enkelvoudigInformatieobjectWithInhoud.setStatus(InformatieobjectStatus.DEFINITIEF);
        enkelvoudigInformatieobjectWithInhoud.setVertrouwelijkheidaanduiding(
                Vertrouwelijkheidaanduiding.fromValue(documentData.documentType.vertrouwelijkheidaanduiding));
        return enkelvoudigInformatieobjectWithInhoud;
    }

    public RESTEnkelvoudigInformatieobject convert(final EnkelvoudigInformatieobjectWithInhoudAndLock enkelvoudigInformatieobjectWithInhoudAndLock) {
        final RESTEnkelvoudigInformatieobject restEnkelvoudigInformatieobject = new RESTEnkelvoudigInformatieobject();
        restEnkelvoudigInformatieobject.uuid = UriUtil.uuidFromURI(enkelvoudigInformatieobjectWithInhoudAndLock.getUrl()).toString();
        restEnkelvoudigInformatieobject.identificatie = enkelvoudigInformatieobjectWithInhoudAndLock.getIdentificatie();
        restEnkelvoudigInformatieobject.titel = enkelvoudigInformatieobjectWithInhoudAndLock.getTitel();
        restEnkelvoudigInformatieobject.bronorganisatie = enkelvoudigInformatieobjectWithInhoudAndLock.getBronorganisatie()
                .equals(ConfiguratieService.BRON_ORGANISATIE) ? null : enkelvoudigInformatieobjectWithInhoudAndLock.getBronorganisatie();
        restEnkelvoudigInformatieobject.creatiedatum = enkelvoudigInformatieobjectWithInhoudAndLock.getCreatiedatum();
        if (enkelvoudigInformatieobjectWithInhoudAndLock.getVertrouwelijkheidaanduiding() != null) {
            restEnkelvoudigInformatieobject.vertrouwelijkheidaanduiding = enkelvoudigInformatieobjectWithInhoudAndLock.getVertrouwelijkheidaanduiding()
                    .toString();
        }
        restEnkelvoudigInformatieobject.auteur = enkelvoudigInformatieobjectWithInhoudAndLock.getAuteur();
        if (enkelvoudigInformatieobjectWithInhoudAndLock.getStatus() != null) {
            restEnkelvoudigInformatieobject.status = enkelvoudigInformatieobjectWithInhoudAndLock.getStatus().toString();
        }
        restEnkelvoudigInformatieobject.formaat = enkelvoudigInformatieobjectWithInhoudAndLock.getFormaat();
        final RESTTaal taal = restTaalConverter.convert(enkelvoudigInformatieobjectWithInhoudAndLock.getTaal());
        if (taal != null) {
            restEnkelvoudigInformatieobject.taal = taal.naam;
        }
        restEnkelvoudigInformatieobject.versie = enkelvoudigInformatieobjectWithInhoudAndLock.getVersie();
        restEnkelvoudigInformatieobject.registratiedatumTijd = enkelvoudigInformatieobjectWithInhoudAndLock.getBeginRegistratie();
        restEnkelvoudigInformatieobject.bestandsnaam = enkelvoudigInformatieobjectWithInhoudAndLock.getBestandsnaam();
        if (enkelvoudigInformatieobjectWithInhoudAndLock.getLink() != null) {
            restEnkelvoudigInformatieobject.link = enkelvoudigInformatieobjectWithInhoudAndLock.getLink().toString();
        }
        restEnkelvoudigInformatieobject.beschrijving = enkelvoudigInformatieobjectWithInhoudAndLock.getBeschrijving();
        restEnkelvoudigInformatieobject.ontvangstdatum = enkelvoudigInformatieobjectWithInhoudAndLock.getOntvangstdatum();
        restEnkelvoudigInformatieobject.verzenddatum = enkelvoudigInformatieobjectWithInhoudAndLock.getVerzenddatum();
        if (enkelvoudigInformatieobjectWithInhoudAndLock.getLocked()) {
            final EnkelvoudigInformatieObjectLock enkelvoudigInformatieObjectLock =
                    enkelvoudigInformatieObjectLockService.findLock(enkelvoudigInformatieobjectWithInhoudAndLock.getUUID());
            restEnkelvoudigInformatieobject.gelockedDoor =
                    restUserConverter.convertUser(identityService.readUser(enkelvoudigInformatieObjectLock.getUserId()));
        }
        restEnkelvoudigInformatieobject.gelockedDoor = restUserConverter.convertUser(loggedInUserInstance.get());
        restEnkelvoudigInformatieobject.bestandsomvang = enkelvoudigInformatieobjectWithInhoudAndLock.getBestandsomvang();
        restEnkelvoudigInformatieobject.informatieobjectTypeOmschrijving = ztcClientService.readInformatieobjecttype(
                enkelvoudigInformatieobjectWithInhoudAndLock.getInformatieobjecttype()).getOmschrijving();
        return restEnkelvoudigInformatieobject;
    }

    public RESTEnkelvoudigInformatieObjectVersieGegevens convertHuidigeVersie(final EnkelvoudigInformatieobject informatieobject) {
        final RESTEnkelvoudigInformatieObjectVersieGegevens restEnkelvoudigInformatieObjectVersieGegevens = new RESTEnkelvoudigInformatieObjectVersieGegevens();

        restEnkelvoudigInformatieObjectVersieGegevens.uuid = UriUtil.uuidFromURI(informatieobject.getUrl()).toString();

        if (informatieobject.getStatus() != null) {
            restEnkelvoudigInformatieObjectVersieGegevens.status = informatieobject.getStatus().toValue();
        }
        if (informatieobject.getVertrouwelijkheidaanduiding() != null) {
            restEnkelvoudigInformatieObjectVersieGegevens.vertrouwelijkheidaanduiding = informatieobject.getVertrouwelijkheidaanduiding().toValue();
        }

        restEnkelvoudigInformatieObjectVersieGegevens.beschrijving = informatieobject.getBeschrijving();
        restEnkelvoudigInformatieObjectVersieGegevens.verzenddatum = informatieobject.getVerzenddatum();
        restEnkelvoudigInformatieObjectVersieGegevens.titel = informatieobject.getTitel();
        restEnkelvoudigInformatieObjectVersieGegevens.auteur = informatieobject.getAuteur();
        restEnkelvoudigInformatieObjectVersieGegevens.taal = restTaalConverter.convert(informatieobject.getTaal());
        restEnkelvoudigInformatieObjectVersieGegevens.bestandsnaam = informatieobject.getInhoud().toString();

        return restEnkelvoudigInformatieObjectVersieGegevens;
    }

    public EnkelvoudigInformatieobjectWithInhoudAndLock convert(
            final RESTEnkelvoudigInformatieObjectVersieGegevens restEnkelvoudigInformatieObjectVersieGegevens,
            final String lock, final RESTFileUpload file) {
        final EnkelvoudigInformatieobjectWithInhoudAndLock enkelvoudigInformatieobjectWithInhoudAndLock = new EnkelvoudigInformatieobjectWithInhoudAndLock();
        enkelvoudigInformatieobjectWithInhoudAndLock.setLock(lock);

        if (restEnkelvoudigInformatieObjectVersieGegevens.status != null) {
            enkelvoudigInformatieobjectWithInhoudAndLock.setStatus(InformatieobjectStatus.valueOf(restEnkelvoudigInformatieObjectVersieGegevens.status));
        }
        if (restEnkelvoudigInformatieObjectVersieGegevens.vertrouwelijkheidaanduiding != null) {
            enkelvoudigInformatieobjectWithInhoudAndLock.setVertrouwelijkheidaanduiding(Vertrouwelijkheidaanduiding.valueOf(
                    restEnkelvoudigInformatieObjectVersieGegevens.vertrouwelijkheidaanduiding));
        }
        if (restEnkelvoudigInformatieObjectVersieGegevens.beschrijving != null) {
            enkelvoudigInformatieobjectWithInhoudAndLock.setBeschrijving(restEnkelvoudigInformatieObjectVersieGegevens.beschrijving);
        }
        if (restEnkelvoudigInformatieObjectVersieGegevens.verzenddatum != null) {
            enkelvoudigInformatieobjectWithInhoudAndLock.setVerzenddatum(restEnkelvoudigInformatieObjectVersieGegevens.verzenddatum);
        }
        if (restEnkelvoudigInformatieObjectVersieGegevens.ontvangstdatum != null) {
            enkelvoudigInformatieobjectWithInhoudAndLock.setOntvangstdatum(restEnkelvoudigInformatieObjectVersieGegevens.ontvangstdatum);
        }
        if (restEnkelvoudigInformatieObjectVersieGegevens.titel != null) {
            enkelvoudigInformatieobjectWithInhoudAndLock.setTitel(restEnkelvoudigInformatieObjectVersieGegevens.titel);
        }
        if (restEnkelvoudigInformatieObjectVersieGegevens.taal != null) {
            enkelvoudigInformatieobjectWithInhoudAndLock.setTaal(restEnkelvoudigInformatieObjectVersieGegevens.taal.code);
        }
        if (restEnkelvoudigInformatieObjectVersieGegevens.auteur != null) {
            enkelvoudigInformatieobjectWithInhoudAndLock.setAuteur(restEnkelvoudigInformatieObjectVersieGegevens.auteur);
        }
        if (restEnkelvoudigInformatieObjectVersieGegevens.bestandsnaam != null) {
            enkelvoudigInformatieobjectWithInhoudAndLock.setBestandsnaam((restEnkelvoudigInformatieObjectVersieGegevens.bestandsnaam));
        }
        if (file != null && file.file != null) {
            enkelvoudigInformatieobjectWithInhoudAndLock.setInhoud(file.file);
            enkelvoudigInformatieobjectWithInhoudAndLock.setFormaat(file.type);
        }

        return enkelvoudigInformatieobjectWithInhoudAndLock;
    }
}
