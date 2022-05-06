/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.identity.converter;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import net.atos.zac.app.identity.model.RESTGroep;
import net.atos.zac.identity.IdentityService;
import net.atos.zac.identity.model.Group;

public class RESTGroepConverter {

    @Inject
    private IdentityService identityService;

    public List<RESTGroep> convertGroups(final List<Group> groups) {
        return groups.stream()
                .map(this::convertGroup)
                .collect(Collectors.toList());
    }

    public RESTGroep convertGroup(final Group group) {
        if (group != null) {
            final RESTGroep restGroep = new RESTGroep();
            restGroep.id = group.getId();
            restGroep.naam = group.getName();
            return restGroep;
        } else {
            return null;
        }
    }

    public RESTGroep convertGroupId(final String groupId) {
        if (StringUtils.isNotEmpty(groupId)) {
            final Group group = identityService.readGroup(groupId);
            final RESTGroep restGroep = convertGroup(group);
            return restGroep;
        }
        return null;
    }
}
