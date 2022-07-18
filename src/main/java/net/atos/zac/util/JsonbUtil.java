/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.util;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyVisibilityStrategy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class JsonbUtil {

    public static final PropertyVisibilityStrategy visibilityStrategy = new PropertyVisibilityStrategy() {
        @Override
        public boolean isVisible(Field field) {
            return true;
        }

        @Override
        public boolean isVisible(Method method) {
            return false;
        }
    };

    public static final Jsonb JSONB = JsonbBuilder.create();

    public static final Jsonb FIELD_VISIBILITY_STRATEGY =
            JsonbBuilder.create(new JsonbConfig().withPropertyVisibilityStrategy(JsonbUtil.visibilityStrategy));

    private JsonbUtil() {}
}
