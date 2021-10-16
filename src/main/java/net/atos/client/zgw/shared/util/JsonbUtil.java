/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyVisibilityStrategy;

public final class JsonbUtil {

    public static final PropertyVisibilityStrategy PROPERTY_VISIBILITY_STRATEGY = new PropertyVisibilityStrategy() {

        @Override
        public boolean isVisible(final Field field) {
            return true;
        }

        @Override
        public boolean isVisible(final Method method) {
            return false;
        }
    };

    public static final Jsonb JSONB = JsonbBuilder.create(new JsonbConfig().withPropertyVisibilityStrategy(PROPERTY_VISIBILITY_STRATEGY));

    private JsonbUtil() {
    }
}
