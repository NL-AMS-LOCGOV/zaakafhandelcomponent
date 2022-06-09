/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.drc.model;

/**
 * Representation of EnkelvoudigInformatieobject for PUT request and respone and for PATCH request only
 */
public class EnkelvoudigInformatieobjectWithInhoudAndLock extends EnkelvoudigInformatieobjectWithInhoud {

    /**
     * Tijdens het updaten van een document (PATCH, PUT) moet het `lock` veld opgegeven worden.
     */
    private String lock;

    public String getLock() {
        return lock;
    }

    public void setLock(final String lock) {
        this.lock = lock;
    }
}
