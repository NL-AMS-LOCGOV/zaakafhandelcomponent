/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.model;

import java.util.ArrayList;
import java.util.List;

public class RESTHumanTaskParameters {

    public boolean actief;

    public Long id;

    public RESTPlanItemDefinition planItemDefinition;

    public Integer doorlooptijd;

    public String defaultGroepId;

    public String formulierDefinitieId;

    public List<RESTHumanTaskReferentieTabel> referentieTabellen = new ArrayList<>();
}
