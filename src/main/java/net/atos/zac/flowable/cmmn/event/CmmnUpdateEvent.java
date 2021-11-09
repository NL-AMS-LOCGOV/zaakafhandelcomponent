/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable.cmmn.event;


import java.net.URI;

import javax.validation.constraints.NotNull;

import net.atos.zac.event.AbstractUpdateEvent;
import net.atos.zac.event.OpcodeEnum;

public class CmmnUpdateEvent extends AbstractUpdateEvent<CmmnObjectTypeEnum, URI> {

    private static final long serialVersionUID = 4764736142892883842L;

    @NotNull
    private CmmnObjectTypeEnum objectType;

    /**
     * Constructor for the sake of JAXB
     */
    public CmmnUpdateEvent() {
        super();
    }

    /**
     * Constructor die alle verplichte velden bevat.
     *
     * @param operatie   de operatie die uitgevoerd is op het betreffende object
     * @param objectType het type object waarop de operatie is uitgevoerd
     * @param objectId   de identificatie van het object waarop een operatie is uitgevoerd
     */
    public CmmnUpdateEvent(final OpcodeEnum operatie, final CmmnObjectTypeEnum objectType, final URI objectId) {
        super(operatie, objectId);
        this.objectType = objectType;
    }

    @Override
    public CmmnObjectTypeEnum getObjectType() {
        return objectType;
    }
}
