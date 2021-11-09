/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.notificaties;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Enumeratie die de acties bevat zoals die binnenkomen op de {@link NotificatieReceiver}.
 * <p>
 * http://open-zaak.default/ref/kanalen/
 */
public enum ActionEnum {

    CREATE("create"),
    UPDATE("update"),
    DELETE("destroy");

    private static final Logger LOG = Logger.getLogger(ActionEnum.class.getName());

    private final String code;

    private static final Map<String, ActionEnum> VALUES = new HashMap<>();

    static {
        for (final ActionEnum value : values()) {
            VALUES.put(value.code, value);
        }
    }

    ActionEnum(final String code) {
        this.code = code;
    }

    public static ActionEnum value(final String code) {
        final ActionEnum value = VALUES.get(code);
        if (value == null) {
            LOG.warning(String.format("unknown %s action", code));
        }
        return value;
    }
}
