/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.informatieobjecten;

import net.atos.client.zgw.drc.DRCClientService;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.ZaakInformatieobject;

import javax.inject.Inject;
import javax.ws.rs.core.StreamingOutput;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class EnkelvoudigInformatieObjectDownloadService {

    @Inject
    private DRCClientService drcClientService;

    @Inject
    private ZRCClientService zrcClientService;

    private static final String ZAAK_ONTKOPPELD = "ontkoppeld";
    private static final String RICHTING_INKOMEND = "inkomend";
    private static final String RICHTING_UITGAAND = "uitgaand";
    private static final String RICHTING_INTERN = "intern";
    private static final String SAMENVATTING_BESTANDSNAAM = "samenvatting.txt";

    /**
     * Retourneer {@link StreamingOutput} zip-bestand met informatieobjecten en samenvatting
     *
     * @param uuids {@link UUID}s van op te halen informatieobjecten
     * @return het zip-bestand
     */
    public StreamingOutput getZipFileFor(final List<UUID> uuids) {
        return outputStream -> {
            try (ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(outputStream))) {
                Map<String, Map<String, List<String>>> samenvatting = new HashMap<>();
                uuids.forEach(uuid -> {
                    String pad = addInformatieObjectToZip(uuid, zipOutputStream);
                    addInformatieObjectToSummary(pad, samenvatting);
                });
                addSamenvattingToZip(samenvatting, zipOutputStream);
                zipOutputStream.finish();
            }
            outputStream.flush();
            outputStream.close();
        };
    }

    /**
     * Voeg een informatieobject toe aan het zip-bestand
     *
     * @param uuid {@link UUID} van het informatieobject
     * @param zipOutputStream {@link ZipOutputStream} van het te updaten zip-bestand
     * @return {@link String} pad naar het toegevoegde bestand in het zip-bestand
     */
    private String addInformatieObjectToZip(final UUID uuid, ZipOutputStream zipOutputStream) {
        final String path = getInformatieObjectZipPath(uuid);
        final ZipEntry zipEntry = new ZipEntry(path);
        try {
            zipOutputStream.putNextEntry(zipEntry);
            zipOutputStream.write(getInformatieObjectInhoud(uuid));
            zipOutputStream.closeEntry();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return path;
    }

    /**
     * Retourneer binary inhoud van het informatieobject
     *
     * @param uuid {@link UUID} van het informatieobject
     * @return binary inhoud van het informatieobject
     */
    private byte[] getInformatieObjectInhoud(final UUID uuid) {
        final ByteArrayInputStream inhoud = drcClientService.downloadEnkelvoudigInformatieobject(uuid);
        return inhoud.readAllBytes();
    }

    /**
     * Retourneer pad in mappenstructuur voor een informatieobject in het zip-bestand
     *
     * @param uuid {@link UUID} van het informatieobject
     * @return {@link String} pad naar het informatieobject
     */
    private String getInformatieObjectZipPath(final UUID uuid) {
        final EnkelvoudigInformatieobject enkelvoudigInformatieobject = drcClientService.readEnkelvoudigInformatieobject(uuid);
        final List<ZaakInformatieobject> zaakInformatieObjectenList = zrcClientService.listZaakinformatieobjecten(enkelvoudigInformatieobject);
        String zaakId = ZAAK_ONTKOPPELD;
        if(zaakInformatieObjectenList.size() > 0) {
            final URI zaakUri = zaakInformatieObjectenList.get(0).getZaak();
            zaakId = zrcClientService.readZaak(zaakUri).getIdentificatie();
        }
        final String subfolder = enkelvoudigInformatieobject.getOntvangstdatum() != null ?
                RICHTING_INKOMEND :
                enkelvoudigInformatieobject.getVerzenddatum() != null ?
                        RICHTING_UITGAAND :
                        RICHTING_INTERN;
        final String[] bestandsnaamExtensie = enkelvoudigInformatieobject.getBestandsnaam().split("\\.");
        return String.format("%s/%s/%s-%s.%s",
                                          zaakId,
                                          subfolder,
                                          bestandsnaamExtensie[0],
                                          enkelvoudigInformatieobject.getIdentificatie(),
                                          bestandsnaamExtensie[1]);
    }

    /**
     * Voeg een informatieobject toe aan de samenvatting
     *
     * @param pad {@link String} pad naar het informatieobject
     * @param samenvatting {@link Map} samenvatting van het zip-bestand
     */
    private void addInformatieObjectToSummary(final String pad, Map<String, Map<String, List<String>>> samenvatting) {
        final String[] padParts = pad.split("/");
        final String zaakId = padParts[0];
        final String richting = padParts[1];
        final String bestandsnaam = padParts[2];

        // Voeg zaak toe aan samenvatting als deze nog niet bestaat
        if(!samenvatting.containsKey(zaakId)) {
            samenvatting.put(zaakId, new HashMap<>());
        }

        // Voeg richting toe aan samenvatting als deze nog niet bestaat
        final Map<String, List<String>> zaakSamenvatting = samenvatting.get(zaakId);
        if(!zaakSamenvatting.containsKey(richting)) {
            zaakSamenvatting.put(richting, new ArrayList<>());
        }

        // Voeg informatieobject toe
        zaakSamenvatting.get(richting).add(bestandsnaam);
    }

    /**
     * Voeg de samenvatting toe aan het zip-bestand
     *
     * @param samenvatting {@link Map} samenvatting van bestanden in het zip-bestand
     * @param zipOutputStream {@link ZipOutputStream} van het te updaten zip-bestand
     */
    private void addSamenvattingToZip(Map<String, Map<String, List<String>>> samenvatting, ZipOutputStream zipOutputStream) {
        final ZipEntry zipEntry = new ZipEntry(SAMENVATTING_BESTANDSNAAM);
        final StringBuilder stringBuilder = new StringBuilder();
        for(String zaak : samenvatting.keySet()) {
            stringBuilder.append(zaak);
            stringBuilder.append(":\n");
            final Map<String, List<String>> zaakRichtingen = samenvatting.get(zaak);
            for(String richting : zaakRichtingen.keySet()) {
                stringBuilder.append('\t');
                stringBuilder.append(richting);
                stringBuilder.append(":\n");
                for(String bestand : zaakRichtingen.get(richting)) {
                    stringBuilder.append("\t  - ");
                    stringBuilder.append(bestand);
                    stringBuilder.append("\n");
                }
            }
            stringBuilder.append('\n');
        }
        final byte[] samenvattingInhoud = stringBuilder.toString().getBytes();
        try {
            zipOutputStream.putNextEntry(zipEntry);
            zipOutputStream.write(samenvattingInhoud);
            zipOutputStream.closeEntry();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
