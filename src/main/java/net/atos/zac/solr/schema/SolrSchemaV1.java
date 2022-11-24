/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.solr.schema;

import static net.atos.zac.solr.schema.SolrSchemaUpdateHelper.addField;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;

import net.atos.zac.solr.SolrSchemaUpdate;
import net.atos.zac.zoeken.model.index.ZoekObjectType;

class SolrSchemaV1 implements SolrSchemaUpdate {

    @Override
    public int getVersie() {
        return 1;
    }

    @Override
    public Set<ZoekObjectType> getTeHerindexerenZoekObjectTypes() {
        return Set.of(ZoekObjectType.ZAAK, ZoekObjectType.TAAK);
    }

    @Override
    public List<SchemaRequest.Update> getSchemaUpdates() {
        // updateSolrConfig();
        final List<SchemaRequest.Update> schemaUpdates = new LinkedList<>();
        schemaUpdates.addAll(createGenericSchema());
        schemaUpdates.addAll(createZakenSchema());
        schemaUpdates.addAll(createTakenSchema());
        schemaUpdates.addAll(createDocumentenSchema());
        return schemaUpdates;
    }

    private void updateSolrConfig() throws SolrServerException, IOException {
        final String command = """
                {
                    "update-updateprocessor" : {
                        "name" : "add-schema-fields",
                        "class" : "solr.AddSchemaFieldsUpdateProcessorFactory",
                        "defaultFieldType" : "string"
                    }
                }
                """;
        // executeGenericSolrRequest(command);
    }

    private List<SchemaRequest.Update> createGenericSchema() {
        return List.of(
                addField("created", "pdate", true, true)
        );
    }

    private List<SchemaRequest.Update> createZakenSchema() {
        return Collections.emptyList();
    }

    private List<SchemaRequest.Update> createTakenSchema() {
        return Collections.emptyList();
    }

    private List<SchemaRequest.Update> createDocumentenSchema() {
        return Collections.emptyList();
    }
}
