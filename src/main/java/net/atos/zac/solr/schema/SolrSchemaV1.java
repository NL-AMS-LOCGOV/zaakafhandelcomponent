/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.solr.schema;

import static net.atos.zac.solr.schema.SolrSchemaUpdateHelper.addCopyField;
import static net.atos.zac.solr.schema.SolrSchemaUpdateHelper.addDynamicField;
import static net.atos.zac.solr.schema.SolrSchemaUpdateHelper.addField;
import static net.atos.zac.solr.schema.SolrSchemaUpdateHelper.addFieldMultiValued;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
        return Set.of(ZoekObjectType.ZAAK, ZoekObjectType.TAAK, ZoekObjectType.DOCUMENT);
    }

    @Override
    public List<SchemaRequest.Update> getSchemaUpdates() {
        final List<SchemaRequest.Update> schemaUpdates = new LinkedList<>();
        schemaUpdates.addAll(createGenericSchema());
        schemaUpdates.addAll(createZaakSchema());
        schemaUpdates.addAll(createTaakSchema());
        schemaUpdates.addAll(createInformatieobjectSchema());
        return schemaUpdates;
    }

    private List<SchemaRequest.Update> createGenericSchema() {
        return List.of(
                addField("created", "pdate"),
                addField("type", "string"),
                addField("timestamp", "pdate", "NOW"),
                addField("zaaktypeUuid", "string"),
                addField("zaaktypeIdentificatie", "string"),
                addField("zaaktypeOmschrijving", "string", true),
                addField("behandelaarNaam", "string", true),
                addField("groepNaam", "string", true),
                addField("isToegekend", "boolean", true),
                addField("startdatum", "pdate"),
                addField("streefdatum", "pdate"),
                addFieldMultiValued("text", "text_nl", true, false),
                addFieldMultiValued("text_exact", "text_ws", true, false),
                addFieldMultiValued("text_rev", "text_general_rev", true, false),
                addDynamicField("*_coordinate", "pdouble", true, false),
                addCopyField("id", "text", "text_exact")
        );
    }

    private List<SchemaRequest.Update> createZaakSchema() {
        return List.of(
                addField("zaak_identificatie", "string"),
                addCopyField("zaak_identificatie", "text", "text_exact"),
                addField("zaak_omschrijving", "text_nl"),
                addCopyField("zaak_omschrijving", "text"),
                addField("zaak_toelichting", "text_nl"),
                addCopyField("zaak_toelichting", "text"),
                addField("zaak_registratiedatum", "pdate"),
                addCopyField("zaak_registratiedatum", "created"),
                addField("zaak_startdatum", "pdate"),
                addCopyField("zaak_startdatum", "startdatum"),
                addField("zaak_einddatumGepland", "pdate"),
                addCopyField("zaak_einddatumGepland", "streefdatum"),
                addField("zaak_einddatum", "pdate"),
                addField("zaak_uiterlijkeEinddatumAfdoening", "pdate"),
                addField("zaak_publicatiedatum", "pdate"),
                addField("zaak_communicatiekanaal", "string", true),
                addCopyField("zaak_communicatiekanaal", "text"),
                addField("zaak_vertrouwelijkheidaanduiding", "string", true),
                addField("zaak_afgehandeld", "boolean", true),
                addField("zaak_groepId", "string"),
                addField("zaak_groepNaam", "string", true),
                addCopyField("zaak_groepNaam", "text", "text_exact", "groepNaam"),
                addField("zaak_behandelaarNaam", "string", true),
                addCopyField("zaak_behandelaarNaam", "text", "text_exact", "behandelaarNaam"),
                addField("zaak_behandelaarGebruikersnaam", "string"),
                addCopyField("zaak_behandelaarGebruikersnaam", "text"),
                addField("zaak_initiatorIdentificatie", "string"),
                addCopyField("zaak_initiatorIdentificatie", "text", "text_exact"),
                addField("zaak_initiatorType", "string", true),
                addField("zaak_locatie", "location"),
                addField("zaak_locatie_adres", "text_nl"),
                addField("zaak_redenOpschorting", "text_nl"),
                addCopyField("zaak_redenOpschorting", "text"),
                addField("zaak_redenVerlenging", "text_nl"),
                addCopyField("zaak_redenVerlenging", "text"),
                addField("zaak_duurVerlenging", "string", true),
                addField("zaak_zaaktypeUuid", "string"),
                addCopyField("zaak_zaaktypeUuid", "zaaktypeUuid"),
                addField("zaak_zaaktypeIdentificatie", "string"),
                addCopyField("zaak_zaaktypeIdentificatie", "text", "zaaktypeIdentificatie"),
                addField("zaak_zaaktypeOmschrijving", "string", true),
                addCopyField("zaak_zaaktypeOmschrijving", "text", "zaaktypeOmschrijving"),
                addField("zaak_statustypeOmschrijving", "string"),
                addCopyField("zaak_statustypeOmschrijving", "text", "text_exact"),
                addField("zaak_statusDatumGezet", "pdate"),
                addField("zaak_statusToelichting", "text_nl"),
                addCopyField("zaak_statusToelichting", "text"),
                addField("zaak_statusEindstatus", "boolean", true),
                addField("zaak_resultaattypeOmschrijving", "string"),
                addCopyField("zaak_resultaattypeOmschrijving", "text", "text_exact"),
                addField("zaak_resultaatToelichting", "text_nl"),
                addCopyField("zaak_resultaatToelichting", "text"),
                addField("zaak_aantalOpenstaandeTaken", "pint"),
                addFieldMultiValued("zaak_indicaties", "string", true),
                addField("zaak_indicaties_sort", "plong", true, false, true)
        );
    }

    private List<SchemaRequest.Update> createTaakSchema() {
        return List.of(
                addField("taak_naam", "string", true),
                addCopyField("taak_naam", "text"),
                addField("taak_toelichting", "text_nl"),
                addCopyField("taak_toelichting", "text"),
                addField("taak_status", "string", true),
                addCopyField("taak_status", "text", "text_exact"),
                addField("taak_zaaktypeUuid", "string"),
                addCopyField("taak_zaaktypeUuid", "zaaktypeUuid"),
                addField("taak_zaaktypeIdentificatie", "string"),
                addCopyField("taak_zaaktypeIdentificatie", "text", "zaaktypeIdentificatie"),
                addField("taak_zaaktypeOmschrijving", "string", true),
                addCopyField("taak_zaaktypeOmschrijving", "text", "text_exact", "zaaktypeOmschrijving"),
                addField("taak_zaakUuid", "string"),
                addField("taak_zaakId", "string"),
                addCopyField("taak_zaakId", "text_exact"),
                addField("taak_creatiedatum", "pdate"),
                addCopyField("taak_creatiedatum", "created", "startdatum"),
                addField("taak_toekenningsdatum", "pdate"),
                addField("taak_streefdatum", "pdate"),
                addCopyField("taak_streefdatum", "streefdatum"),
                addField("taak_groepId", "string"),
                addField("taak_groepNaam", "string", true),
                addCopyField("taak_groepNaam", "text", "text_exact", "groepNaam"),
                addField("taak_behandelaarNaam", "string", true),
                addCopyField("taak_behandelaarNaam", "text", "text_exact", "behandelaarNaam"),
                addField("taak_behandelaarGebruikersnaam", "string"),
                addCopyField("taak_behandelaarGebruikersnaam", "text"),
                addFieldMultiValued("taak_data", "string"),
                addCopyField("taak_data", "text"),
                addFieldMultiValued("taak_informatie", "string")
        );
    }

    private List<SchemaRequest.Update> createInformatieobjectSchema() {
        return List.of(
                addField("informatieobject_identificatie", "string"),
                addCopyField("informatieobject_identificatie", "text", "text_exact"),
                addField("informatieobject_titel", "text_nl"),
                addCopyField("informatieobject_titel", "text"),
                addField("informatieobject_titel_sort", "string", true, false),
                addCopyField("informatieobject_titel", "informatieobject_titel_sort"),
                addField("informatieobject_beschrijving", "text_nl"),
                addCopyField("informatieobject_beschrijving", "text"),
                addField("informatieobject_zaaktypeUuid", "string"),
                addCopyField("informatieobject_zaaktypeUuid", "zaaktypeUuid"),
                addField("informatieobject_zaaktypeIdentificatie", "string"),
                addCopyField("informatieobject_zaaktypeIdentificatie", "zaaktypeIdentificatie"),
                addField("informatieobject_zaaktypeOmschrijving", "string", true),
                addCopyField("informatieobject_zaaktypeOmschrijving", "zaaktypeOmschrijving"),
                addField("informatieobject_zaakId", "string"),
                addCopyField("informatieobject_zaakId", "text_exact"),
                addField("informatieobject_zaakUuid", "string"),
                addCopyField("informatieobject_zaakUuid", "text_exact"),
                addField("informatieobject_zaakAfgehandeld", "boolean", true),
                addField("informatieobject_zaakRelatie", "string"),
                addField("informatieobject_creatiedatum", "pdate"),
                addField("informatieobject_vertrouwelijkheidaanduiding", "string", true),
                addField("informatieobject_auteur", "text_nl"),
                addCopyField("informatieobject_auteur", "text"),
                addField("informatieobject_auteur_sort", "string", true, false),
                addCopyField("informatieobject_auteur", "informatieobject_auteur_sort"),
                addField("informatieobject_status", "string", true),
                addCopyField("informatieobject_status", "text"),
                addField("informatieobject_formaat", "string", true),
                addField("informatieobject_versie", "pint"),
                addField("informatieobject_registratiedatum", "pdate"),
                addCopyField("informatieobject_registratiedatum", "created", "startdatum"),
                addField("informatieobject_bestandsnaam", "string"),
                addCopyField("informatieobject_bestandsnaam", "text"),
                addField("informatieobject_bestandsomvang", "plong"),
                addField("informatieobject_documentType", "string", true),
                addCopyField("informatieobject_documentType", "text"),
                addField("informatieobject_ontvangstdatum", "pdate"),
                addField("informatieobject_verzenddatum", "pdate"),
                addField("informatieobject_ondertekeningDatum", "pdate"),
                addField("informatieobject_ondertekeningSoort", "string"),
                addField("informatieobject_inhoudUrl", "string"),
                addField("informatieobject_vergrendeldDoorNaam", "string", true),
                addField("informatieobject_vergrendeldDoorGebruikersnaam", "string"),
                addFieldMultiValued("informatieobject_indicaties", "string", true),
                addField("informatieobject_indicaties_sort", "plong", true, false, true)
        );
    }
}
