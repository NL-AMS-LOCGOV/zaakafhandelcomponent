/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.model;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import net.atos.zac.app.planitems.model.PlanItemType;

public class RESTCaseModel {
    public String naam;

    public String key;

    public List<RESTPlanItemDefinitie> planItemDefinities;

    public RESTCaseModel(final String naam, final String key) {
        this.naam = naam;
        this.key = key;
    }

    public List<RESTPlanItemDefinitie> getHumanTaskPlanItemDefinities() {
        if (CollectionUtils.isEmpty(planItemDefinities)) {
            return Collections.emptyList();
        }
        return planItemDefinities.stream().filter(planItemDefinition -> PlanItemType.HUMAN_TASK == planItemDefinition.type).collect(Collectors.toList());
    }
}
