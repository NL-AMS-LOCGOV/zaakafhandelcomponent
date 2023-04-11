/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.brp.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.json.bind.config.PropertyVisibilityStrategy;

public class FieldPropertyVisibilityStrategy implements PropertyVisibilityStrategy {

    @Override
    public boolean isVisible(final Field field) {
        return true;
    }

    @Override
    public boolean isVisible(final Method method) {
        return false;
    }
}
