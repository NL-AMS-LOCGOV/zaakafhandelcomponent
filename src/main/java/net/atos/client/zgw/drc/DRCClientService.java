/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.drc;

import static java.lang.String.format;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import net.atos.client.zgw.drc.model.EnkelvoudigInformatieObject;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieObjectListParameters;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieObjectWithLockData;
import net.atos.client.zgw.drc.model.LockEnkelvoudigInformatieObject;
import net.atos.client.zgw.drc.model.UnlockEnkelvoudigInformatieObject;
import net.atos.client.zgw.shared.util.URIUtil;

/**
 *
 */
@ApplicationScoped
public class DRCClientService {

    @Inject
    @RestClient
    private DRCClient drcClient;

    // Dummy sleutel persistance
    private static final Map<String, String> DUMMY = new HashMap<>();

    public Optional<EnkelvoudigInformatieObject> getInformatieObject(final String identificatie) {
        return drcClient.enkelvoudiginformatieobjectList(new EnkelvoudigInformatieObjectListParameters(identificatie)).getSingleResult();
    }

    public void replaceInhoudOfInformatieObjectWithIdentificatie(final String identificatie, final String inhoud) {
        final EnkelvoudigInformatieObject enkelvoudigInformatieObject = getInformatieObject(identificatie)
                .orElseThrow(() -> new RuntimeException(format("EnkelvoudigInformatieObject with identificatie '%s' not found.", identificatie)));

        final UUID uuid = URIUtil.parseUUIDFromResourceURI(enkelvoudigInformatieObject.getUrl());

        String lock = null;
        try {
            lock = lock(uuid);
            final EnkelvoudigInformatieObjectWithLockData enkelvoudigInformatieObjectWithLockData = new EnkelvoudigInformatieObjectWithLockData(lock);
            enkelvoudigInformatieObjectWithLockData.setInhoud(inhoud);
            drcClient.enkelvoudiginformatieobjectPartialUpdate(uuid, enkelvoudigInformatieObjectWithLockData);
        } finally {
            // In ALLE gevallen het document weer unlocken, OOK als er een exception is opgetreden
            unlock(uuid, lock);
        }
    }

    public void lockInformatieObject(final UUID uuid, String lockEigenaar) {
        final String lock = lock(uuid);
        try {
            putLock(uuid, lockEigenaar, lock);
        } catch (Exception ex) {
            // Als de sleutel niet gepersisteerd kon worden, dan het document ook NIET gelockt laten
            unlock(uuid, lock);
            throw ex;
        }
    }

    public void unlockInformatieObject(final UUID uuid, String lockEigenaar) {
        unlock(uuid, getLock(uuid, lockEigenaar));
        // Pas nadat het document geunlocked is de sleutel weer opruimen
        deleteLock(uuid, lockEigenaar);
    }

    private String lock(final UUID uuid) {
        return drcClient.enkelvoudiginformatieobjectLock(uuid, new LockEnkelvoudigInformatieObject()).getLock();
    }

    private void unlock(final UUID uuid, final String lock) {
        if (lock != null) {
            drcClient.enkelvoudiginformatieobjectUnlock(uuid, new UnlockEnkelvoudigInformatieObject(lock));
        }
    }

    // Dummy sleutel persistance
    private void putLock(final UUID uuid, final String lockEigenaar, final String lock) {
        if (lock != null) {
            DUMMY.put(lockId(uuid, lockEigenaar), lock);
        }
    }

    // Dummy sleutel persistance
    private String getLock(final UUID uuid, final String lockEigenaar) {
        return DUMMY.get(lockId(uuid, lockEigenaar));
    }

    // Dummy sleutel persistance
    private void deleteLock(final UUID uuid, final String lockEigenaar) {
        DUMMY.remove(lockId(uuid, lockEigenaar));
    }

    // Dummy persistance
    private String lockId(final UUID uuid, final String lockEigenaar) {
        return uuid.toString() + ';' + lockEigenaar;
    }
}
