/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.notificaties;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.annotation.JsonbTypeAdapter;

import org.apache.commons.lang3.NotImplementedException;

/**
 * Enumeratie die de resources bevat zoals die binnenkomen op de {@link NotificatieReceiver}.
 * <p>
 * http://open-zaak.default/ref/kanalen/
 */
@JsonbTypeAdapter(Resource.Adapter.class)
public enum Resource {

    APPLICATIE("applicatie"),
    BESLUIT("besluit"),
    BESLUITINFORMATIEOBJECT("besluitinformatieobject"),
    BESLUITTYPE("besluittype"),
    GEBRUIKSRECHTEN("gebruiksrechten"),
    INFORMATIEOBJECT("enkelvoudiginformatieobject"),
    INFORMATIEOBJECTTYPE("informatieobjecttype"),
    KLANTCONTACT("klantcontact"),
    OBJECT("object"),
    RESULTAAT("resultaat"),
    ROL("rol"),
    STATUS("status"),
    ZAAK("zaak"),
    ZAAKBESLUIT("zaakbesluit"),
    ZAAKOBJECT("zaakobject"),
    ZAAKEIGENSCHAP("zaakeigenschap"),
    ZAAKINFORMATIEOBJECT("zaakinformatieobject"),
    ZAAKTYPE("zaaktype");

    private static final Logger LOG = Logger.getLogger(Resource.class.getName());

    private final String code;

    private static final Map<String, Resource> VALUES = new HashMap<>();

    static {
        for (final Resource value : values()) {
            VALUES.put(value.code, value);
        }
    }

    Resource(final String code) {
        this.code = code;
    }

    public static Resource fromCode(final String code) {
        final Resource value = VALUES.get(code);
        if (value == null) {
            LOG.warning(String.format("unknown %s resource", code));
        }
        return value;
    }

    static class Adapter implements JsonbAdapter<Resource, String> {

        @Override
        public String adaptToJson(final Resource resource) {
            throw new NotImplementedException();
        }

        @Override
        public Resource adaptFromJson(final String code) {
            return fromCode(code);
        }
    }
}
