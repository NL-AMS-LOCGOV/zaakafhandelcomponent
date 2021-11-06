/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.drc.model;

import java.util.UUID;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

/**
 *
 */
public class Lock {

    /**
     * Hash string, wordt gebruikt als ID voor de lock
     */
    private final String lock;

    public Lock() {
        lock = UUID.randomUUID().toString();
    }

    /**
     * Constructor for POST response
     */
    @JsonbCreator
    public Lock(@JsonbProperty("lock") final String lock) {
        this.lock = lock;
    }

    public String getLock() {
        return lock;
    }
}
