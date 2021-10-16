/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {TableColumnFilter} from '../filter/table-column-filter';
import {PipeTransform, Type} from '@angular/core';

export class TableColumn {
    label: string;
    model: string;
    sort: string;
    visible: boolean = false;
    sticky: boolean = false;
    filter: TableColumnFilter<any>;
    pipe: Type<PipeTransform>;
    pipeArg: string;

    constructor(label: string, model: string, visible?: boolean, sort?: string, sticky?: boolean) {
        this.label = label;
        this.model = model;
        this.visible = visible;
        this.sort = sort;
        this.sticky = sticky;
    }

}
