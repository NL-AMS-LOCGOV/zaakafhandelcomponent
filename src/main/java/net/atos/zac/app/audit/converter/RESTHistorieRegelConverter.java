/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.audit.converter;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import net.atos.client.zgw.shared.model.audit.AuditTrailRegel;
import net.atos.client.zgw.shared.model.audit.AuditWijziging;
import net.atos.zac.app.audit.model.RESTHistorieRegel;

public class RESTHistorieRegelConverter {

    @Inject
    @Any
    private Instance<AbstractAuditWijzigingConverter<? extends AuditWijziging<?>>> wijzigingConverterInstance;

    public List<RESTHistorieRegel> convert(final List<AuditTrailRegel> auditTrail) {
        return auditTrail.stream()
                .sorted(Comparator.comparing(AuditTrailRegel::getAanmaakdatum).reversed())
                .flatMap(this::convert)
                .collect(Collectors.toList());
    }

    private Stream<RESTHistorieRegel> convert(final AuditTrailRegel auditTrailRegel) {
        return convertWijziging(auditTrailRegel.getWijzigingen())
                .peek(historieRegel -> convertAuditTrailBasis(historieRegel, auditTrailRegel));
    }

    private Stream<RESTHistorieRegel> convertWijziging(final AuditWijziging<?> wijziging) {
        for (AbstractAuditWijzigingConverter<?> wijzigingConverter : wijzigingConverterInstance) {
            if (wijzigingConverter.supports(wijziging.getObjectType())) {
                return wijzigingConverter.convert(wijziging);
            }
        }
        return Stream.empty();
    }

    private void convertAuditTrailBasis(final RESTHistorieRegel historieRegel, final AuditTrailRegel auditTrailRegel) {
        historieRegel.datumTijd = auditTrailRegel.getAanmaakdatum();
        historieRegel.door = auditTrailRegel.getGebruikersWeergave();
        historieRegel.applicatie = auditTrailRegel.getApplicatieWeergave();
        historieRegel.toelichting = auditTrailRegel.getToelichting();
    }
}
