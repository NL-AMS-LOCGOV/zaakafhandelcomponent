/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Pipe, PipeTransform} from '@angular/core';
import {TableColumn} from '../column/table-column';

@Pipe({
    name: 'noStickyColumn',
    pure: false
})
export class NoStickyColumnPipe implements PipeTransform {

    transform(columns: TableColumn[]): TableColumn[] {
        return columns ? columns.filter(column => !column.sticky) : [];
    }

}
