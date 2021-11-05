/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.or.shared;

import static java.lang.String.format;
import static net.atos.client.or.objecttype.model.Status.PUBLISHED;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

import java.time.LocalDate;
import java.util.Comparator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import net.atos.client.or.object.ObjectsClientService;
import net.atos.client.or.object.model.ORObject;
import net.atos.client.or.object.model.ObjectRecord;
import net.atos.client.or.objecttype.ObjecttypesClientService;
import net.atos.client.or.objecttype.model.Objecttype;
import net.atos.client.or.objecttype.model.ObjecttypeVersion;

@ApplicationScoped
public class ObjectRegistratieClientService {

    @Inject
    private ObjectsClientService objectsClientService;

    @Inject
    private ObjecttypesClientService objecttypesClientService;

    /**
     * Create an Object of a specific type.
     *
     * @param objecttypeNaam The name of the objecttype
     * @param data           Data stored in the ObjectRecord of the newly created Object
     * @return New Object
     */
    public ORObject createObject(final String objecttypeNaam, final Object data) {
        // Search Objecttype
        final Objecttype objecttype = objecttypesClientService.getObjecttypes().stream()
                .filter(_objecttype -> equalsIgnoreCase(_objecttype.getName(), objecttypeNaam))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(format("Objecttype with name '%s' not found", objecttypeNaam)));

        // Get latest ObjecttypeVersion
        final ObjecttypeVersion objecttypeVersion = objecttypesClientService.getObjecttypeVersions(objecttype.getUuid()).stream()
                .filter(_objecttypeVersion -> _objecttypeVersion.getStatus() == PUBLISHED)
                .max(Comparator.comparing(ObjecttypeVersion::getVersion))
                .orElseThrow(() -> new RuntimeException(format("No ObjecttypeVersion found for Objecttype with UUID: '%s'", objecttype.getUuid().toString())));

        // Create ObjectRecord
        final ObjectRecord record = new ObjectRecord();
        record.setTypeVersion(objecttypeVersion.getVersion());
        record.setStartAt(LocalDate.now());
        record.setData(data);

        // create Object
        final ORObject object = new ORObject();
        object.setType(objecttype.getUrl());
        object.setRecord(record);

        return objectsClientService.createObject(object);
    }
}
