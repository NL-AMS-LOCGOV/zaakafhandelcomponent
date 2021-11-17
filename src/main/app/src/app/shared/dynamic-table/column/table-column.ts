/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {TableColumnFilter} from '../filter/table-column-filter';
import {PipeTransform, Type} from '@angular/core';
import {DynamicPipe} from '../pipes/dynamic.pipe';

export class TableColumn {
    label: string;
    model: string;
    sort: string;
    visible: boolean = false;
    sticky: boolean = false;
    filter: TableColumnFilter<any>;
    private _pipe: Type<PipeTransform>[] = [];
    private _pipeArg: string[] = [];

    constructor(label: string, model: string, visible?: boolean, sort?: string, sticky?: boolean) {
        this.label = label;
        this.model = model;
        this.visible = visible;
        this.sort = sort;
        this.sticky = sticky;
    }

    pipe(value: Type<PipeTransform>, arg?: string): TableColumn {
        this._pipe.push(value);
        this._pipeArg.push(arg);
        return this;
    }

    transform(value: any, getArgValue: (pipeArg: string) => any): any {
        const dynamicPipe = new DynamicPipe();
        for (let i = 0; i < this._pipe.length; i++) {
            value = dynamicPipe.transform(value, this._pipe[i], this._pipeArg[i] ? getArgValue(this._pipeArg[i]) : null);
        }
        return value;
    }
}
