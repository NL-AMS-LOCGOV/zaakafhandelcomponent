/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.signalering.event;

import java.util.Objects;

public class SignaleringEventId<ID> {
    private final ID resource;

    private final ID detail;

    public SignaleringEventId(final ID resource, final ID detail) {
        this.resource = resource;
        this.detail = detail;
    }

    public ID getResource() {
        return resource;
    }

    public ID getDetail() {
        return detail;
    }

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
        final SignaleringEventId<?> other = (SignaleringEventId<?>) obj;
        return Objects.equals(resource, other.resource) && Objects.equals(detail, other.detail);
    }

    @Override
    public int hashCode() {
        final int result = resource != null ? resource.hashCode() : 0;
        return detail != null ? 31 * result + detail.hashCode() : result;
    }

    @Override
    public String toString() {
        return detail != null ? String.format("%s;%s", resource, detail) : resource.toString();
    }
}
