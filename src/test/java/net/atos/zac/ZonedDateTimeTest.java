/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.json.bind.Jsonb;

import org.junit.jupiter.api.Test;
import org.wildfly.common.Assert;

import net.atos.zac.config.JsonbContextResolver;

/**
 * Datums worden niet aangepast naar locale zone.
 * Wanneer je deze test aanpast moet je ook angular test datum.pipe.spec.ts aanpassen
 */
public class ZonedDateTimeTest {

    private static final Jsonb contextResolver = new JsonbContextResolver().getContext(ZonedDateTimeTest.class);

    @Test
    public void testFromJsonDatumTijd_ISO_Z() {
        String s = "\"2021-06-23T00:00:00Z\"";
        ZonedDateTime datumTijd = contextResolver.fromJson(s, ZonedDateTime.class);
        Assert.assertNotNull(datumTijd);
        assertEquals("2021-06-23T00:00:00Z", datumTijd.format(DateTimeFormatter.ISO_DATE_TIME));
    }

    @Test
    public void testFromJsonDatumTijd_ISO_Offset_Plus_2() {
        String s = "\"2021-06-23T00:00:00+02:00\"";
        ZonedDateTime datum = contextResolver.fromJson(s, ZonedDateTime.class);
        Assert.assertNotNull(datum);
        assertEquals("2021-06-23T00:00:00+02:00", datum.format(DateTimeFormatter.ISO_DATE_TIME));
    }

    @Test
    public void testFromJsonDatumTijd_ISO_Offset_Min_2() {
        String s = "\"2021-06-23T00:00:00-02:00\"";
        ZonedDateTime datum = contextResolver.fromJson(s, ZonedDateTime.class);
        Assert.assertNotNull(datum);
        assertEquals("2021-06-23T00:00:00-02:00", datum.format(DateTimeFormatter.ISO_DATE_TIME));
    }

    @Test
    public void testFromJsonDatumTijd_ISO_Amsterdamse_Tijd() {
        String s = "\"1939-04-01T00:00:00+00:20\"";
        ZonedDateTime datum = contextResolver.fromJson(s, ZonedDateTime.class);
        Assert.assertNotNull(datum);
        assertEquals("1939-04-01T00:00:00+00:20", datum.format(DateTimeFormatter.ISO_DATE_TIME));
    }

    @Test
    public void testToJsonDatumTijd_ISO_Z() {
        ZonedDateTime datumTijd = ZonedDateTime.parse("2021-06-23T00:00:00Z");
        String json = contextResolver.toJson(datumTijd);
        assertEquals("\"2021-06-23T00:00:00Z\"", json);
    }

    @Test
    public void testToJsonDatumTijd_ISO_Offset_Plus_2() {
        ZonedDateTime datumTijd = ZonedDateTime.parse("2021-06-23T00:00:00+02:00");
        String json = contextResolver.toJson(datumTijd);
        assertEquals("\"2021-06-22T22:00:00Z\"", json);
    }

    @Test
    public void testToJsonDatumTijd_ISO_Offset_Min_2() {
        ZonedDateTime datumTijd = ZonedDateTime.parse("2021-06-23T00:00:00-02:00");
        String json = contextResolver.toJson(datumTijd, ZonedDateTime.class);
        assertEquals("\"2021-06-23T02:00:00Z\"", json);
    }

    @Test
    public void testToJsonDatumTijd_ISO_Amsterdamse_Tijd() {
        ZonedDateTime datumTijd = ZonedDateTime.parse("1939-04-01T00:00:00+00:20");
        String json = contextResolver.toJson(datumTijd, ZonedDateTime.class);
        assertEquals("\"1939-03-31T23:40:00Z\"", json);
    }
}
