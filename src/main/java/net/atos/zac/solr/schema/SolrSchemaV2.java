/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.solr.schema;

import java.util.Set;

import net.atos.zac.solr.SolrSchemaUpdate;
import net.atos.zac.zoeken.model.index.ZoekObjectType;

public class SolrSchemaV2 implements SolrSchemaUpdate {

    @Override
    public int getVersion() {
        return 2;
    }

    @Override
    public void updateSchema() {
    }

    @Override
    public Set<ZoekObjectType> herindexeren() {
        return Set.of(ZoekObjectType.TAAK);
    }
}
