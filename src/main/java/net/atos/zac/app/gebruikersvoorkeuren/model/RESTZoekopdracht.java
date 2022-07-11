/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.gebruikersvoorkeuren.model;

import java.time.LocalDate;

import net.atos.zac.gebruikersvoorkeuren.model.Werklijst;

public class RESTZoekopdracht {

    public Long id;

    public Werklijst lijstID;

    public boolean actief;

    public LocalDate creatiedatum;

    public String naam;

    public String json;
}
