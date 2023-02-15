/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.identity.model;

import java.util.Set;

public class RESTLoggedInUser extends RESTUser {

    public Set<String> groupIds;
}
