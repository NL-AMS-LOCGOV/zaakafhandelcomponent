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
 * Enumeratie die de acties bevat zoals die binnenkomen op de {@link NotificatieReceiver}.
 * <p>
 * http://open-zaak.default/ref/kanalen/
 */
@JsonbTypeAdapter(Action.Adapter.class)
public enum Action {

    CREATE("create"),
    READ("read", "list"),
    UPDATE("update", "partial_update"),
    DELETE("destroy");

    private static final Logger LOG = Logger.getLogger(Action.class.getName());

    private final String code;

    private final String alt;

    private static final Map<String, Action> VALUES = new HashMap<>();

    static {
        for (final Action value : values()) {
            VALUES.put(value.code, value);
            if (value.alt != null) {
                VALUES.put(value.alt, value);
            }
        }
    }

    Action(final String code, final String alt) {
        this.code = code;
        this.alt = alt;
    }

    Action(final String code) {
        this(code, null);
    }

    public static Action fromCode(final String code) {
        final Action value = VALUES.get(code);
        if (value == null) {
            LOG.warning(String.format("unknown %s action", code));
        }
        return value;
    }

    static class Adapter implements JsonbAdapter<Action, String> {

        @Override
        public String adaptToJson(final Action action) {
            throw new NotImplementedException();
        }

        @Override
        public Action adaptFromJson(final String code) {
            return fromCode(code);
        }
    }
}
