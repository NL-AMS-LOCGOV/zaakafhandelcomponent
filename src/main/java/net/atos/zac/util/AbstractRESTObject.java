/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.util;

import java.util.Map;

import net.atos.zac.rechten.RechtOperatie;

public abstract class AbstractRESTObject {
    public Map<RechtOperatie, Boolean> rechten;
}
