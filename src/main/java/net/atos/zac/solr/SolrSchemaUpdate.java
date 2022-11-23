/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.solr;

import java.util.Collections;
import java.util.Set;

import net.atos.zac.zoeken.model.index.ZoekObjectType;

public interface SolrSchemaUpdate {

    void updateSchema();

    int getVersion();

    default Set<ZoekObjectType> herindexeren() {
        return Collections.emptySet();
    }
}
