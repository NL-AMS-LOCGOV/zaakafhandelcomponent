/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {BAGObject} from './bagobject';

export class BAGObjectGegevens {
    constructor(public zaakUUID: string, public bagObject: BAGObject) {}
}
