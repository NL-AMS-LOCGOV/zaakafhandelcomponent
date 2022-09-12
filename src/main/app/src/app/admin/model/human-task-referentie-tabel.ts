/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Group} from '../../identity/model/group';
import {ReferentieTabel} from './referentie-tabel';

export class HumanTaskReferentieTabel {
    id: number;
    defaultGroep: Group;
    veld: string;
    tabel: ReferentieTabel;
}
