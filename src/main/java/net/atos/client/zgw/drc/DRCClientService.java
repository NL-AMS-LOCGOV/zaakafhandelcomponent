/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.drc;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import net.atos.client.util.ClientFactory;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobjectWithInhoud;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobjectWithInhoudAndLock;
import net.atos.client.zgw.drc.model.Gebruiksrechten;
import net.atos.client.zgw.drc.model.Lock;
import net.atos.client.zgw.shared.model.audit.AuditTrailRegel;
import net.atos.client.zgw.shared.util.ZGWClientHeadersFactory;

/**
 *
 */
@ApplicationScoped
public class DRCClientService {

    @Inject
    @RestClient
    private DRCClient drcClient;

    @Inject
    private ZGWClientHeadersFactory zgwClientHeadersFactory;

    /*
     * In memory persistance map of locks.
     * key: Combination of EnkelvoudigInformatieobject UUID and owner of the lock.
     * value: the lock.
     *
     * ToDo: ESUITEDEV-25841
     */
    private static final Map<String, String> LOCKS = new HashMap<>();

    /**
     * Read {@link EnkelvoudigInformatieobject} via its UUID.
     * Throws a RuntimeException if the {@link EnkelvoudigInformatieobject} can not be read.
     *
     * @param uuid UUID of the {@link EnkelvoudigInformatieobject}.
     * @return {@link EnkelvoudigInformatieobject}. Never 'null'!
     */
    public EnkelvoudigInformatieobject readEnkelvoudigInformatieobject(final UUID uuid) {
        return drcClient.enkelvoudigInformatieobjectRead(uuid);
    }


    /**
     * Read {@link EnkelvoudigInformatieobject} via its UUID and version.
     * Throws a RuntimeException if the {@link EnkelvoudigInformatieobject} can not be read.
     *
     * @param uuid   UUID of the {@link EnkelvoudigInformatieobject}.
     * @param versie Required version
     * @return {@link EnkelvoudigInformatieobject}. Never 'null'!
     */
    public EnkelvoudigInformatieobject readEnkelvoudigInformatieobjectVersie(final UUID uuid, final int versie) {
        return drcClient.enkelvoudigInformatieobjectReadVersie(uuid, versie);
    }


    /**
     * DELETE {@link EnkelvoudigInformatieobject} via its UUID.
     * Throws a RuntimeException if the {@link EnkelvoudigInformatieobject} can not be deleted.
     *
     * @param uuid UUID of the {@link EnkelvoudigInformatieobject}.
     */
    public void deleteEnkelvoudigInformatieobject(final UUID uuid) {
        drcClient.enkelvoudigInformatieobjectDelete(uuid);
    }

    /**
     * Read {@link EnkelvoudigInformatieobject} via its URI.
     * Throws a RuntimeException if the {@link EnkelvoudigInformatieobject} can not be read.
     *
     * @param enkelvoudigInformatieobjectURI URI of the {@link EnkelvoudigInformatieobject}.
     * @return {@link EnkelvoudigInformatieobject}. Never 'null'!
     */
    public EnkelvoudigInformatieobject readEnkelvoudigInformatieobject(final URI enkelvoudigInformatieobjectURI) {
        return createInvocationBuilder(enkelvoudigInformatieobjectURI).get(EnkelvoudigInformatieobject.class);
    }

    public EnkelvoudigInformatieobjectWithInhoudAndLock updateEnkelvoudigInformatieobject(final UUID uuid, final String toelichting,
            final EnkelvoudigInformatieobjectWithInhoudAndLock enkelvoudigInformatieObjectWithInhoudAndLock) {
        zgwClientHeadersFactory.setAuditToelichting(toelichting);
        return drcClient.enkelvoudigInformatieobjectPartialUpdate(uuid, enkelvoudigInformatieObjectWithInhoudAndLock);
    }

    /**
     * Lock a {@link EnkelvoudigInformatieobject}.
     *
     * @param enkelvoudigInformatieobjectUUID {@link EnkelvoudigInformatieobject}
     * @param lockOwner                       Owner of the lock
     */
    public String lockEnkelvoudigInformatieobject(final UUID enkelvoudigInformatieobjectUUID, String lockOwner) {
        // If the EnkelvoudigInformatieobject is already locked a ValidationException is thrown.
        final String lock = drcClient.enkelvoudigInformatieobjectLock(enkelvoudigInformatieobjectUUID, new Lock()).getLock();
        LOCKS.put(generateLockId(enkelvoudigInformatieobjectUUID, lockOwner), lock);
        return lock;
    }

    /**
     * Unlock a {@link EnkelvoudigInformatieobject}.
     *
     * @param enkelvoudigInformatieobjectUUID {@link EnkelvoudigInformatieobject}
     * @param lockOwner                       Owner of the lock
     */
    public void unlockEnkelvoudigInformatieobject(final UUID enkelvoudigInformatieobjectUUID, String lockOwner) {
        final String lock = LOCKS.get(generateLockId(enkelvoudigInformatieobjectUUID, lockOwner));
        if (lock != null) {
            drcClient.enkelvoudigInformatieobjectUnlock(enkelvoudigInformatieobjectUUID, new Lock(lock));
            LOCKS.remove(generateLockId(enkelvoudigInformatieobjectUUID, lockOwner));
        }
    }

    /**
     * Download content of {@link EnkelvoudigInformatieobject}.
     *
     * @param enkelvoudigInformatieobjectUUID UUID of {@link EnkelvoudigInformatieobject}
     * @return Content of {@link EnkelvoudigInformatieobject}.
     */
    public ByteArrayInputStream downloadEnkelvoudigInformatieobject(final UUID enkelvoudigInformatieobjectUUID) {
        final Response response = drcClient.enkelvoudigInformatieobjectDownload(enkelvoudigInformatieobjectUUID);
        if (!response.bufferEntity()) {
            throw new RuntimeException(
                    String.format("Content of enkelvoudig informatieobject with uuid '%s' could not be buffered.", enkelvoudigInformatieobjectUUID.toString()));
        }
        return (ByteArrayInputStream) response.getEntity();
    }

    /**
     * Download content of {@link EnkelvoudigInformatieobject} of a specific version
     *
     * @param enkelvoudigInformatieobjectUUID UUID of {@link EnkelvoudigInformatieobject}
     * @param versie                          Required version
     * @return Content of {@link EnkelvoudigInformatieobject}.
     */
    public ByteArrayInputStream downloadEnkelvoudigInformatieobjectVersie(final UUID enkelvoudigInformatieobjectUUID, final Integer versie) {
        final Response response = drcClient.enkelvoudigInformatieobjectDownloadVersie(enkelvoudigInformatieobjectUUID, versie);
        if (!response.bufferEntity()) {
            throw new RuntimeException(String.format("Content of enkelvoudig informatieobject with uuid '%s' and version '%d' could not be buffered.",
                                                     enkelvoudigInformatieobjectUUID.toString(), versie));
        }
        return (ByteArrayInputStream) response.getEntity();
    }

    /**
     * List all instances of {@link AuditTrailRegel} for a specific {@link EnkelvoudigInformatieobject}.
     *
     * @param enkelvoudigInformatieobjectUUID UUID of {@link EnkelvoudigInformatieobject}.
     * @return List of {@link AuditTrailRegel} instances.
     */
    public List<AuditTrailRegel> listAuditTrail(final UUID enkelvoudigInformatieobjectUUID) {
        return drcClient.listAuditTrail(enkelvoudigInformatieobjectUUID);
    }

    public EnkelvoudigInformatieobjectWithInhoud createEnkelvoudigInformatieobject(final EnkelvoudigInformatieobjectWithInhoud informatieobject) {
        return drcClient.enkelvoudigInformatieobjectCreate(informatieobject);
    }

    public Gebruiksrechten createGebruiksrechten(final Gebruiksrechten gebruiksrechten) {
        return drcClient.gebruiksrechtenCreate(gebruiksrechten);
    }

    private String generateLockId(final UUID enkelvoudigInformatieobjectUUID, final String lockOwner) {
        return enkelvoudigInformatieobjectUUID.toString() + ';' + lockOwner;
    }

    private Invocation.Builder createInvocationBuilder(final URI uri) {
        return ClientFactory.create().target(uri)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, zgwClientHeadersFactory.generateJWTToken());
    }
}
