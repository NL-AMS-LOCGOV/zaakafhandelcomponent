/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {SortDirection} from '@angular/material/sort';
import {ListParameters} from '../../shared/model/list-parameters';

export class OntkoppeldDocumentListParameters extends ListParameters {

    zaakID: string;
    ontkoppeldeDoor: string;
    ontkoppeldOp: { van: string, tot: string } = {van: null, tot: null};
    creatiedatum: { van: string, tot: string } = {van: null, tot: null};
    titel: string;
    reden: string;

    constructor(sort: string, order: SortDirection) {
        super(sort, order);
    }
}
