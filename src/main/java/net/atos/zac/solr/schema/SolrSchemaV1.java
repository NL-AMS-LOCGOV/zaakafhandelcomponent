/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.solr.schema;

import static net.atos.zac.solr.SolrSchemaUpdateHelper.TYPE_BOOLEAN;
import static net.atos.zac.solr.SolrSchemaUpdateHelper.TYPE_LOCATION;
import static net.atos.zac.solr.SolrSchemaUpdateHelper.TYPE_PDATE;
import static net.atos.zac.solr.SolrSchemaUpdateHelper.TYPE_PDOUBLE;
import static net.atos.zac.solr.SolrSchemaUpdateHelper.TYPE_PINT;
import static net.atos.zac.solr.SolrSchemaUpdateHelper.TYPE_PLONG;
import static net.atos.zac.solr.SolrSchemaUpdateHelper.TYPE_STRING;
import static net.atos.zac.solr.SolrSchemaUpdateHelper.TYPE_TEXT_GENERAL_REV;
import static net.atos.zac.solr.SolrSchemaUpdateHelper.TYPE_TEXT_NL;
import static net.atos.zac.solr.SolrSchemaUpdateHelper.TYPE_TEXT_WS;
import static net.atos.zac.solr.SolrSchemaUpdateHelper.addCopyField;
import static net.atos.zac.solr.SolrSchemaUpdateHelper.addDynamicField;
import static net.atos.zac.solr.SolrSchemaUpdateHelper.addField;
import static net.atos.zac.solr.SolrSchemaUpdateHelper.addFieldMultiValued;

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
                addField("created", TYPE_PDATE),
                addField("type", TYPE_STRING),
                addField("timestamp", TYPE_PDATE, "NOW"),
                addField("zaaktypeUuid", TYPE_STRING),
                addField("zaaktypeIdentificatie", TYPE_STRING),
                addField("zaaktypeOmschrijving", TYPE_STRING, true),
                addField("behandelaarNaam", TYPE_STRING, true),
                addField("groepNaam", TYPE_STRING, true),
                addField("isToegekend", TYPE_BOOLEAN, true),
                addField("startdatum", TYPE_PDATE),
                addField("streefdatum", TYPE_PDATE),
                addFieldMultiValued("text", TYPE_TEXT_NL, true, false),
                addFieldMultiValued("text_exact", TYPE_TEXT_WS, true, false),
                addFieldMultiValued("text_rev", TYPE_TEXT_GENERAL_REV, true, false),
                addDynamicField("*_coordinate", TYPE_PDOUBLE, true, false),
                addCopyField("id", "text", "text_exact")
        );
    }

    private List<SchemaRequest.Update> createZaakSchema() {
        return List.of(
                addField("zaak_identificatie", TYPE_STRING),
                addCopyField("zaak_identificatie", "text", "text_exact"),
                addField("zaak_omschrijving", TYPE_TEXT_NL),
                addCopyField("zaak_omschrijving", "text"),
                addField("zaak_toelichting", TYPE_TEXT_NL),
                addCopyField("zaak_toelichting", "text"),
                addField("zaak_registratiedatum", TYPE_PDATE),
                addCopyField("zaak_registratiedatum", "created"),
                addField("zaak_startdatum", TYPE_PDATE),
                addCopyField("zaak_startdatum", "startdatum"),
                addField("zaak_einddatumGepland", TYPE_PDATE),
                addCopyField("zaak_einddatumGepland", "streefdatum"),
                addField("zaak_einddatum", TYPE_PDATE),
                addField("zaak_uiterlijkeEinddatumAfdoening", TYPE_PDATE),
                addField("zaak_publicatiedatum", TYPE_PDATE),
                addField("zaak_communicatiekanaal", TYPE_STRING, true),
                addCopyField("zaak_communicatiekanaal", "text"),
                addField("zaak_vertrouwelijkheidaanduiding", TYPE_STRING, true),
                addField("zaak_afgehandeld", TYPE_BOOLEAN, true),
                addField("zaak_groepId", TYPE_STRING),
                addField("zaak_groepNaam", TYPE_STRING, true),
                addCopyField("zaak_groepNaam", "text", "text_exact", "groepNaam"),
                addField("zaak_behandelaarNaam", TYPE_STRING, true),
                addCopyField("zaak_behandelaarNaam", "text", "text_exact", "behandelaarNaam"),
                addField("zaak_behandelaarGebruikersnaam", TYPE_STRING),
                addCopyField("zaak_behandelaarGebruikersnaam", "text"),
                addField("zaak_initiatorIdentificatie", TYPE_STRING),
                addCopyField("zaak_initiatorIdentificatie", "text", "text_exact"),
                addField("zaak_initiatorType", TYPE_STRING, true),
                addField("zaak_locatie", TYPE_LOCATION),
                addField("zaak_locatie_adres", TYPE_TEXT_NL),
                addField("zaak_redenOpschorting", TYPE_TEXT_NL),
                addCopyField("zaak_redenOpschorting", "text"),
                addField("zaak_redenVerlenging", TYPE_TEXT_NL),
                addCopyField("zaak_redenVerlenging", "text"),
                addField("zaak_duurVerlenging", TYPE_STRING, true),
                addField("zaak_zaaktypeUuid", TYPE_STRING),
                addCopyField("zaak_zaaktypeUuid", "zaaktypeUuid"),
                addField("zaak_zaaktypeIdentificatie", TYPE_STRING),
                addCopyField("zaak_zaaktypeIdentificatie", "text", "zaaktypeIdentificatie"),
                addField("zaak_zaaktypeOmschrijving", TYPE_STRING, true),
                addCopyField("zaak_zaaktypeOmschrijving", "text", "zaaktypeOmschrijving"),
                addField("zaak_statustypeOmschrijving", TYPE_STRING),
                addCopyField("zaak_statustypeOmschrijving", "text", "text_exact"),
                addField("zaak_statusDatumGezet", TYPE_PDATE),
                addField("zaak_statusToelichting", TYPE_TEXT_NL),
                addCopyField("zaak_statusToelichting", "text"),
                addField("zaak_statusEindstatus", TYPE_BOOLEAN, true),
                addField("zaak_resultaattypeOmschrijving", TYPE_STRING),
                addCopyField("zaak_resultaattypeOmschrijving", "text", "text_exact"),
                addField("zaak_resultaatToelichting", TYPE_TEXT_NL),
                addCopyField("zaak_resultaatToelichting", "text"),
                addField("zaak_aantalOpenstaandeTaken", TYPE_PINT),
                addFieldMultiValued("zaak_indicaties", TYPE_STRING, true),
                addField("zaak_indicaties_sort", TYPE_PLONG, true, false, true)
        );
    }

    private List<SchemaRequest.Update> createTaakSchema() {
        return List.of(
                addField("taak_naam", TYPE_STRING, true),
                addCopyField("taak_naam", "text"),
                addField("taak_toelichting", TYPE_TEXT_NL),
                addCopyField("taak_toelichting", "text"),
                addField("taak_status", TYPE_STRING, true),
                addCopyField("taak_status", "text", "text_exact"),
                addField("taak_zaaktypeUuid", TYPE_STRING),
                addCopyField("taak_zaaktypeUuid", "zaaktypeUuid"),
                addField("taak_zaaktypeIdentificatie", TYPE_STRING),
                addCopyField("taak_zaaktypeIdentificatie", "text", "zaaktypeIdentificatie"),
                addField("taak_zaaktypeOmschrijving", TYPE_STRING, true),
                addCopyField("taak_zaaktypeOmschrijving", "text", "text_exact", "zaaktypeOmschrijving"),
                addField("taak_zaakUuid", TYPE_STRING),
                addField("taak_zaakId", TYPE_STRING),
                addCopyField("taak_zaakId", "text_exact"),
                addField("taak_creatiedatum", TYPE_PDATE),
                addCopyField("taak_creatiedatum", "created", "startdatum"),
                addField("taak_toekenningsdatum", TYPE_PDATE),
                addField("taak_streefdatum", TYPE_PDATE),
                addCopyField("taak_streefdatum", "streefdatum"),
                addField("taak_groepId", TYPE_STRING),
                addField("taak_groepNaam", TYPE_STRING, true),
                addCopyField("taak_groepNaam", "text", "text_exact", "groepNaam"),
                addField("taak_behandelaarNaam", TYPE_STRING, true),
                addCopyField("taak_behandelaarNaam", "text", "text_exact", "behandelaarNaam"),
                addField("taak_behandelaarGebruikersnaam", TYPE_STRING),
                addCopyField("taak_behandelaarGebruikersnaam", "text"),
                addFieldMultiValued("taak_data", TYPE_STRING),
                addCopyField("taak_data", "text"),
                addFieldMultiValued("taak_informatie", TYPE_STRING)
        );
    }

    private List<SchemaRequest.Update> createInformatieobjectSchema() {
        return List.of(
                addField("informatieobject_identificatie", TYPE_STRING),
                addCopyField("informatieobject_identificatie", "text", "text_exact"),
                addField("informatieobject_titel", TYPE_TEXT_NL),
                addCopyField("informatieobject_titel", "text"),
                addField("informatieobject_titel_sort", TYPE_STRING, true, false),
                addCopyField("informatieobject_titel", "informatieobject_titel_sort"),
                addField("informatieobject_beschrijving", TYPE_TEXT_NL),
                addCopyField("informatieobject_beschrijving", "text"),
                addField("informatieobject_zaaktypeUuid", TYPE_STRING),
                addCopyField("informatieobject_zaaktypeUuid", "zaaktypeUuid"),
                addField("informatieobject_zaaktypeIdentificatie", TYPE_STRING),
                addCopyField("informatieobject_zaaktypeIdentificatie", "zaaktypeIdentificatie"),
                addField("informatieobject_zaaktypeOmschrijving", TYPE_STRING, true),
                addCopyField("informatieobject_zaaktypeOmschrijving", "zaaktypeOmschrijving"),
                addField("informatieobject_zaakId", TYPE_STRING),
                addCopyField("informatieobject_zaakId", "text_exact"),
                addField("informatieobject_zaakUuid", TYPE_STRING),
                addCopyField("informatieobject_zaakUuid", "text_exact"),
                addField("informatieobject_zaakAfgehandeld", TYPE_BOOLEAN, true),
                addField("informatieobject_zaakRelatie", TYPE_STRING),
                addField("informatieobject_creatiedatum", TYPE_PDATE),
                addField("informatieobject_vertrouwelijkheidaanduiding", TYPE_STRING, true),
                addField("informatieobject_auteur", TYPE_TEXT_NL),
                addCopyField("informatieobject_auteur", "text"),
                addField("informatieobject_auteur_sort", TYPE_STRING, true, false),
                addCopyField("informatieobject_auteur", "informatieobject_auteur_sort"),
                addField("informatieobject_status", TYPE_STRING, true),
                addCopyField("informatieobject_status", "text"),
                addField("informatieobject_formaat", TYPE_STRING, true),
                addField("informatieobject_versie", TYPE_PINT),
                addField("informatieobject_registratiedatum", TYPE_PDATE),
                addCopyField("informatieobject_registratiedatum", "created", "startdatum"),
                addField("informatieobject_bestandsnaam", TYPE_STRING),
                addCopyField("informatieobject_bestandsnaam", "text"),
                addField("informatieobject_bestandsomvang", TYPE_PLONG),
                addField("informatieobject_documentType", TYPE_STRING, true),
                addCopyField("informatieobject_documentType", "text"),
                addField("informatieobject_ontvangstdatum", TYPE_PDATE),
                addField("informatieobject_verzenddatum", TYPE_PDATE),
                addField("informatieobject_ondertekeningDatum", TYPE_PDATE),
                addField("informatieobject_ondertekeningSoort", TYPE_STRING),
                addField("informatieobject_inhoudUrl", TYPE_STRING),
                addField("informatieobject_vergrendeldDoorNaam", TYPE_STRING, true),
                addField("informatieobject_vergrendeldDoorGebruikersnaam", TYPE_STRING),
                addFieldMultiValued("informatieobject_indicaties", TYPE_STRING, true),
                addField("informatieobject_indicaties_sort", TYPE_PLONG, true, false, true)
        );
    }
}
