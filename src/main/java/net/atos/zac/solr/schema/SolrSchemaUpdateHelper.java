/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.solr.schema;

import java.util.Map;

import org.apache.solr.client.solrj.request.schema.SchemaRequest;

public final class SolrSchemaUpdateHelper {

    public static final String NAME = "name";

    public static final String STRING = "string";

    private static final String TYPE = "type";

    private static final String INDEXED = "indexed";

    private static final String STORED = "stored";

    private SolrSchemaUpdateHelper() {
    }

    public static SchemaRequest.AddField addField(final String name, final String type, final boolean indexed,
            final boolean stored) {
        return new SchemaRequest.AddField(
                Map.of(NAME, name,
                       TYPE, type,
                       INDEXED, indexed,
                       STORED, stored
                ));
    }

    public static SchemaRequest.DeleteField deleteField(final String name) {
        return new SchemaRequest.DeleteField(name);
    }

//    protected void executeGenericSolrRequest(final String command) throws SolrServerException, IOException {
//        final var request = new GenericSolrRequest(SolrRequest.METHOD.POST, "/config", null);
//        final var content = new RequestWriter.StringPayloadContentWriter(command, MediaType.APPLICATION_JSON);
//        request.setContentWriter(content);
//        request.process(solrClient);
//    }
}
