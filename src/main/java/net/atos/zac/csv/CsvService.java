/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.csv;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.lang3.StringUtils;

import com.opencsv.CSVWriter;

import net.atos.zac.zoeken.model.ZoekObject;
import net.atos.zac.zoeken.model.ZoekResultaat;

public class CsvService {

    private final static char SEPARATOR = ';';

    private final static char QUOTE_ESCAPE_CHAR = '"';

    private final static String LINE_END = "\n";

    private final static List<String> uitzonderingen = List.of("class", "uuid", "zaaktypeUuid", "type",
                                                       "zaakUUID", "taakData", "taakInformatie", "id",
                                                               "zaaktypeIdentificatie");

    public StreamingOutput exportToCsv(final ZoekResultaat<? extends ZoekObject> zoekResultaat) {
        final AtomicInteger headerCounter = new AtomicInteger();
        final List<String> headers = new ArrayList<>();
        final List<String[]> records = new ArrayList<>();
        zoekResultaat.getItems()
                .forEach(zoekObject -> {
                    try {
                        final List<String> record = new ArrayList<>();
                        final BeanInfo beanInfo = Introspector.getBeanInfo(zoekObject.getClass());
                        for (final PropertyDescriptor property : beanInfo.getPropertyDescriptors()) {
                            if (uitzonderingen.contains(property.getName()) || property.getReadMethod() == null) {
                                continue;
                            }

                            final Method getter = property.getReadMethod();
                            if (headerCounter.get() < beanInfo.getPropertyDescriptors().length) {
                                headerCounter.getAndIncrement();
                                headers.add(property.getDisplayName());
                            }

                            if (getter != null) {
                                final Object value = getter.invoke(zoekObject);

                                if (value instanceof Boolean) {
                                    record.add((Boolean) value ? "Ja" : "Nee");
                                } else {
                                    record.add(value != null ? value.toString() : StringUtils.EMPTY);
                                }
                            }
                        }
                        records.add(record.toArray(new String[0]));
                    } catch (final IntrospectionException | InvocationTargetException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });

        return outputStream -> {
            try (final CSVWriter writer = new CSVWriter(new OutputStreamWriter(outputStream), SEPARATOR,
                                                        QUOTE_ESCAPE_CHAR, QUOTE_ESCAPE_CHAR, LINE_END)) {
                writer.writeNext(headers.toArray(new String[0]));
                writer.writeAll(records);
            }
            outputStream.flush();
            outputStream.close();
        };
    }
}
