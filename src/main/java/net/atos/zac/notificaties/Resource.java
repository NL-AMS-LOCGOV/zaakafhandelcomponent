/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.notificaties;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Enumeratie die de resources bevat zoals die binnenkomen op de {@link NotificatieReceiver}.
 * <p>
 * http://open-zaak.default/ref/kanalen/
 */
public enum Resource {

    APPLICATIE("applicatie"),
    BESLUIT("besluit"),
    BESLUITINFORMATIEOBJECT("besluitinformatieobject"),
    BESLUITTYPE("besluittype"),
    GEBRUIKSRECHTEN("gebruiksrechten"),
    INFORMATIEOBJECT("enkelvoudiginformatieobject"),
    INFORMATIEOBJECTTYPE("informatieobjecttype"),
    KLANTCONTACT("klantcontact"),
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

    public static Resource value(final String code) {
        final Resource value = VALUES.get(code);
        if (value == null) {
            LOG.warning(String.format("unknown %s resource", code));
        }
        return value;
    }
}
