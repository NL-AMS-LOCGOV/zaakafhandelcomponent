/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.authentication;

import java.util.List;
import java.util.stream.Collectors;

import org.flowable.idm.api.Group;
import org.flowable.idm.api.User;

import net.atos.zac.app.identity.converter.RESTMedewerkerConverter;

public class Medewerker {

    private final User user;

    private final List<Group> groups;

    public Medewerker(final User user, final List<Group> groups) {
        this.user = user;
        this.groups = groups;
    }

    public String getGebruikersnaam() {
        return user.getId();
    }

    public String getNaam() {
        return RESTMedewerkerConverter.convertToNaam(user);
    }

    public List<String> getGroupIds() {
        return groups.stream().map(Group::getId).collect(Collectors.toList());
    }

    public boolean isUser(final String userId) {
        return user.getId().equals(userId);
    }

    public boolean isInAnyGroup() {
        return !groups.isEmpty();
    }

    public boolean isInGroup(final String groupId) {
        return groups.stream()
                .anyMatch(group -> group.getId().equals(groupId));
    }

    public List<Group> getGroups() {
        return groups;
    }
}
