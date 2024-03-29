/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.informatieobjecten.converter;

import static net.atos.zac.configuratie.ConfiguratieService.OMSCHRIJVING_TAAK_DOCUMENT;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import net.atos.client.zgw.brc.BRCClientService;
import net.atos.client.zgw.drc.DRCClientService;
import net.atos.client.zgw.drc.model.AbstractEnkelvoudigInformatieobject;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobjectWithInhoud;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobjectWithInhoudAndLock;
import net.atos.client.zgw.drc.model.InformatieobjectStatus;
import net.atos.client.zgw.shared.model.Vertrouwelijkheidaanduiding;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.ZaakInformatieobject;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.zac.app.configuratie.converter.RESTTaalConverter;
import net.atos.zac.app.identity.converter.RESTUserConverter;
import net.atos.zac.app.informatieobjecten.model.RESTEnkelvoudigInformatieObjectVersieGegevens;
import net.atos.zac.app.informatieobjecten.model.RESTEnkelvoudigInformatieobject;
import net.atos.zac.app.informatieobjecten.model.RESTFileUpload;
import net.atos.zac.app.informatieobjecten.model.RESTGekoppeldeZaakEnkelvoudigInformatieObject;
import net.atos.zac.app.policy.converter.RESTRechtenConverter;
import net.atos.zac.app.taken.model.RESTTaakDocumentData;
import net.atos.zac.app.zaken.model.RelatieType;
import net.atos.zac.authentication.LoggedInUser;
import net.atos.zac.configuratie.ConfiguratieService;
import net.atos.zac.enkelvoudiginformatieobject.EnkelvoudigInformatieObjectLockService;
import net.atos.zac.enkelvoudiginformatieobject.model.EnkelvoudigInformatieObjectLock;
import net.atos.zac.identity.IdentityService;
import net.atos.zac.policy.PolicyService;
import net.atos.zac.policy.output.DocumentRechten;
import net.atos.zac.util.UriUtil;

public class RESTInformatieobjectConverter {

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private DRCClientService drcClientService;

    @Inject
    private BRCClientService brcClientService;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private RESTTaalConverter restTaalConverter;

    @Inject
    private Instance<LoggedInUser> loggedInUserInstance;

    @Inject
    private RESTUserConverter restUserConverter;

    @Inject
    private RESTOndertekeningConverter restOndertekeningConverter;

    @Inject
    private EnkelvoudigInformatieObjectLockService enkelvoudigInformatieObjectLockService;

    @Inject
    private IdentityService identityService;

    @Inject
    private RESTRechtenConverter rechtenConverter;

    @Inject
    private PolicyService policyService;

    @Inject
    private ConfiguratieService configuratieService;

    public List<RESTEnkelvoudigInformatieobject> convertToREST(
            final List<ZaakInformatieobject> zaakInformatieobjecten) {
        return zaakInformatieobjecten.stream().map(this::convertToREST).toList();
    }

    public RESTEnkelvoudigInformatieobject convertToREST(final ZaakInformatieobject zaakInformatieObject) {
        final EnkelvoudigInformatieobject enkelvoudigInformatieObject = drcClientService.readEnkelvoudigInformatieobject(
                zaakInformatieObject.getInformatieobject());
        final Zaak zaak = zrcClientService.readZaak(zaakInformatieObject.getZaakUUID());
        return convertToREST(enkelvoudigInformatieObject, zaak);
    }

    public RESTEnkelvoudigInformatieobject convertToREST(
            final AbstractEnkelvoudigInformatieobject enkelvoudigInformatieObject) {
        return convertToREST(enkelvoudigInformatieObject, null);
    }

    public RESTEnkelvoudigInformatieobject convertToREST(
            final AbstractEnkelvoudigInformatieobject enkelvoudigInformatieObject, final Zaak zaak) {
        final EnkelvoudigInformatieObjectLock lock = enkelvoudigInformatieObject.getLocked() ?
                enkelvoudigInformatieObjectLockService.findLock(enkelvoudigInformatieObject.getUUID()).orElse(null)
                : null;
        final DocumentRechten rechten = policyService.readDocumentRechten(enkelvoudigInformatieObject, lock, zaak);
        final RESTEnkelvoudigInformatieobject restEnkelvoudigInformatieobject = new RESTEnkelvoudigInformatieobject();
        restEnkelvoudigInformatieobject.uuid = enkelvoudigInformatieObject.getUUID();
        restEnkelvoudigInformatieobject.identificatie = enkelvoudigInformatieObject.getIdentificatie();
        restEnkelvoudigInformatieobject.rechten = rechtenConverter.convert(rechten);
        restEnkelvoudigInformatieobject.isBesluitDocument = brcClientService.isInformatieObjectGekoppeldAanBesluit(enkelvoudigInformatieObject.getUrl());
        if (rechten.getLezen()) {
            restEnkelvoudigInformatieobject.titel = enkelvoudigInformatieObject.getTitel();
            restEnkelvoudigInformatieobject.bronorganisatie = enkelvoudigInformatieObject.getBronorganisatie()
                    .equals(ConfiguratieService.BRON_ORGANISATIE) ? null : enkelvoudigInformatieObject.getBronorganisatie();
            restEnkelvoudigInformatieobject.creatiedatum = enkelvoudigInformatieObject.getCreatiedatum();
            if (enkelvoudigInformatieObject.getVertrouwelijkheidaanduiding() != null) {
                restEnkelvoudigInformatieobject.vertrouwelijkheidaanduiding = enkelvoudigInformatieObject.getVertrouwelijkheidaanduiding()
                        .toString();
            }
            restEnkelvoudigInformatieobject.auteur = enkelvoudigInformatieObject.getAuteur();
            if (enkelvoudigInformatieObject.getStatus() != null) {
                restEnkelvoudigInformatieobject.status = enkelvoudigInformatieObject.getStatus();
            }
            restEnkelvoudigInformatieobject.formaat = enkelvoudigInformatieObject.getFormaat();

            configuratieService.findTaal(enkelvoudigInformatieObject.getTaal())
                    .ifPresent(taal -> restEnkelvoudigInformatieobject.taal = taal.getNaam());
            restEnkelvoudigInformatieobject.versie = enkelvoudigInformatieObject.getVersie();
            restEnkelvoudigInformatieobject.registratiedatumTijd = enkelvoudigInformatieObject.getBeginRegistratie();
            restEnkelvoudigInformatieobject.bestandsnaam = enkelvoudigInformatieObject.getBestandsnaam();
            if (enkelvoudigInformatieObject.getLink() != null) {
                restEnkelvoudigInformatieobject.link = enkelvoudigInformatieObject.getLink().toString();
            }
            restEnkelvoudigInformatieobject.beschrijving = enkelvoudigInformatieObject.getBeschrijving();
            restEnkelvoudigInformatieobject.ontvangstdatum = enkelvoudigInformatieObject.getOntvangstdatum();
            restEnkelvoudigInformatieobject.verzenddatum = enkelvoudigInformatieObject.getVerzenddatum();
            if (lock != null) {
                restEnkelvoudigInformatieobject.gelockedDoor = restUserConverter.convertUser(
                        identityService.readUser(lock.getUserId()));
            }
            restEnkelvoudigInformatieobject.bestandsomvang = enkelvoudigInformatieObject.getBestandsomvang();
            restEnkelvoudigInformatieobject.informatieobjectTypeOmschrijving = ztcClientService.readInformatieobjecttype(
                    enkelvoudigInformatieObject.getInformatieobjecttype()).getOmschrijving();
            restEnkelvoudigInformatieobject.informatieobjectTypeUUID = enkelvoudigInformatieObject.getInformatieobjectTypeUUID();
            if (enkelvoudigInformatieObject.getOndertekening() != null && enkelvoudigInformatieObject.getOndertekening()
                    .getSoort() != null &&
                    enkelvoudigInformatieObject.getOndertekening().getDatum() != null) {
                restEnkelvoudigInformatieobject.ondertekening =
                        restOndertekeningConverter.convert(enkelvoudigInformatieObject.getOndertekening());
            }
        } else {
            restEnkelvoudigInformatieobject.titel = enkelvoudigInformatieObject.getIdentificatie();
        }
        return restEnkelvoudigInformatieobject;
    }

    public EnkelvoudigInformatieobjectWithInhoud convertZaakObject(
            final RESTEnkelvoudigInformatieobject restEnkelvoudigInformatieobject,
            final RESTFileUpload bestand) {
        final EnkelvoudigInformatieobjectWithInhoud enkelvoudigInformatieobjectWithInhoud = new EnkelvoudigInformatieobjectWithInhoud();
        enkelvoudigInformatieobjectWithInhoud.setBronorganisatie(ConfiguratieService.BRON_ORGANISATIE);
        enkelvoudigInformatieobjectWithInhoud.setCreatiedatum(restEnkelvoudigInformatieobject.creatiedatum);
        enkelvoudigInformatieobjectWithInhoud.setTitel(restEnkelvoudigInformatieobject.titel);
        enkelvoudigInformatieobjectWithInhoud.setAuteur(restEnkelvoudigInformatieobject.auteur);
        enkelvoudigInformatieobjectWithInhoud.setTaal(restEnkelvoudigInformatieobject.taal);
        enkelvoudigInformatieobjectWithInhoud.setInformatieobjecttype(
                ztcClientService.readInformatieobjecttype(restEnkelvoudigInformatieobject.informatieobjectTypeUUID)
                        .getUrl());
        enkelvoudigInformatieobjectWithInhoud.setInhoud(bestand.file);
        enkelvoudigInformatieobjectWithInhoud.setFormaat(bestand.type);
        enkelvoudigInformatieobjectWithInhoud.setBestandsnaam(restEnkelvoudigInformatieobject.bestandsnaam);
        enkelvoudigInformatieobjectWithInhoud.setBeschrijving(restEnkelvoudigInformatieobject.beschrijving);
        enkelvoudigInformatieobjectWithInhoud.setStatus(restEnkelvoudigInformatieobject.status);
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
                ztcClientService.readInformatieobjecttype(restEnkelvoudigInformatieobject.informatieobjectTypeUUID)
                        .getUrl());
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

    public EnkelvoudigInformatieobjectWithInhoud convert(final RESTTaakDocumentData documentData,
            final RESTFileUpload bestand) {
        final EnkelvoudigInformatieobjectWithInhoud enkelvoudigInformatieobjectWithInhoud = new EnkelvoudigInformatieobjectWithInhoud();
        enkelvoudigInformatieobjectWithInhoud.setBronorganisatie(ConfiguratieService.BRON_ORGANISATIE);
        enkelvoudigInformatieobjectWithInhoud.setCreatiedatum(LocalDate.now());
        enkelvoudigInformatieobjectWithInhoud.setTitel(documentData.documentTitel);
        enkelvoudigInformatieobjectWithInhoud.setAuteur(loggedInUserInstance.get().getFullName());
        enkelvoudigInformatieobjectWithInhoud.setTaal(ConfiguratieService.TAAL_NEDERLANDS);
        enkelvoudigInformatieobjectWithInhoud.setInformatieobjecttype(
                ztcClientService.readInformatieobjecttype(documentData.documentType.uuid).getUrl());
        enkelvoudigInformatieobjectWithInhoud.setInhoud(bestand.file);
        enkelvoudigInformatieobjectWithInhoud.setFormaat(bestand.type);
        enkelvoudigInformatieobjectWithInhoud.setBestandsnaam(bestand.filename);
        enkelvoudigInformatieobjectWithInhoud.setStatus(InformatieobjectStatus.DEFINITIEF);
        enkelvoudigInformatieobjectWithInhoud.setVertrouwelijkheidaanduiding(
                Vertrouwelijkheidaanduiding.fromValue(documentData.documentType.vertrouwelijkheidaanduiding));
        return enkelvoudigInformatieobjectWithInhoud;
    }


    public RESTEnkelvoudigInformatieObjectVersieGegevens convertToRESTEnkelvoudigInformatieObjectVersieGegevens(
            final EnkelvoudigInformatieobject informatieobject) {
        final RESTEnkelvoudigInformatieObjectVersieGegevens restEnkelvoudigInformatieObjectVersieGegevens = new RESTEnkelvoudigInformatieObjectVersieGegevens();

        restEnkelvoudigInformatieObjectVersieGegevens.uuid = UriUtil.uuidFromURI(informatieobject.getUrl());

        if (informatieobject.getStatus() != null) {
            restEnkelvoudigInformatieObjectVersieGegevens.status = informatieobject.getStatus();
        }
        if (informatieobject.getVertrouwelijkheidaanduiding() != null) {
            restEnkelvoudigInformatieObjectVersieGegevens.vertrouwelijkheidaanduiding = informatieobject.getVertrouwelijkheidaanduiding()
                    .toValue();
        }

        restEnkelvoudigInformatieObjectVersieGegevens.beschrijving = informatieobject.getBeschrijving();
        restEnkelvoudigInformatieObjectVersieGegevens.verzenddatum = informatieobject.getVerzenddatum();
        restEnkelvoudigInformatieObjectVersieGegevens.ontvangstdatum = informatieobject.getOntvangstdatum();
        restEnkelvoudigInformatieObjectVersieGegevens.titel = informatieobject.getTitel();
        restEnkelvoudigInformatieObjectVersieGegevens.auteur = informatieobject.getAuteur();
        configuratieService.findTaal(informatieobject.getTaal())
                .map(restTaalConverter::convert)
                .ifPresent(taal -> restEnkelvoudigInformatieObjectVersieGegevens.taal = taal);
        restEnkelvoudigInformatieObjectVersieGegevens.bestandsnaam = informatieobject.getInhoud().toString();

        return restEnkelvoudigInformatieObjectVersieGegevens;
    }

    public EnkelvoudigInformatieobjectWithInhoudAndLock convert(
            final RESTEnkelvoudigInformatieObjectVersieGegevens restEnkelvoudigInformatieObjectVersieGegevens, final RESTFileUpload file) {
        final EnkelvoudigInformatieobjectWithInhoudAndLock enkelvoudigInformatieobjectWithInhoudAndLock = new EnkelvoudigInformatieobjectWithInhoudAndLock();

        if (restEnkelvoudigInformatieObjectVersieGegevens.status != null) {
            enkelvoudigInformatieobjectWithInhoudAndLock.setStatus(
                    restEnkelvoudigInformatieObjectVersieGegevens.status);
        }
        if (restEnkelvoudigInformatieObjectVersieGegevens.vertrouwelijkheidaanduiding != null) {
            enkelvoudigInformatieobjectWithInhoudAndLock.setVertrouwelijkheidaanduiding(
                    Vertrouwelijkheidaanduiding.valueOf(
                            restEnkelvoudigInformatieObjectVersieGegevens.vertrouwelijkheidaanduiding));
        }
        if (restEnkelvoudigInformatieObjectVersieGegevens.beschrijving != null) {
            enkelvoudigInformatieobjectWithInhoudAndLock.setBeschrijving(
                    restEnkelvoudigInformatieObjectVersieGegevens.beschrijving);
        }
        if (restEnkelvoudigInformatieObjectVersieGegevens.verzenddatum != null) {
            enkelvoudigInformatieobjectWithInhoudAndLock.setVerzenddatum(
                    restEnkelvoudigInformatieObjectVersieGegevens.verzenddatum);
        }
        if (restEnkelvoudigInformatieObjectVersieGegevens.ontvangstdatum != null) {
            enkelvoudigInformatieobjectWithInhoudAndLock.setOntvangstdatum(
                    restEnkelvoudigInformatieObjectVersieGegevens.ontvangstdatum);
        }
        if (restEnkelvoudigInformatieObjectVersieGegevens.titel != null) {
            enkelvoudigInformatieobjectWithInhoudAndLock.setTitel(restEnkelvoudigInformatieObjectVersieGegevens.titel);
        }
        if (restEnkelvoudigInformatieObjectVersieGegevens.taal != null) {
            enkelvoudigInformatieobjectWithInhoudAndLock.setTaal(
                    restEnkelvoudigInformatieObjectVersieGegevens.taal.code);
        }
        if (restEnkelvoudigInformatieObjectVersieGegevens.auteur != null) {
            enkelvoudigInformatieobjectWithInhoudAndLock.setAuteur(
                    restEnkelvoudigInformatieObjectVersieGegevens.auteur);
        }
        if (restEnkelvoudigInformatieObjectVersieGegevens.bestandsnaam != null) {
            enkelvoudigInformatieobjectWithInhoudAndLock.setBestandsnaam(
                    (restEnkelvoudigInformatieObjectVersieGegevens.bestandsnaam));
        }
        if (file != null && file.file != null) {
            enkelvoudigInformatieobjectWithInhoudAndLock.setInhoud(file.file);
            enkelvoudigInformatieobjectWithInhoudAndLock.setBestandsomvang((long) file.file.length);
            enkelvoudigInformatieobjectWithInhoudAndLock.setFormaat(file.type);
        }

        return enkelvoudigInformatieobjectWithInhoudAndLock;
    }

    public List<RESTEnkelvoudigInformatieobject> convertUUIDsToREST(final List<UUID> enkelvoudigInformatieobjectUUIDs,
            final Zaak zaak) {
        return enkelvoudigInformatieobjectUUIDs.stream()
                .map(enkelvoudigInformatieobjectUUID -> convertToREST(
                        drcClientService.readEnkelvoudigInformatieobject(enkelvoudigInformatieobjectUUID), zaak))
                .toList();
    }

    public RESTGekoppeldeZaakEnkelvoudigInformatieObject convertToREST(
            final ZaakInformatieobject zaakInformatieObject, final RelatieType relatieType, final Zaak zaak) {
        final EnkelvoudigInformatieobject enkelvoudigInformatieObject = drcClientService.readEnkelvoudigInformatieobject(
                zaakInformatieObject.getInformatieobject());
        final EnkelvoudigInformatieObjectLock lock = enkelvoudigInformatieObject.getLocked() ?
                enkelvoudigInformatieObjectLockService.findLock(enkelvoudigInformatieObject.getUUID()).orElse(null)
                : null;
        final DocumentRechten rechten = policyService.readDocumentRechten(enkelvoudigInformatieObject, lock, zaak);
        final RESTGekoppeldeZaakEnkelvoudigInformatieObject restEnkelvoudigInformatieobject = new RESTGekoppeldeZaakEnkelvoudigInformatieObject();
        restEnkelvoudigInformatieobject.uuid = enkelvoudigInformatieObject.getUUID();
        restEnkelvoudigInformatieobject.identificatie = enkelvoudigInformatieObject.getIdentificatie();
        restEnkelvoudigInformatieobject.rechten = rechtenConverter.convert(rechten);
        if (rechten.getLezen()) {
            restEnkelvoudigInformatieobject.titel = enkelvoudigInformatieObject.getTitel();
            restEnkelvoudigInformatieobject.bronorganisatie = enkelvoudigInformatieObject.getBronorganisatie()
                    .equals(ConfiguratieService.BRON_ORGANISATIE) ? null : enkelvoudigInformatieObject.getBronorganisatie();
            restEnkelvoudigInformatieobject.creatiedatum = enkelvoudigInformatieObject.getCreatiedatum();
            if (enkelvoudigInformatieObject.getVertrouwelijkheidaanduiding() != null) {
                restEnkelvoudigInformatieobject.vertrouwelijkheidaanduiding = enkelvoudigInformatieObject.getVertrouwelijkheidaanduiding()
                        .toString();
            }
            restEnkelvoudigInformatieobject.auteur = enkelvoudigInformatieObject.getAuteur();
            if (enkelvoudigInformatieObject.getStatus() != null) {
                restEnkelvoudigInformatieobject.status = enkelvoudigInformatieObject.getStatus();
            }
            restEnkelvoudigInformatieobject.formaat = enkelvoudigInformatieObject.getFormaat();
            configuratieService.findTaal(enkelvoudigInformatieObject.getTaal())
                    .ifPresent(taal -> restEnkelvoudigInformatieobject.taal = taal.getName());
            restEnkelvoudigInformatieobject.versie = enkelvoudigInformatieObject.getVersie();
            restEnkelvoudigInformatieobject.registratiedatumTijd = enkelvoudigInformatieObject.getBeginRegistratie();
            restEnkelvoudigInformatieobject.bestandsnaam = enkelvoudigInformatieObject.getBestandsnaam();
            if (enkelvoudigInformatieObject.getLink() != null) {
                restEnkelvoudigInformatieobject.link = enkelvoudigInformatieObject.getLink().toString();
            }
            restEnkelvoudigInformatieobject.beschrijving = enkelvoudigInformatieObject.getBeschrijving();
            restEnkelvoudigInformatieobject.ontvangstdatum = enkelvoudigInformatieObject.getOntvangstdatum();
            restEnkelvoudigInformatieobject.verzenddatum = enkelvoudigInformatieObject.getVerzenddatum();
            if (lock != null) {
                restEnkelvoudigInformatieobject.gelockedDoor = restUserConverter.convertUser(
                        identityService.readUser(lock.getUserId()));
            }
            restEnkelvoudigInformatieobject.bestandsomvang = enkelvoudigInformatieObject.getBestandsomvang();
            restEnkelvoudigInformatieobject.informatieobjectTypeOmschrijving = ztcClientService.readInformatieobjecttype(
                    enkelvoudigInformatieObject.getInformatieobjecttype()).getOmschrijving();
            restEnkelvoudigInformatieobject.informatieobjectTypeUUID = enkelvoudigInformatieObject.getInformatieobjectTypeUUID();
            restEnkelvoudigInformatieobject.relatieType = relatieType;
            restEnkelvoudigInformatieobject.zaakIdentificatie = zaak.getIdentificatie();
            restEnkelvoudigInformatieobject.zaakUUID = zaak.getUuid();
        } else {
            restEnkelvoudigInformatieobject.titel = enkelvoudigInformatieObject.getIdentificatie();
        }
        return restEnkelvoudigInformatieobject;
    }

    public List<RESTEnkelvoudigInformatieobject> convertInformatieobjectenToREST(
            final List<EnkelvoudigInformatieobject> informatieobjecten) {
        return informatieobjecten.stream().map(this::convertToREST).toList();
    }
}
