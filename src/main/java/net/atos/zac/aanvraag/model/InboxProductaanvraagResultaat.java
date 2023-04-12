/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.aanvraag.model;

import java.util.List;

import net.atos.zac.shared.model.Resultaat;

public class InboxProductaanvraagResultaat extends Resultaat<InboxProductaanvraag> {

    private final List<String> typeFilter;

    public InboxProductaanvraagResultaat(final List<InboxProductaanvraag> items, final long count, final List<String> typeFilter) {
        super(items, count);
        this.typeFilter = typeFilter;
    }

    public List<String> getTypeFilter() {
        return typeFilter;
    }
}
