/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.util;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

public final class JsonbUtil {

    public static final Jsonb JSONB = JsonbBuilder.create();

    private JsonbUtil() {}
}
