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
public enum ResourceEnum {

    APPLICATIE("applicatie"),
    BESLUIT("besluit"),
    BESLUITTYPE("besluittype"),
    INFORMATIEOBJECT("enkelvoudiginformatieobject"),
    INFORMATIEOBJECTTYPE("informatieobjecttype"),
    ZAAKTYPE("zaaktype"),
    ZAAK("zaak");

    private static final Logger LOG = Logger.getLogger(ResourceEnum.class.getName());

    private final String code;

    private static final Map<String, ResourceEnum> VALUES = new HashMap<>();

    static {
        for (final ResourceEnum value : values()) {
            VALUES.put(value.code, value);
        }
    }

    ResourceEnum(final String code) {
        this.code = code;
    }

    public static ResourceEnum value(final String code) {
        final ResourceEnum value = VALUES.get(code);
        if (value == null) {
            LOG.warning(String.format("unknown %s resource", code));
        }
        return value;
    }
}
