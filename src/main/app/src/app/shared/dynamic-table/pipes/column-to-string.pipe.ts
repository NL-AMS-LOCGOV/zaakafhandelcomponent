/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Pipe, PipeTransform} from '@angular/core';
import {TableColumn} from '../column/table-column';

@Pipe({
    name: 'columnToString',
    pure: false
})
export class ColumnToStringPipe implements PipeTransform {

    transform(columns: TableColumn[], postfix?: string): string[] {
        return columns ? columns.map(column => postfix ? column.model + postfix : column.model) : [];
    }

}
