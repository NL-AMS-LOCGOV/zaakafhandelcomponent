/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {SortDirection} from '@angular/material/sort';
import {ListParameters} from '../../shared/model/list-parameters';

export class InboxDocumentListParameters extends ListParameters {

    identificatie: string;
    creatiedatum: { van: Date, tot: Date } = {van: null, tot: null};
    titel: string;

    constructor(sort: string, order: SortDirection) {
        super(sort, order);
    }
}
