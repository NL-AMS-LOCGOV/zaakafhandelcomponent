/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.audit.converter;

import java.util.Objects;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.shared.model.audit.AuditTrailRegel;
import net.atos.client.zgw.shared.model.audit.AuditWijziging;
import net.atos.client.zgw.shared.model.audit.EnkelvoudigInformatieobjectWijziging;
import net.atos.client.zgw.shared.model.audit.ResultaatWijziging;
import net.atos.client.zgw.shared.model.audit.StatusWijziging;
import net.atos.client.zgw.shared.model.audit.ZaakInformatieobjectWijziging;
import net.atos.client.zgw.shared.model.audit.ZaakWijziging;
import net.atos.client.zgw.zrc.model.Resultaat;
import net.atos.client.zgw.zrc.model.Rol;
import net.atos.client.zgw.zrc.model.Status;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.ZaakInformatieobject;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Resultaattype;
import net.atos.client.zgw.ztc.model.Statustype;
import net.atos.zac.app.audit.model.RESTAuditTrailRegel;
import net.atos.zac.app.audit.model.RESTWijziging;
import net.atos.zac.util.UriUtil;

public class RESTAuditTrailRegelConverter {

    @Inject
    private ZTCClientService ztcClientService;

    public RESTAuditTrailRegel convert(final AuditTrailRegel auditTrailRegel) {
        final RESTAuditTrailRegel rest = new RESTAuditTrailRegel();
        rest.uuid = auditTrailRegel.getUuid();
        rest.applicatieId = auditTrailRegel.getApplicatieId();
        rest.applicatieWeergave = auditTrailRegel.getApplicatieWeergave();
        rest.gebruikersId = auditTrailRegel.getGebruikersId();
        rest.actie = auditTrailRegel.getActie();
        rest.actieWeergave = auditTrailRegel.getActieWeergave();
        rest.httpStatusCode = auditTrailRegel.getResultaat();
        rest.resource = auditTrailRegel.getResource();
        rest.resourceID = UriUtil.uuidFromURI(auditTrailRegel.getResourceUrl());
        rest.toelichting = auditTrailRegel.getToelichting();
        rest.wijzigingsDatumTijd = auditTrailRegel.getAanmaakdatum();
        rest.wijziging = convertWijziging(auditTrailRegel);
        return rest;
    }


    private RESTWijziging convertWijziging(AuditTrailRegel regel) {
        final AuditWijziging<?> wijziging = regel.getWijzigingen();
        switch (wijziging.getObjectType()) {
            case ZAAK:
                return convertZaakWijziging((ZaakWijziging) wijziging);
            case RESULTAAT:
                return convertResultaatWijziging((ResultaatWijziging) wijziging);
            case STATUS:
                return convertStatusWijziging((StatusWijziging) wijziging);
            case ROL:
                return convertRolWijziging(wijziging);
            case ENKELVOUDIG_INFORMATIEOBJECT:
                return convertEnkelvoudigInformatieobjectWijziging((EnkelvoudigInformatieobjectWijziging) wijziging);
            case ZAAK_INFORMATIEOBJECT:
                return convertZaakInformatieobjectWijziging((ZaakInformatieobjectWijziging) wijziging);
            default:
                throw new IllegalStateException("Unexpected value: " + wijziging.getObjectType());
        }
    }

    private RESTWijziging convertZaakInformatieobjectWijziging(final ZaakInformatieobjectWijziging wijziging) {
        final ZaakInformatieobject nieuw = wijziging.getNieuw();
        final ZaakInformatieobject oud = wijziging.getOud();

        if (oud == null) {
            return new RESTWijziging(String.format("Document '%s' is toegevoegd aan zaak", nieuw.getTitel()));
        }
        if (nieuw == null) {
            return new RESTWijziging(String.format("Document '%s' is verwijderd van zaak", oud.getTitel()));
        }
        return new RESTWijziging(String.format("Document '%s' is gewijzigd", nieuw.getTitel()));
    }

    private RESTWijziging convertEnkelvoudigInformatieobjectWijziging(final EnkelvoudigInformatieobjectWijziging wijziging) {
        final EnkelvoudigInformatieobject nieuw = wijziging.getNieuw();
        final EnkelvoudigInformatieobject oud = wijziging.getOud();

        if (oud == null) {
            return new RESTWijziging(String.format("Document '%s' is toegevoegd", nieuw.getIdentificatie()));
        }
        if (nieuw == null) {
            return new RESTWijziging(String.format("Document '%s' is verwijderd", oud.getIdentificatie()));
        }

        if (!Objects.equals(nieuw.getVersie(), oud.getVersie())) {
            return new RESTWijziging("Versie", Integer.toString(oud.getVersie()), Integer.toString(nieuw.getVersie()));
        }
        if (!StringUtils.equals(nieuw.getBeschrijving(), oud.getBeschrijving())) {
            return new RESTWijziging("Beschrijving", oud.getBeschrijving(), nieuw.getBeschrijving());
        }

        return new RESTWijziging("Onbekend wijziging", "-", "-");
    }

    private RESTWijziging convertRolWijziging(final AuditWijziging<?> rolWijziging) {
        final Rol<?> oud = (Rol<?>) rolWijziging.getOud();
        final Rol<?> nieuw = (Rol<?>) rolWijziging.getNieuw();

        if (oud == null) {
            return new RESTWijziging(String.format("%s '%s' is toegevoegd", nieuw.getBetrokkeneType().toValue(), nieuw.getNaam()));
        }
        if (nieuw == null) {
            return new RESTWijziging(String.format("%s '%s' is verwijderd", oud.getBetrokkeneType().toValue(), oud.getNaam()));
        }

        return new RESTWijziging(nieuw.getBetrokkeneType().toValue(), oud.getNaam(), nieuw.getNaam());
    }


    private RESTWijziging convertZaakWijziging(final ZaakWijziging zaakWijziging) {
        final Zaak nieuw = zaakWijziging.getNieuw();
        final Zaak oud = zaakWijziging.getOud();
        if (oud == null) {
            return new RESTWijziging(String.format("Zaak '%s' is toegevoegd", nieuw.getIdentificatie()));
        }
        if (nieuw == null) {
            return new RESTWijziging(String.format("Zaak '%s' is verwijderd", oud.getIdentificatie()));
        }

        if (!StringUtils.equals(nieuw.getToelichting(), oud.getToelichting())) {
            return new RESTWijziging("Toelichting", oud.getToelichting(), nieuw.getToelichting());
        }
        if (!StringUtils.equals(nieuw.getOmschrijving(), oud.getOmschrijving())) {
            return new RESTWijziging("Omschrijving", oud.getOmschrijving(), nieuw.getOmschrijving());
        }
        return new RESTWijziging("Onbekend", "-", "-");
    }

    private RESTWijziging convertStatusWijziging(final StatusWijziging statusWijziging) {
        final Status nieuw = statusWijziging.getNieuw();
        final Status oud = statusWijziging.getOud();
        if (oud == null) {
            final Statustype statustype = ztcClientService.readStatustype(nieuw.getStatustype());
            return new RESTWijziging("Status", "", statustype.getOmschrijving());
        }
        if (nieuw == null) {
            final Statustype statustype = ztcClientService.readStatustype(oud.getStatustype());
            return new RESTWijziging("Status", statustype.getOmschrijving(), "");
        }

        final Statustype statustypeOud = ztcClientService.readStatustype(oud.getStatustype());
        final Statustype statustypeNieuw = ztcClientService.readStatustype(nieuw.getStatustype());
        return new RESTWijziging("Status", statustypeOud.getOmschrijving(), statustypeNieuw.getOmschrijving());
    }

    private RESTWijziging convertResultaatWijziging(final ResultaatWijziging resultaatWijziging) {
        final Resultaat nieuw = resultaatWijziging.getNieuw();
        final Resultaat oud = resultaatWijziging.getOud();
        if (oud == null) {
            Resultaattype resultaattype = ztcClientService.readResultaattype(nieuw.getResultaattype());
            return new RESTWijziging("Resultaat", "", resultaattype.getOmschrijving());
        }
        if (nieuw == null) {
            Resultaattype resultaattype = ztcClientService.readResultaattype(oud.getResultaattype());
            return new RESTWijziging("Resultaat", resultaattype.getOmschrijving(), "");
        }

        final Resultaattype resultaattypeOud = ztcClientService.readResultaattype(oud.getResultaattype());
        final Resultaattype resultaattypeNieuw = ztcClientService.readResultaattype(nieuw.getResultaattype());
        return new RESTWijziging("Reslutaat", resultaattypeOud.getOmschrijving(), resultaattypeNieuw.getOmschrijving());
    }
}
