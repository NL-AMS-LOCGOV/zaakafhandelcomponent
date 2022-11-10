/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.websocket.event;

import org.apache.commons.lang3.StringUtils;

public class ScreenEventId {
    private final String resource;

    private final String detail;

    public ScreenEventId(final String resource, final String detail) {
        this.resource = resource;
        this.detail = detail;
    }

    public String getResource() {
        return resource;
    }

    public String getDetail() {
        return detail;
    }

    /* Do NOT include the detail field in equality! */
    @Override
    public boolean equals(final Object obj) {
        // snel antwoord
        if (obj == this) {
            return true;
        }
        // gebruik getClass i.p.v. instanceof, maar dan wel met de null check
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        // cast en vergelijk
        final ScreenEventId other = (ScreenEventId) obj;
        return StringUtils.equals(resource, other.resource);
    }

    @Override
    public int hashCode() {
        return resource != null ? resource.hashCode() : 0;
    }

    @Override
    public String toString() {
        return detail != null ? String.format("%s;%s", resource, detail) : resource;
    }
}
