/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.or.object;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import net.atos.client.or.object.model.ORObject;

@ApplicationScoped
public class ObjectsClientService {

    @Inject
    @RestClient
    private ObjectsClient objectsClient;

    /**
     * Create {@link ORObject}.
     *
     * @param object {@link ORObject}.
     * @return Created {@link ORObject}.
     */
    public ORObject createObject(final ORObject object) {
        return objectsClient.objectCreate(object);
    }

    /**
     * Read {@link ORObject} via its UUID.
     * Throws a RuntimeException if the {@link ORObject} can not be read.
     *
     * @param object UUID of {@link ORObject}.
     * @return {@link ORObject}. Never 'null'!
     */
    public ORObject readObject(final UUID object) {
        return objectsClient.objectRead(object);
    }

    /**
     * Update {@link ORObject}.
     * The given instance completely replaces the existing instance.
     *
     * @param object {@link ORObject}.
     * @return Updated {@link ORObject}.
     */
    public ORObject updateObject(final ORObject object) {
        return objectsClient.objectUpdate(object.getUuid(), object);
    }
}
