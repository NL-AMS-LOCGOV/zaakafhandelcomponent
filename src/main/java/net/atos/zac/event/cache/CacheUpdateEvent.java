/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.event.cache;

import static net.atos.zac.event.OpcodeEnum.UPDATED;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import net.atos.zac.event.AbstractUpdateEvent;

// TODO Verplaatsen naar waar dit gebruikt worden .../event
public class CacheUpdateEvent extends AbstractUpdateEvent<CacheObjectTypeEnum, UUID> {

    private static final long serialVersionUID = -329301003012599689L;

    @NotNull
    private CacheObjectTypeEnum objectType;

    /**
     * Constructor for the sake of JAXB
     */
    public CacheUpdateEvent() {
        super();
    }

    /**
     * Constructor die alle verplichte velden bevat.
     *
     * @param objectType het type object waarop de operatie is uitgevoerd
     * @param objectId   de identificatie van het object waarop een operatie is uitgevoerd
     */
    public CacheUpdateEvent(final CacheObjectTypeEnum objectType, final UUID objectId) {
        super(UPDATED, objectId);
        this.objectType = objectType;
    }

    @Override
    public CacheObjectTypeEnum getObjectType() {
        return objectType;
    }
}
