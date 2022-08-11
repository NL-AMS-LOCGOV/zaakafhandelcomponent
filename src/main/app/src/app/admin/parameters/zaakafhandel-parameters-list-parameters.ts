/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {SortDirection} from '@angular/material/sort';
import {ListParameters} from '../../shared/model/list-parameters';

export class ZaakafhandelParametersListParameters extends ListParameters {

    valide: string = null;
    geldig: string = null;
    beginGeldigheid: { van: string, tot: string } = {van: null, tot: null};
    eindeGeldigheid: { van: string, tot: string } = {van: null, tot: null};

    constructor(sort: string, order: SortDirection) {
        super(sort, order);
    }
}
