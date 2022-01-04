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

public class RESTCaseDefinition {
    public String naam;

    public String key;

    public List<RESTPlanItemDefinition> planItemDefinitions;

    public RESTCaseDefinition() {
    }

    public RESTCaseDefinition(final String naam, final String key) {
        this.naam = naam;
        this.key = key;
    }

    public List<RESTPlanItemDefinition> getHumanTaskPlanItemDefinitions() {
        if (CollectionUtils.isEmpty(planItemDefinitions)) {
            return Collections.emptyList();
        }
        return planItemDefinitions.stream().filter(planItemDefinition -> PlanItemType.HUMAN_TASK == planItemDefinition.type).collect(Collectors.toList());
    }
}
