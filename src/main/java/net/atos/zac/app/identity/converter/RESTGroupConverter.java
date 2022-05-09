/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.identity.converter;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import net.atos.zac.app.identity.model.RESTGroup;
import net.atos.zac.identity.IdentityService;
import net.atos.zac.identity.model.Group;

public class RESTGroupConverter {

    @Inject
    private IdentityService identityService;

    public List<RESTGroup> convertGroups(final List<Group> groups) {
        return groups.stream()
                .map(this::convertGroup)
                .collect(Collectors.toList());
    }

    public RESTGroup convertGroup(final Group group) {
        if (group != null) {
            final RESTGroup restGroup = new RESTGroup();
            restGroup.id = group.getId();
            restGroup.naam = group.getName();
            return restGroup;
        } else {
            return null;
        }
    }

    public RESTGroup convertGroupId(final String groupId) {
        if (StringUtils.isNotEmpty(groupId)) {
            final Group group = identityService.readGroup(groupId);
            final RESTGroup restGroup = convertGroup(group);
            return restGroup;
        }
        return null;
    }
}
