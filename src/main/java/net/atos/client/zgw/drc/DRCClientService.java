/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.drc;

import static java.lang.String.format;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import net.atos.client.zgw.drc.model.EnkelvoudigInformatieObjectListParameters;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobjectWithLockData;
import net.atos.client.zgw.drc.model.LockEnkelvoudigInformatieObject;
import net.atos.client.zgw.drc.model.UnlockEnkelvoudigInformatieObject;
import net.atos.client.zgw.shared.util.InvocationBuilderFactory;
import net.atos.client.zgw.shared.util.URIUtil;

/**
 *
 */
@ApplicationScoped
public class DRCClientService {

    @Inject
    @RestClient
    private DRCClient drcClient;

    // In memory persistance of the mapping between a lock and its owner (lockeigenaar)
    private static final Map<String, String> LOCKS = new HashMap<>();

    public EnkelvoudigInformatieobject getEnkelvoudigInformatieobject(final String identificatie) {
        return drcClient.enkelvoudigInformatieobjectList(new EnkelvoudigInformatieObjectListParameters(identificatie))
                .getSingleResult()
                .orElseThrow(() -> new RuntimeException(format("EnkelvoudigInformatieobject with identificatie '%s' not found.", identificatie)));
    }

    public void replaceInhoudOfInformatieobjectWithIdentificatie(final String identificatie, final String inhoud) {
        final EnkelvoudigInformatieobject enkelvoudigInformatieobject = getEnkelvoudigInformatieobject(identificatie);
        final UUID uuid = URIUtil.parseUUIDFromResourceURI(enkelvoudigInformatieobject.getUrl());

        String lock = null;
        try {
            lock = lock(uuid);
            final EnkelvoudigInformatieobjectWithLockData enkelvoudigInformatieObjectWithLockData = new EnkelvoudigInformatieobjectWithLockData(lock);
            enkelvoudigInformatieObjectWithLockData.setInhoud(inhoud);
            drcClient.enkelvoudigInformatieobjectPartialUpdate(uuid, enkelvoudigInformatieObjectWithLockData);
        } finally {
            // In ALLE gevallen het document weer unlocken, OOK als er een exception is opgetreden
            unlock(uuid, lock);
        }
    }

    public void lockEnkelvoudigInformatieobject(final UUID enkelvoudigInformatieobjectUUID, String lockEigenaar) {
        final String lock = lock(enkelvoudigInformatieobjectUUID);
        try {
            putLock(enkelvoudigInformatieobjectUUID, lockEigenaar, lock);
        } catch (Exception ex) {
            // Als de sleutel niet gepersisteerd kon worden, dan het document ook NIET gelockt laten
            unlock(enkelvoudigInformatieobjectUUID, lock);
            throw ex;
        }
    }

    public void unlockEnkelvoudigInformatieobject(final UUID enkelvoudigInformatieobjectUUID, String lockEigenaar) {
        unlock(enkelvoudigInformatieobjectUUID, getLock(enkelvoudigInformatieobjectUUID, lockEigenaar));
        // Pas nadat het document geunlocked is de sleutel weer opruimen
        deleteLock(enkelvoudigInformatieobjectUUID, lockEigenaar);
    }

    public EnkelvoudigInformatieobject getEnkelvoudigInformatieobject(final URI enkelvoudigInformatieobjectURI) {
        return InvocationBuilderFactory.create(enkelvoudigInformatieobjectURI).get(EnkelvoudigInformatieobject.class);
    }

    private String lock(final UUID enkelvoudigInformatieobjectUUID) {
        return drcClient.enkelvoudigInformatieobjectLock(enkelvoudigInformatieobjectUUID, new LockEnkelvoudigInformatieObject()).getLock();
    }

    private void unlock(final UUID uuid, final String lock) {
        if (lock != null) {
            drcClient.enkelvoudigInformatieobjectUnlock(uuid, new UnlockEnkelvoudigInformatieObject(lock));
        }
    }

    private void putLock(final UUID enkelvoudigInformatieobjectUUID, final String lockEigenaar, final String lock) {
        if (lock != null) {
            LOCKS.put(generateLockId(enkelvoudigInformatieobjectUUID, lockEigenaar), lock);
        }
    }

    private String getLock(final UUID enkelvoudigInformatieobjectUUID, final String lockEigenaar) {
        return LOCKS.get(generateLockId(enkelvoudigInformatieobjectUUID, lockEigenaar));
    }

    private void deleteLock(final UUID enkelvoudigInformatieobjectUUID, final String lockEigenaar) {
        LOCKS.remove(generateLockId(enkelvoudigInformatieobjectUUID, lockEigenaar));
    }

    private String generateLockId(final UUID enkelvoudigInformatieobjectUUID, final String lockEigenaar) {
        return enkelvoudigInformatieobjectUUID.toString() + ';' + lockEigenaar;
    }
}
