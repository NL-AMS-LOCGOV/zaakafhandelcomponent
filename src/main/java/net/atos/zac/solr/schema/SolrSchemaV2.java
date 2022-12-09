/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.solr.schema;

import static net.atos.zac.solr.FieldType.PDATE;
import static net.atos.zac.solr.SolrSchemaUpdateHelper.addField;
import static net.atos.zac.solr.SolrSchemaUpdateHelper.deleteCopyField;
import static net.atos.zac.solr.SolrSchemaUpdateHelper.deleteField;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.solr.client.solrj.request.schema.SchemaRequest;

import net.atos.zac.solr.SolrSchemaUpdate;
import net.atos.zac.zoeken.model.index.ZoekObjectType;

class SolrSchemaV2 implements SolrSchemaUpdate {

    @Override
    public int getVersie() {
        return 2;
    }

    @Override
    public Set<ZoekObjectType> getTeHerindexerenZoekObjectTypes() {
        return Set.of(ZoekObjectType.TAAK);
    }

    @Override
    public List<SchemaRequest.Update> getSchemaUpdates() {
        final List<SchemaRequest.Update> schemaUpdates = new LinkedList<>();
        schemaUpdates.addAll(updateGenericSchema());
        schemaUpdates.addAll(updateTaakSchema());
        return schemaUpdates;
    }

    private List<SchemaRequest.Update> updateGenericSchema() {
        return List.of(
                deleteCopyField("zaak_einddatumGepland", "streefdatum"),
                deleteCopyField("taak_streefdatum", "streefdatum"),
                deleteField("streefdatum")
        );
    }

    private List<SchemaRequest.Update> updateTaakSchema() {
        return List.of(
                deleteField("taak_streefdatum"),
                addField("taak_fataledatum", PDATE)
        );
    }
}
