/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.gebruikersvoorkeuren.model;

import net.atos.zac.gebruikersvoorkeuren.model.DashboardCardId;
import net.atos.zac.signalering.model.SignaleringType;

public class RESTDashboardCardInstelling {

    public Long id;

    public DashboardCardId cardId;

    public SignaleringType.Type signaleringType;

    public int column;

    public int row;
}
