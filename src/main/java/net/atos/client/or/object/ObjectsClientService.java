/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.or.object;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static net.atos.client.util.Constant.APPLICATION_PROBLEM_JSON;

import java.net.URI;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.Invocation;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import net.atos.client.or.object.model.ORObject;
import net.atos.client.util.ClientFactory;

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
     * Read {@link ORObject} via its URI.
     * Throws a RuntimeException if the {@link ORObject} can not be read.
     *
     * @param objectURI URI of {@link ORObject}.
     * @return {@link ORObject}. Never 'null'!
     */
    public ORObject readObject(final URI objectURI) {
        return createInvocationBuilder(objectURI).get(ORObject.class);
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

    private Invocation.Builder createInvocationBuilder(final URI uri) {
        return ClientFactory.create().target(uri)
                .request(APPLICATION_JSON, APPLICATION_PROBLEM_JSON)
                .header(AUTHORIZATION, ObjectsClientHeadersFactory.generateToken())
                .header(ObjectsClient.ACCEPT_CRS, ObjectsClient.ACCEPT_CRS_VALUE)
                .header(ObjectsClient.CONTENT_CRS, ObjectsClient.ACCEPT_CRS_VALUE);
    }
}
