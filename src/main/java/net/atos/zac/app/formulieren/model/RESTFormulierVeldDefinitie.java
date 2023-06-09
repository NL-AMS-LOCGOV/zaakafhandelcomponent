/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.formulieren.model;

import java.util.List;

import net.atos.zac.formulieren.model.FormulierVeldType;

public class RESTFormulierVeldDefinitie {

    public Long id;

    public String systeemnaam;

    public int volgorde;

    public String label;

    public FormulierVeldType type;

    public String beschrijving;

    public String helptekst;

    public boolean verplicht;

    public String defaultWaarde;

    public List<String> meerkeuzeWaarden;

    public List<String> validaties;

}
