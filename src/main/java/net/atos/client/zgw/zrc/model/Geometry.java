/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model;

/**
 *
 */
public class Geometry {

    private final String type;

    public Geometry(final String type) {
        this.type = type;
    }

    public Geometry() {
        type = null;
    }

    public String getType() {
        return type;
    }
}
