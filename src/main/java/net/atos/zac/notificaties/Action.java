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
public enum Action {

    CREATE("create"),
    UPDATE("update"),
    DELETE("destroy");

    private static final Logger LOG = Logger.getLogger(Action.class.getName());

    private final String code;

    private static final Map<String, Action> VALUES = new HashMap<>();

    static {
        for (final Action value : values()) {
            VALUES.put(value.code, value);
        }
    }

    Action(final String code) {
        this.code = code;
    }

    public static Action value(final String code) {
        final Action value = VALUES.get(code);
        if (value == null) {
            LOG.warning(String.format("unknown %s action", code));
        }
        return value;
    }
}
