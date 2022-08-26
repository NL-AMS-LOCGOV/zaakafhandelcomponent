/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Group} from '../../identity/model/group';
import {User} from '../../identity/model/user';

export class ZaakToekenning {
    groep: Group;
    medewerker: User;
}
