/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable.cmmn.event;

import static net.atos.zac.event.OpcodeEnum.CREATE;

import java.net.URI;

/**
 * Enumeratie die de soorten object wijzigingen bevat zoals die gebruikt worden door het {@link CmmnUpdateEvent}.
 *
 * Dat is er op dit moment maar een dus zo eenvoudig mogelijk (maar wel met eenzelfde syntax als de andere observers).
 */
public enum CmmnObjectTypeEnum {
    ZAAK;

    public final CmmnUpdateEvent creation(final URI url) {
        return new CmmnUpdateEvent(CREATE, this, url);
    }
}
