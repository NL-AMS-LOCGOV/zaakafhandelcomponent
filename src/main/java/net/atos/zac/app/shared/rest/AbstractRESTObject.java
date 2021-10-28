/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.shared.rest;

import java.util.Map;

import net.atos.zac.app.rechten.RechtOperatie;

public abstract class AbstractRESTObject {
    public Map<RechtOperatie, Boolean> rechten;
}
