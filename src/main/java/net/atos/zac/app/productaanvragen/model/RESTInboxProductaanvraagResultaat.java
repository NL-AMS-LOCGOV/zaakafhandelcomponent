/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.productaanvragen.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.atos.zac.app.shared.RESTResultaat;

public class RESTInboxProductaanvraagResultaat extends RESTResultaat<RESTInboxProductaanvraag> {
    public List<String> filterType = new ArrayList<>();

    public RESTInboxProductaanvraagResultaat(final Collection<RESTInboxProductaanvraag> resultaten, final long aantalTotaal) {
        super(resultaten, aantalTotaal);
    }
}
