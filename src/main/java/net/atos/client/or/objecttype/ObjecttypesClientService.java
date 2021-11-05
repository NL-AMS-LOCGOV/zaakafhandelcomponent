/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.or.objecttype;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import net.atos.client.or.objecttype.model.Objecttype;
import net.atos.client.or.objecttype.model.ObjecttypeVersion;

@ApplicationScoped
public class ObjecttypesClientService {

    @Inject
    @RestClient
    private ObjecttypesClient objecttypesClient;

    public List<Objecttype> getObjecttypes() {
        return objecttypesClient.objecttypeList();
    }

    public List<ObjecttypeVersion> getObjecttypeVersions(final UUID objecttypeUUID) {
        return objecttypesClient.objectversionList(objecttypeUUID);
    }
}
