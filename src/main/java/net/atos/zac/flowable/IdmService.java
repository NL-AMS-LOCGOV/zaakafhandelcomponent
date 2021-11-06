/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.flowable.idm.api.Group;
import org.flowable.idm.api.IdmIdentityService;
import org.flowable.idm.api.User;

@Stateless
public class IdmService {

    @Inject
    private IdmIdentityService idmIdentityService;

    public List<Group> listGroups() {
        return idmIdentityService.createGroupQuery()
                .orderByGroupName()
                .asc()
                .list();
    }

    public Group findGroup(final String groupId) {
        return idmIdentityService.createGroupQuery()
                .groupId(groupId)
                .singleResult();
    }

    public User findUser(final String userId) {
        return idmIdentityService.createUserQuery()
                .userId(userId)
                .singleResult();
    }

    public List<Group> listGroupsForUser(final String userId) {
        return idmIdentityService.createGroupQuery()
                .groupMember(userId)
                .list();
    }

    public List<User> listUsersInGroup(final String groepId) {
        return idmIdentityService.createUserQuery()
                .memberOfGroup(groepId)
                .list();
    }
}
