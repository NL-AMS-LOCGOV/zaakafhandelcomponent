/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.formulieren.model;

import java.time.ZonedDateTime;
import java.util.List;

public class RESTFormulierDefinitie {

    public Long id;

    public String systeemnaam;

    public String naam;

    public String beschrijving;

    public String uitleg;

    public ZonedDateTime creatiedatum;

    public ZonedDateTime wijzigingsdatum;

    public List<RESTFormulierVeldDefinitie> veldDefinities;
}
