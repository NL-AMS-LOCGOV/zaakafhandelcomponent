/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {SortDirection} from '@angular/material/sort';
import {ListParameters} from '../../shared/model/list-parameters';
import {Zaaktype} from '../../zaken/model/zaaktype';
import {CaseDefinition} from '../model/case-definition';
import {ToggleSwitchOptions} from '../../zoeken/toggle-filter/toggle-switch-options';

export class ZaakafhandelParametersListParameters extends ListParameters {

    valide: ToggleSwitchOptions = ToggleSwitchOptions.INDETERMINATE;
    geldig:  ToggleSwitchOptions = ToggleSwitchOptions.INDETERMINATE;
    zaaktype: Zaaktype = null;
    caseDefinition: CaseDefinition = null;
    beginGeldigheid: { van: string, tot: string } = {van: null, tot: null};
    eindeGeldigheid: { van: string, tot: string } = {van: null, tot: null};

    constructor(sort: string, order: SortDirection) {
        super(sort, order);
    }
}
