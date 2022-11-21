/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {SortDirection} from '@angular/material/sort';
import {ListParameters} from '../../shared/model/list-parameters';
import {User} from '../../identity/model/user';
import {DatumRange} from '../../zoeken/model/datum-range';

export class OntkoppeldDocumentListParameters extends ListParameters {

    zaakID: string;
    ontkoppeldDoor: User;
    ontkoppeldOp = new DatumRange();
    creatiedatum = new DatumRange();
    titel: string;
    reden: string;

    constructor(sort: string, order: SortDirection) {
        super(sort, order);
    }
}
