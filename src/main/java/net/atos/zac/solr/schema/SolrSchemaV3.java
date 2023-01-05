/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.solr.schema;

import static net.atos.zac.solr.FieldType.STRING;
import static net.atos.zac.solr.SolrSchemaUpdateHelper.addCopyField;
import static net.atos.zac.solr.SolrSchemaUpdateHelper.addField;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.solr.client.solrj.request.schema.SchemaRequest;

import net.atos.zac.solr.SolrSchemaUpdate;
import net.atos.zac.zoeken.model.index.ZoekObjectType;

class SolrSchemaV3 implements SolrSchemaUpdate {

    @Override
    public int getVersie() {
        return 3;
    }

    @Override
    public Set<ZoekObjectType> getTeHerindexerenZoekObjectTypes() {
        return Set.of(ZoekObjectType.ZAAK, ZoekObjectType.TAAK, ZoekObjectType.DOCUMENT);
    }

    @Override
    public List<SchemaRequest.Update> getSchemaUpdates() {
        final List<SchemaRequest.Update> schemaUpdates = new LinkedList<>();
        schemaUpdates.addAll(updateGenericSchema());
        schemaUpdates.addAll(updateZaakSchema());
        schemaUpdates.addAll(updateTaakSchema());
        schemaUpdates.addAll(updateDocumentSchema());
        return schemaUpdates;
    }

    private List<SchemaRequest.Update> updateGenericSchema() {
        return List.of(
                addField("zaaktypeDomein", STRING, true)
        );
    }

    private List<SchemaRequest.Update> updateZaakSchema() {
        return List.of(
                addField("zaak_zaaktypeDomein", STRING, true),
                addCopyField("zaak_zaaktypeDomein", "text", "text_exact", "zaaktypeDomein")
        );
    }

    private List<SchemaRequest.Update> updateTaakSchema() {
        return List.of(
                addField("taak_zaaktypeDomein", STRING, true),
                addCopyField("taak_zaaktypeDomein", "text", "text_exact", "zaaktypeDomein")
        );
    }

    private List<SchemaRequest.Update> updateDocumentSchema() {
        return List.of(
                addField("informatieobject_zaaktypeDomein", STRING, true),
                addCopyField("informatieobject_zaaktypeDomein", "text", "text_exact", "zaaktypeDomein")
        );
    }
}
