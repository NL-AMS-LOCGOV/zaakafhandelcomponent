/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared.cache.event;

import static net.atos.zac.event.OpcodeEnum.UPDATED;

import java.net.URI;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import net.atos.client.zgw.shared.util.URIUtil;
import net.atos.zac.event.AbstractUpdateEvent;

public class CacheUpdateEvent extends AbstractUpdateEvent<CacheObjectTypeEnum, URI> {

    private static final long serialVersionUID = -329301003012599689L;

    @NotNull
    private CacheObjectTypeEnum objectType;

    private volatile UUID uuid = null;

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
    public CacheUpdateEvent(final CacheObjectTypeEnum objectType, final URI objectId) {
        super(UPDATED, objectId);
        this.objectType = objectType;
    }

    @Override
    public CacheObjectTypeEnum getObjectType() {
        return objectType;
    }

    public URI getUrl() {
        return getObjectId();
    }

    public UUID getUUID() {
        if (uuid == null) {
            uuid = URIUtil.parseUUIDFromResourceURI(getObjectId());
        }
        return uuid;
    }
}
