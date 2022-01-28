/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.audit.converter;

import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.ObjectUtils;

import net.atos.client.zgw.shared.model.AbstractEnum;
import net.atos.client.zgw.shared.model.ObjectType;
import net.atos.client.zgw.shared.model.audit.AuditWijziging;
import net.atos.zac.app.audit.model.RESTHistorieRegel;

public abstract class AbstractAuditWijzigingConverter<W extends AuditWijziging<?>> {

    public Stream<RESTHistorieRegel> convert(final AuditWijziging<?> wijziging) {
        return doConvert((W) wijziging);
    }

    public abstract boolean supports(final ObjectType objectType);

    protected abstract Stream<RESTHistorieRegel> doConvert(final W wijziging);

    protected void checkWaarden(final String attribuutLabel, final Object waardeOud, final Object waardeNieuw, final List<RESTHistorieRegel> historieRegels) {
        if (ObjectUtils.notEqual(waardeOud, waardeNieuw)) {
            historieRegels.add(new RESTHistorieRegel(attribuutLabel, waardeOud, waardeNieuw));
        }
    }

    protected String enumToWaarde(final AbstractEnum<?> abstractEnum) {
        return abstractEnum != null ? abstractEnum.toValue() : null;
    }

    protected String booleanToWaarde(final Boolean bool) {
        return bool != null ? (bool ? "Ja" : "Nee") : null;
    }
}
