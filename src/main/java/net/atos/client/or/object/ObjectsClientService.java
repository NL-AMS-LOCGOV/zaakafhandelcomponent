/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.or.object;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static net.atos.client.or.shared.util.Constant.APPLICATION_PROBLEM_JSON;

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

    public ORObject getObject(final URI objectURI) {
        return createInvocationBuilder(objectURI).get(ORObject.class);
    }

    public ORObject createObject(final ORObject object) {
        return objectsClient.objectCreate(object);
    }

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
