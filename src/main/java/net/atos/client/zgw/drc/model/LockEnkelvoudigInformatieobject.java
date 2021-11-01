/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.drc.model;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

/**
 *
 */
public class LockEnkelvoudigInformatieobject {

    /**
     * Hash string, wordt gebruikt als ID voor de lock
     */
    private String lock;


    /**
     * Constructor for POST request
     */
    public LockEnkelvoudigInformatieobject() {
    }

    /**
     * Constructor for POST response
     */
    @JsonbCreator
    public LockEnkelvoudigInformatieobject(@JsonbProperty("lock") final String lock) {
        this.lock = lock;
    }

    public String getLock() {
        return lock;
    }
}
