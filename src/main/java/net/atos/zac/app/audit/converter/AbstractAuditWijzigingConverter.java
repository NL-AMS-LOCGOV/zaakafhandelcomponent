/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.audit.converter;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

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

    protected void checkAttribuut(final String label, final String oud, final String nieuw, final List<RESTHistorieRegel> historieRegels) {
        if (!StringUtils.equals(oud, nieuw)) {
            historieRegels.add(new RESTHistorieRegel(label, oud, nieuw));
        }
    }

    protected void checkAttribuut(final String label, final AbstractEnum<?> oud, final AbstractEnum<?> nieuw, final List<RESTHistorieRegel> historieRegels) {
        if (oud != nieuw) {
            historieRegels.add(new RESTHistorieRegel(label, oud, nieuw));
        }
    }

    protected void checkAttribuut(final String label, final Boolean oud, final Boolean nieuw, final List<RESTHistorieRegel> historieRegels) {
        if (ObjectUtils.notEqual(oud, nieuw)) {
            historieRegels.add(new RESTHistorieRegel(label, oud, nieuw));
        }
    }

    protected void checkAttribuut(final String label, final LocalDate oud, final LocalDate nieuw, final List<RESTHistorieRegel> historieRegels) {
        if (ObjectUtils.notEqual(oud, nieuw)) {
            historieRegels.add(new RESTHistorieRegel(label, oud, nieuw));
        }
    }

    protected void checkAttribuut(final String label, final ZonedDateTime oud, final ZonedDateTime nieuw, final List<RESTHistorieRegel> historieRegels) {
        if (ObjectUtils.notEqual(oud, nieuw)) {
            historieRegels.add(new RESTHistorieRegel(label, oud, nieuw));
        }
    }
}
