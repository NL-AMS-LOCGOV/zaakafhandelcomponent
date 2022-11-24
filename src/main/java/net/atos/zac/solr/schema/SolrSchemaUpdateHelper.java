/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.solr.schema;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.request.schema.SchemaRequest;

public final class SolrSchemaUpdateHelper {

    public static final String NAME = "name";

    public static final String STRING = "string";

    private static final String TYPE = "type";

    private static final String INDEXED = "indexed";

    private static final String STORED = "stored";

    private static final String DEFAULT = "default";

    private static final String DOC_VALUES = "docValues";

    private static final String MULTI_VALUED = "multiValued";

    private static final String SOURCE = "source";

    private static final String DEST = "dest";

    private SolrSchemaUpdateHelper() {
    }

    public static SchemaRequest.AddField addField(final String name, final String type) {
        return new SchemaRequest.AddField(
                Map.of(NAME, name,
                       TYPE, type
                ));
    }

    public static SchemaRequest.AddField addField(final String name, final String type, final String _default) {
        return new SchemaRequest.AddField(
                Map.of(NAME, name,
                       TYPE, type,
                       DEFAULT, _default
                ));
    }

    public static SchemaRequest.AddField addField(final String name, final String type, final boolean docValues) {
        return new SchemaRequest.AddField(
                Map.of(NAME, name,
                       TYPE, type,
                       DOC_VALUES, docValues
                ));
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

    public static SchemaRequest.AddField addField(final String name, final String type, final boolean indexed,
            final boolean stored, final boolean docValues) {
        return new SchemaRequest.AddField(
                Map.of(NAME, name,
                       TYPE, type,
                       INDEXED, indexed,
                       STORED, stored,
                       DOC_VALUES, docValues
                ));
    }

    public static SchemaRequest.AddField addFieldMultiValued(final String name, final String type) {
        return new SchemaRequest.AddField(
                Map.of(NAME, name,
                       TYPE, type,
                       MULTI_VALUED, true
                ));
    }

    public static SchemaRequest.AddField addFieldMultiValued(final String name, final String type,
            final boolean docValues) {
        return new SchemaRequest.AddField(
                Map.of(NAME, name,
                       TYPE, type,
                       DOC_VALUES, docValues,
                       MULTI_VALUED, true
                ));
    }

    public static SchemaRequest.AddField addFieldMultiValued(final String name, final String type,
            final boolean indexed, final boolean stored) {
        return new SchemaRequest.AddField(
                Map.of(NAME, name,
                       TYPE, type,
                       INDEXED, indexed,
                       STORED, stored,
                       MULTI_VALUED, true
                ));
    }

    public static SchemaRequest.AddCopyField addCopyField(final String source, final String dest) {
        return new SchemaRequest.AddCopyField(source, List.of(dest));
    }

    public static SchemaRequest.AddCopyField addCopyField(final String source, final String dest1, final String dest2) {
        return new SchemaRequest.AddCopyField(source, List.of(dest1, dest2));
    }

    public static SchemaRequest.AddCopyField addCopyField(final String source, final String dest1, final String dest2
            , final String dest3) {
        return new SchemaRequest.AddCopyField(source, List.of(dest1, dest2, dest3));
    }

    public static SchemaRequest.AddDynamicField addDynamicField(final String name, final String type,
            final boolean indexed, final boolean stored) {
        return new SchemaRequest.AddDynamicField(
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
