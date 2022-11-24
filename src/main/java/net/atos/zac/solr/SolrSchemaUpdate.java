/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.solr;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.solr.client.solrj.request.schema.SchemaRequest;

import net.atos.zac.zoeken.model.index.ZoekObjectType;

public interface SolrSchemaUpdate {

    int getVersie();

    default Set<ZoekObjectType> getTeHerindexerenZoekObjectTypes() {
        return Collections.emptySet();
    }

    List<SchemaRequest.Update> getSchemaUpdates();
}
