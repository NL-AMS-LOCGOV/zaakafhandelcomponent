/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.model;

import java.util.ArrayList;
import java.util.List;

import net.atos.zac.app.identity.model.RESTGroup;

public class RESTHumanTaskParameters {

    public Long id;

    public RESTPlanItemDefinition planItemDefinition;

    public Integer doorlooptijd;

    public RESTGroup defaultGroep;

    public List<RESTHumanTaskReferentieTabel> referentieTabellen = new ArrayList<>();
}
