/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable.idm;

import java.util.List;

import javax.inject.Inject;

import org.flowable.idm.api.Group;
import org.flowable.idm.api.IdmIdentityService;
import org.flowable.idm.api.User;

public class IdmService {

    @Inject
    private IdmIdentityService idmIdentityService;

    public List<Group> getGroups() {
        return idmIdentityService.createGroupQuery()
                .orderByGroupName()
                .asc()
                .list();
    }

    public Group getGroup(final String groupId) {
        return idmIdentityService.createGroupQuery()
                .groupId(groupId)
                .singleResult();
    }

    public User getUser(final String userId) {
        return idmIdentityService.createUserQuery()
                .userId(userId)
                .singleResult();
    }

    public List<Group> getUserGroups(final String userId) {
        return idmIdentityService.createGroupQuery()
                .groupMember(userId)
                .list();
    }

    public List<User> getUsersInGroup(final String groepId) {
        return idmIdentityService.createUserQuery()
                .memberOfGroup(groepId)
                .list();
    }
}
