/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.identity.model;

public class Group {

    private final String id;

    private final String name;

    public Group(final String id, final String name) {
        this.id = id;
        this.name = name != null ? name : id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
