/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.solr;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.request.schema.SchemaRequest;

public final class SolrSchemaUpdateHelper {

    public static final String TYPE_STRING = "string";

    public static final String TYPE_LOCATION = "location";

    public static final String TYPE_PDATE = "pdate";

    public static final String TYPE_PINT = "pint";

    public static final String TYPE_PLONG = "plong";

    public static final String TYPE_PDOUBLE = "pdouble";

    public static final String TYPE_BOOLEAN = "boolean";

    public static final String TYPE_TEXT_NL = "text_nl";

    public static final String TYPE_TEXT_WS = "text_ws";

    public static final String TYPE_TEXT_GENERAL_REV = "text_general_rev";

    public static final String NAME = "name";

    private static final String TYPE = "type";

    private static final String INDEXED = "indexed";

    private static final String STORED = "stored";

    private static final String DEFAULT = "default";

    private static final String DOC_VALUES = "docValues";

    private static final String MULTI_VALUED = "multiValued";

    private SolrSchemaUpdateHelper() {
    }

    public static SchemaRequest.AddField addField(final String name, final String type) {
        return new SchemaRequest.AddField(
                Map.of(NAME, name,
                       TYPE, type
                ));
    }

    public static SchemaRequest.AddField addField(final String name, final String type, final String defaultValue) {
        return new SchemaRequest.AddField(
                Map.of(NAME, name,
                       TYPE, type,
                       DEFAULT, defaultValue
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
}
