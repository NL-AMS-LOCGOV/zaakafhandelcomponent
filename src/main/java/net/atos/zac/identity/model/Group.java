/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.identity.model;

public class Group {

    private final String name;

    public Group(final String name) {
        this.name = name;
    }

    public String getId() {
        return name;
    }

    public String getName() {
        return name;
    }
}
