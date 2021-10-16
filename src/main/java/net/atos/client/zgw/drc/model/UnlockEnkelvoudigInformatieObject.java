/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.drc.model;

/**
 *
 */
public class UnlockEnkelvoudigInformatieObject {

    /**
     * Hash string, wordt gebruikt als ID voor de lock
     */
    private final String lock;

    /**
     * Constructor for POST request
     *
     * @param lock
     */
    public UnlockEnkelvoudigInformatieObject(final String lock) {
        this.lock = lock;
    }

    public String getLock() {
        return lock;
    }
}
