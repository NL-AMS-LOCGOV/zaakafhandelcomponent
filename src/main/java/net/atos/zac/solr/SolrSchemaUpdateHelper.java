/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.solr;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.request.schema.SchemaRequest;

public final class SolrSchemaUpdateHelper {

    public static final String NAME = "name";

    private static final String TYPE = "type";

    private static final String INDEXED = "indexed";

    private static final String STORED = "stored";

    private static final String DEFAULT = "default";

    private static final String DOC_VALUES = "docValues";

    private static final String MULTI_VALUED = "multiValued";

    private SolrSchemaUpdateHelper() {
    }

    public static SchemaRequest.AddField addField(final String name, final FieldType type) {
        return new SchemaRequest.AddField(
                Map.of(NAME, name,
                       TYPE, type.getValue()
                ));
    }

    public static SchemaRequest.AddField addField(final String name, final FieldType type, final String defaultValue) {
        return new SchemaRequest.AddField(
                Map.of(NAME, name,
                       TYPE, type.getValue(),
                       DEFAULT, defaultValue
                ));
    }

    public static SchemaRequest.AddField addField(final String name, final FieldType type, final boolean docValues) {
        return new SchemaRequest.AddField(
                Map.of(NAME, name,
                       TYPE, type.getValue(),
                       DOC_VALUES, docValues
                ));
    }

    public static SchemaRequest.AddField addField(final String name, final FieldType type, final boolean indexed,
            final boolean stored) {
        return new SchemaRequest.AddField(
                Map.of(NAME, name,
                       TYPE, type.getValue(),
                       INDEXED, indexed,
                       STORED, stored
                ));
    }

    public static SchemaRequest.AddField addField(final String name, final FieldType type, final boolean indexed,
            final boolean stored, final boolean docValues) {
        return new SchemaRequest.AddField(
                Map.of(NAME, name,
                       TYPE, type.getValue(),
                       INDEXED, indexed,
                       STORED, stored,
                       DOC_VALUES, docValues
                ));
    }

    public static SchemaRequest.AddField addFieldMultiValued(final String name, final FieldType type) {
        return new SchemaRequest.AddField(
                Map.of(NAME, name,
                       TYPE, type.getValue(),
                       MULTI_VALUED, true
                ));
    }

    public static SchemaRequest.AddField addFieldMultiValued(final String name, final FieldType type,
            final boolean docValues) {
        return new SchemaRequest.AddField(
                Map.of(NAME, name,
                       TYPE, type.getValue(),
                       DOC_VALUES, docValues,
                       MULTI_VALUED, true
                ));
    }

    public static SchemaRequest.AddField addFieldMultiValued(final String name, final FieldType type,
            final boolean indexed, final boolean stored) {
        return new SchemaRequest.AddField(
                Map.of(NAME, name,
                       TYPE, type.getValue(),
                       INDEXED, indexed,
                       STORED, stored,
                       MULTI_VALUED, true
                ));
    }

    public static SchemaRequest.AddCopyField addCopyField(final String source, final String... dest) {
        return new SchemaRequest.AddCopyField(source, List.of(dest));
    }

    public static SchemaRequest.DeleteCopyField deleteCopyField(final String source, final String... dest) {
        return new SchemaRequest.DeleteCopyField(source, List.of(dest));
    }

    public static SchemaRequest.AddDynamicField addDynamicField(final String name, final FieldType type,
            final boolean indexed, final boolean stored) {
        return new SchemaRequest.AddDynamicField(
                Map.of(NAME, name,
                       TYPE, type.getValue(),
                       INDEXED, indexed,
                       STORED, stored
                ));
    }

    public static SchemaRequest.AddDynamicField addDynamicField(final String name, final FieldType type,
            final boolean indexed, final boolean stored, final boolean multiValued) {
        return new SchemaRequest.AddDynamicField(
                Map.of(NAME, name,
                       TYPE, type.getValue(),
                       INDEXED, indexed,
                       STORED, stored,
                       MULTI_VALUED, multiValued
                ));
    }

    public static SchemaRequest.DeleteField deleteField(final String name) {
        return new SchemaRequest.DeleteField(name);
    }
}
