/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.audit.converter;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.lang3.NotImplementedException;

import net.atos.client.zgw.shared.model.audit.AuditTrailRegel;
import net.atos.client.zgw.shared.model.audit.AuditWijziging;
import net.atos.zac.app.audit.model.RESTAuditTrailRegel;
import net.atos.zac.app.audit.model.RESTWijziging;
import net.atos.zac.util.UriUtil;

public class RESTAuditTrailConverter {

    @Inject
    @Any
    private Instance<AbstractRESTAuditWijzigingConverter<? extends AuditWijziging<?>>> wijzigingConverterInstance;

    public List<RESTAuditTrailRegel> convert(final List<AuditTrailRegel> auditTrail) {
        return auditTrail.stream()
                .sorted(Comparator.comparing(AuditTrailRegel::getAanmaakdatum).reversed())
                .map(this::convert).collect(Collectors.toList());
    }

    private RESTAuditTrailRegel convert(final AuditTrailRegel auditTrailRegel) {
        final RESTAuditTrailRegel rest = new RESTAuditTrailRegel();
        rest.uuid = auditTrailRegel.getUuid();
        rest.applicatieId = auditTrailRegel.getApplicatieId();
        rest.applicatieWeergave = auditTrailRegel.getApplicatieWeergave();
        rest.gebruikersId = auditTrailRegel.getGebruikersId();
        rest.gebruikersWeergave = auditTrailRegel.getGebruikersWeergave();
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
        for (AbstractRESTAuditWijzigingConverter<?> wijzigingConverter : wijzigingConverterInstance) {
            if (wijzigingConverter.supports(wijziging.getObjectType())) {
                return wijzigingConverter.convert(wijziging);
            }
        }
        throw new NotImplementedException("No converter supports: " + wijziging.getObjectType());
    }

}
