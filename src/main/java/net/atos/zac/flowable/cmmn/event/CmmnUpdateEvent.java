/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable.cmmn.event;

import static net.atos.zac.event.OperatieEnum.TOEVOEGING;

import java.net.URI;

import net.atos.zac.event.AbstractUpdateEvent;

public class CmmnUpdateEvent extends AbstractUpdateEvent<Object, URI> {

    private static final long serialVersionUID = -2708089744504467450L;

    /**
     * Constructor for the sake of JAXB
     */
    public CmmnUpdateEvent() {
        super();
    }

    public CmmnUpdateEvent(final URI objectId) {
        super(TOEVOEGING, objectId);
    }

    @Override
    public Object getObjectType() {
        return null;
    }
}
