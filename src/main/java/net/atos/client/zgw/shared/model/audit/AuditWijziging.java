/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared.model.audit;

import javax.json.bind.annotation.JsonbTypeDeserializer;

import net.atos.client.zgw.shared.model.ObjectType;
import net.atos.client.zgw.shared.util.AuditWijzigingJsonbDeserializer;

@JsonbTypeDeserializer(AuditWijzigingJsonbDeserializer.class)
public abstract class AuditWijziging<OBJECT> {
    private OBJECT oud;

    private OBJECT nieuw;

    public OBJECT getOud() {
        return oud;
    }

    public void setOud(final OBJECT oud) {
        this.oud = oud;
    }

    public OBJECT getNieuw() {
        return nieuw;
    }

    public void setNieuw(final OBJECT nieuw) {
        this.nieuw = nieuw;
    }

    public abstract ObjectType getObjectType();

}
