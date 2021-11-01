/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.drc.model;

/**
 *
 */
public class UnlockEnkelvoudigInformatieobject {

    /**
     * Hash string, wordt gebruikt als ID voor de lock
     */
    private final String lock;

    /**
     * Constructor for POST request
     *
     * @param lock
     */
    public UnlockEnkelvoudigInformatieobject(final String lock) {
        this.lock = lock;
    }

    public String getLock() {
        return lock;
    }
}
