/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {BAGObjecttype} from './bagobjecttype';

export class BAGObjectGegevens {
    constructor(public zaakUUID: string, public bagObject: string, public bagObjecttype: BAGObjecttype) {}
}
