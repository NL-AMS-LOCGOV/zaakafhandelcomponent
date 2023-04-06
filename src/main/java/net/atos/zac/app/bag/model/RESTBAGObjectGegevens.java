/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.bag.model;

import java.util.UUID;

public class RESTBAGObjectGegevens<E extends RESTBAGObject> {

    public UUID zaakUUID;

    public E bagObject;
}
