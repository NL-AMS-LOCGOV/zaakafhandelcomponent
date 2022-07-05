/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.ontkoppeldedocumenten.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.atos.zac.app.identity.model.RESTUser;
import net.atos.zac.app.shared.RESTResultaat;

public class RESTOntkoppeldDocumentResultaat extends RESTResultaat<RESTOntkoppeldDocument> {

    List<RESTUser> filterOntkoppeldDoor = new ArrayList<>();

    public RESTOntkoppeldDocumentResultaat(final Collection<RESTOntkoppeldDocument> resultaten, final long aantalTotaal) {
        super(resultaten, aantalTotaal);
    }

    public List<RESTUser> getFilterOntkoppeldDoor() {
        return filterOntkoppeldDoor;
    }

    public void setFilterOntkoppeldDoor(final List<RESTUser> filterOntkoppeldDoor) {
        this.filterOntkoppeldDoor = filterOntkoppeldDoor;
    }
}
