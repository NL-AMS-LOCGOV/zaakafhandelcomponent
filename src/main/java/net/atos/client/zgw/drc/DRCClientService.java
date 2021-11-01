/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.drc;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.drc.model.LockEnkelvoudigInformatieobject;
import net.atos.client.zgw.drc.model.UnlockEnkelvoudigInformatieobject;
import net.atos.client.zgw.shared.util.ZGWApisInvocationBuilderFactory;

/**
 *
 */
@ApplicationScoped
public class DRCClientService {

    @Inject
    @RestClient
    private DRCClient drcClient;

    /*
     * In memory persistance map of locks.
     * key: Combination of EnkelvoudigInformatieobject UUID and owner of the lock.
     * value: the lock.
     *
     * ToDo: ESUITEDEV-25841
     */
    private static final Map<String, String> LOCKS = new HashMap<>();

    public EnkelvoudigInformatieobject getEnkelvoudigInformatieobject(final URI enkelvoudigInformatieobjectURI) {
        return ZGWApisInvocationBuilderFactory.create(enkelvoudigInformatieobjectURI).get(EnkelvoudigInformatieobject.class);
    }

    public void lockEnkelvoudigInformatieobject(final UUID enkelvoudigInformatieobjectUUID, String lockOwner) {
        // If the EnkelvoudigInformatieobject is already locked a ValidationException is thrown.
        final String lock = drcClient.enkelvoudigInformatieobjectLock(enkelvoudigInformatieobjectUUID, new LockEnkelvoudigInformatieobject()).getLock();
        LOCKS.put(generateLockId(enkelvoudigInformatieobjectUUID, lockOwner), lock);
    }

    public void unlockEnkelvoudigInformatieobject(final UUID enkelvoudigInformatieobjectUUID, String lockOwner) {
        final String lock = LOCKS.get(generateLockId(enkelvoudigInformatieobjectUUID, lockOwner));
        if (lock != null) {
            drcClient.enkelvoudigInformatieobjectUnlock(enkelvoudigInformatieobjectUUID, new UnlockEnkelvoudigInformatieobject(lock));
            LOCKS.remove(generateLockId(enkelvoudigInformatieobjectUUID, lockOwner));
        }
    }

    private String generateLockId(final UUID enkelvoudigInformatieobjectUUID, final String lockOwner) {
        return enkelvoudigInformatieobjectUUID.toString() + ';' + lockOwner;
    }
}
