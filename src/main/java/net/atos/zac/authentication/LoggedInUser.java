/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.authentication;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

import java.util.List;
import java.util.Set;

import net.atos.zac.identity.model.User;

public class LoggedInUser extends User {

    private final Set<String> groupIds;

    private final Set<String> roles;

    public LoggedInUser(final String id, final String firstName, final String lastName, final String displayName, final String email,
            final List<String> groupIds, final List<String> roles) {
        super(id, firstName, lastName, displayName, email);
        this.groupIds = Set.copyOf(groupIds);
        this.roles = Set.copyOf(roles);
    }

    public Set<String> getGroupIds() {
        return groupIds;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public boolean isInAnyGroup() {
        return isNotEmpty(groupIds);
    }
}
