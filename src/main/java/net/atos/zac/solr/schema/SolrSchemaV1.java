/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.solr.schema;

import static net.atos.zac.solr.FieldType.BOOLEAN;
import static net.atos.zac.solr.FieldType.LOCATION;
import static net.atos.zac.solr.FieldType.PDATE;
import static net.atos.zac.solr.FieldType.PDOUBLE;
import static net.atos.zac.solr.FieldType.PINT;
import static net.atos.zac.solr.FieldType.PLONG;
import static net.atos.zac.solr.FieldType.STRING;
import static net.atos.zac.solr.FieldType.TEXT_GENERAL_REV;
import static net.atos.zac.solr.FieldType.TEXT_NL;
import static net.atos.zac.solr.FieldType.TEXT_WS;
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
                addField("created", PDATE),
                addField("type", STRING),
                addField("timestamp", PDATE, "NOW"),
                addField("zaaktypeUuid", STRING),
                addField("zaaktypeIdentificatie", STRING),
                addField("zaaktypeOmschrijving", STRING, true),
                addField("behandelaarNaam", STRING, true),
                addField("groepNaam", STRING, true),
                addField("isToegekend", BOOLEAN, true),
                addField("startdatum", PDATE),
                addField("streefdatum", PDATE),
                addFieldMultiValued("text", TEXT_NL, true, false),
                addFieldMultiValued("text_exact", TEXT_WS, true, false),
                addFieldMultiValued("text_rev", TEXT_GENERAL_REV, true, false),
                addDynamicField("*_coordinate", PDOUBLE, true, false),
                addCopyField("id", "text", "text_exact")
        );
    }

    private List<SchemaRequest.Update> createZaakSchema() {
        return List.of(
                addField("zaak_identificatie", STRING),
                addCopyField("zaak_identificatie", "text", "text_exact"),
                addField("zaak_omschrijving", TEXT_NL),
                addCopyField("zaak_omschrijving", "text"),
                addField("zaak_toelichting", TEXT_NL),
                addCopyField("zaak_toelichting", "text"),
                addField("zaak_registratiedatum", PDATE),
                addCopyField("zaak_registratiedatum", "created"),
                addField("zaak_startdatum", PDATE),
                addCopyField("zaak_startdatum", "startdatum"),
                addField("zaak_einddatumGepland", PDATE),
                addCopyField("zaak_einddatumGepland", "streefdatum"),
                addField("zaak_einddatum", PDATE),
                addField("zaak_uiterlijkeEinddatumAfdoening", PDATE),
                addField("zaak_publicatiedatum", PDATE),
                addField("zaak_communicatiekanaal", STRING, true),
                addCopyField("zaak_communicatiekanaal", "text"),
                addField("zaak_vertrouwelijkheidaanduiding", STRING, true),
                addField("zaak_afgehandeld", BOOLEAN, true),
                addField("zaak_groepId", STRING),
                addField("zaak_groepNaam", STRING, true),
                addCopyField("zaak_groepNaam", "text", "text_exact", "groepNaam"),
                addField("zaak_behandelaarNaam", STRING, true),
                addCopyField("zaak_behandelaarNaam", "text", "text_exact", "behandelaarNaam"),
                addField("zaak_behandelaarGebruikersnaam", STRING),
                addCopyField("zaak_behandelaarGebruikersnaam", "text"),
                addField("zaak_initiatorIdentificatie", STRING),
                addCopyField("zaak_initiatorIdentificatie", "text", "text_exact"),
                addField("zaak_initiatorType", STRING, true),
                addField("zaak_locatie", LOCATION),
                addField("zaak_locatie_adres", TEXT_NL),
                addField("zaak_redenOpschorting", TEXT_NL),
                addCopyField("zaak_redenOpschorting", "text"),
                addField("zaak_redenVerlenging", TEXT_NL),
                addCopyField("zaak_redenVerlenging", "text"),
                addField("zaak_duurVerlenging", STRING, true),
                addField("zaak_zaaktypeUuid", STRING),
                addCopyField("zaak_zaaktypeUuid", "zaaktypeUuid"),
                addField("zaak_zaaktypeIdentificatie", STRING),
                addCopyField("zaak_zaaktypeIdentificatie", "text", "zaaktypeIdentificatie"),
                addField("zaak_zaaktypeOmschrijving", STRING, true),
                addCopyField("zaak_zaaktypeOmschrijving", "text", "zaaktypeOmschrijving"),
                addField("zaak_statustypeOmschrijving", STRING),
                addCopyField("zaak_statustypeOmschrijving", "text", "text_exact"),
                addField("zaak_statusDatumGezet", PDATE),
                addField("zaak_statusToelichting", TEXT_NL),
                addCopyField("zaak_statusToelichting", "text"),
                addField("zaak_statusEindstatus", BOOLEAN, true),
                addField("zaak_resultaattypeOmschrijving", STRING),
                addCopyField("zaak_resultaattypeOmschrijving", "text", "text_exact"),
                addField("zaak_resultaatToelichting", TEXT_NL),
                addCopyField("zaak_resultaatToelichting", "text"),
                addField("zaak_aantalOpenstaandeTaken", PINT),
                addFieldMultiValued("zaak_indicaties", STRING, true),
                addField("zaak_indicaties_sort", PLONG, true, false, true)
        );
    }

    private List<SchemaRequest.Update> createTaakSchema() {
        return List.of(
                addField("taak_naam", STRING, true),
                addCopyField("taak_naam", "text"),
                addField("taak_toelichting", TEXT_NL),
                addCopyField("taak_toelichting", "text"),
                addField("taak_status", STRING, true),
                addCopyField("taak_status", "text", "text_exact"),
                addField("taak_zaaktypeUuid", STRING),
                addCopyField("taak_zaaktypeUuid", "zaaktypeUuid"),
                addField("taak_zaaktypeIdentificatie", STRING),
                addCopyField("taak_zaaktypeIdentificatie", "text", "zaaktypeIdentificatie"),
                addField("taak_zaaktypeOmschrijving", STRING, true),
                addCopyField("taak_zaaktypeOmschrijving", "text", "text_exact", "zaaktypeOmschrijving"),
                addField("taak_zaakUuid", STRING),
                addField("taak_zaakId", STRING),
                addCopyField("taak_zaakId", "text_exact"),
                addField("taak_creatiedatum", PDATE),
                addCopyField("taak_creatiedatum", "created", "startdatum"),
                addField("taak_toekenningsdatum", PDATE),
                addField("taak_streefdatum", PDATE),
                addCopyField("taak_streefdatum", "streefdatum"),
                addField("taak_groepId", STRING),
                addField("taak_groepNaam", STRING, true),
                addCopyField("taak_groepNaam", "text", "text_exact", "groepNaam"),
                addField("taak_behandelaarNaam", STRING, true),
                addCopyField("taak_behandelaarNaam", "text", "text_exact", "behandelaarNaam"),
                addField("taak_behandelaarGebruikersnaam", STRING),
                addCopyField("taak_behandelaarGebruikersnaam", "text"),
                addFieldMultiValued("taak_data", STRING),
                addCopyField("taak_data", "text"),
                addFieldMultiValued("taak_informatie", STRING)
        );
    }

    private List<SchemaRequest.Update> createInformatieobjectSchema() {
        return List.of(
                addField("informatieobject_identificatie", STRING),
                addCopyField("informatieobject_identificatie", "text", "text_exact"),
                addField("informatieobject_titel", TEXT_NL),
                addCopyField("informatieobject_titel", "text"),
                addField("informatieobject_titel_sort", STRING, true, false),
                addCopyField("informatieobject_titel", "informatieobject_titel_sort"),
                addField("informatieobject_beschrijving", TEXT_NL),
                addCopyField("informatieobject_beschrijving", "text"),
                addField("informatieobject_zaaktypeUuid", STRING),
                addCopyField("informatieobject_zaaktypeUuid", "zaaktypeUuid"),
                addField("informatieobject_zaaktypeIdentificatie", STRING),
                addCopyField("informatieobject_zaaktypeIdentificatie", "zaaktypeIdentificatie"),
                addField("informatieobject_zaaktypeOmschrijving", STRING, true),
                addCopyField("informatieobject_zaaktypeOmschrijving", "zaaktypeOmschrijving"),
                addField("informatieobject_zaakId", STRING),
                addCopyField("informatieobject_zaakId", "text_exact"),
                addField("informatieobject_zaakUuid", STRING),
                addCopyField("informatieobject_zaakUuid", "text_exact"),
                addField("informatieobject_zaakAfgehandeld", BOOLEAN, true),
                addField("informatieobject_zaakRelatie", STRING),
                addField("informatieobject_creatiedatum", PDATE),
                addField("informatieobject_vertrouwelijkheidaanduiding", STRING, true),
                addField("informatieobject_auteur", TEXT_NL),
                addCopyField("informatieobject_auteur", "text"),
                addField("informatieobject_auteur_sort", STRING, true, false),
                addCopyField("informatieobject_auteur", "informatieobject_auteur_sort"),
                addField("informatieobject_status", STRING, true),
                addCopyField("informatieobject_status", "text"),
                addField("informatieobject_formaat", STRING, true),
                addField("informatieobject_versie", PINT),
                addField("informatieobject_registratiedatum", PDATE),
                addCopyField("informatieobject_registratiedatum", "created", "startdatum"),
                addField("informatieobject_bestandsnaam", STRING),
                addCopyField("informatieobject_bestandsnaam", "text"),
                addField("informatieobject_bestandsomvang", PLONG),
                addField("informatieobject_documentType", STRING, true),
                addCopyField("informatieobject_documentType", "text"),
                addField("informatieobject_ontvangstdatum", PDATE),
                addField("informatieobject_verzenddatum", PDATE),
                addField("informatieobject_ondertekeningDatum", PDATE),
                addField("informatieobject_ondertekeningSoort", STRING),
                addField("informatieobject_inhoudUrl", STRING),
                addField("informatieobject_vergrendeldDoorNaam", STRING, true),
                addField("informatieobject_vergrendeldDoorGebruikersnaam", STRING),
                addFieldMultiValued("informatieobject_indicaties", STRING, true),
                addField("informatieobject_indicaties_sort", PLONG, true, false, true)
        );
    }
}
