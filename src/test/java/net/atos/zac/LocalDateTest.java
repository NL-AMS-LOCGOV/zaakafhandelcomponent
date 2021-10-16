/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.json.bind.Jsonb;

import org.junit.jupiter.api.Test;

import net.atos.zac.app.JsonbContextResolver;

/**
 * Datums worden NIET aangepast naar Locale tijdzone (Europe/Amsterdam), dus 23-06-2020 blijft 23-06-2020, ongeacht de tijdzone
 * Wanneer je deze test aanpast moet je ook angular test datum.pipe.spec.ts aanpassen
 */
public class LocalDateTest {

    private static final Jsonb contextResolver = new JsonbContextResolver().getContext(LocalDateTest.class);

    @Test
    public void testFromJsonDatumTijd_ISO_Z() {
        String s = "\"2021-06-23T00:00:00Z\"";
        LocalDate datum = contextResolver.fromJson(s, LocalDate.class);
        assertNotNull(datum);
        assertEquals("2021-06-23", datum.format(DateTimeFormatter.ISO_DATE));
    }

    @Test
    public void testFromJsonDatumTijd_ISO_Offset_Plus_2() {
        String s = "\"2021-06-23T00:00:00+02:00\"";
        LocalDate datum = contextResolver.fromJson(s, LocalDate.class);
        assertNotNull(datum);
        assertEquals("2021-06-23", datum.format(DateTimeFormatter.ISO_DATE));
    }

    @Test
    public void testFromJsonDatumTijd_ISO_Offset_Min_2() {
        String s = "\"2021-06-23T00:00:00-02:00\"";
        LocalDate datum = contextResolver.fromJson(s, LocalDate.class);
        assertNotNull(datum);
        assertEquals("2021-06-23", datum.format(DateTimeFormatter.ISO_DATE));
    }

    @Test
    public void testFromJsonDatumTijd_ISO_Amsterdamse_Tijd() {
        String s = "\"1939-04-01T00:00:00+00:20\"";
        LocalDate datum = contextResolver.fromJson(s, LocalDate.class);
        assertNotNull(datum);
        assertEquals("1939-04-01", datum.format(DateTimeFormatter.ISO_DATE));
    }

    @Test
    public void testToJsonDatumTijd_ISO_Z() {
        LocalDate localDate = LocalDate.parse("2021-06-23");
        assertEquals("\"2021-06-23\"", contextResolver.toJson(localDate));
    }

    @Test
    public void testToJsonDatumTijd_ISO_Amsterdamse_Tijd() {
        LocalDate localDate = LocalDate.parse("1939-04-01");
        assertEquals("\"1939-04-01\"", contextResolver.toJson(localDate));
    }
}
