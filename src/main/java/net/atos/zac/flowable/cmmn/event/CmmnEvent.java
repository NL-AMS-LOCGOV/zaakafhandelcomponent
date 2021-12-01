/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable.cmmn.event;


import java.net.URI;

import net.atos.zac.event.AbstractEvent;
import net.atos.zac.event.Opcode;

public class CmmnEvent extends AbstractEvent<CmmnEventType, URI> {

    private static final long serialVersionUID = 4764736142892883842L;

    private CmmnEventType objectType;

    /**
     * Constructor for the sake of JAXB
     */
    public CmmnEvent() {
        super();
    }

    /**
     * Constructor die alle verplichte velden bevat.
     *
     * @param operation   de operatie die uitgevoerd is op het betreffende object
     * @param objectType het type object waarop de operatie is uitgevoerd
     * @param objectId   de identificatie van het object waarop een operatie is uitgevoerd
     */
    public CmmnEvent(final Opcode operation, final CmmnEventType objectType, final URI objectId) {
        super(operation, objectId);
        this.objectType = objectType;
    }

    @Override
    public CmmnEventType getObjectType() {
        return objectType;
    }
}
