/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.policy.input;

import javax.json.bind.annotation.JsonbProperty;

import net.atos.zac.authentication.LoggedInUser;

public class UserInput {

    @JsonbProperty("user")
    private final UserData userData = new UserData();

    public UserInput(final LoggedInUser loggedInUser) {
        userData.id = loggedInUser.getId();
        userData.roles = loggedInUser.getRoles();
    }

    public UserData getUser() {
        return userData;
    }
}
