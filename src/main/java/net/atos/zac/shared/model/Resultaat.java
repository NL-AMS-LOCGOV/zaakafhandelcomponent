/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.shared.model;

import java.util.List;

public class Resultaat<TYPE> {

    private final List<TYPE> items;

    private final long count;

    public Resultaat(final List<TYPE> items, final long count) {
        this.items = items;
        this.count = count;
    }

    public List<TYPE> getItems() {
        return items;
    }

    public long getCount() {
        return count;
    }
}
