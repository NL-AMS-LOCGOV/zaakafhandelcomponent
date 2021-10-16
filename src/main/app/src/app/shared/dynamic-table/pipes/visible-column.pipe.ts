/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Pipe, PipeTransform} from '@angular/core';
import {TableColumn} from '../column/table-column';

@Pipe({
    name: 'visibleColumn',
    pure: false
})
export class VisibleColumnPipe implements PipeTransform {

    transform(columns: TableColumn[], visible: boolean): TableColumn[] {
        return columns ? columns.filter(column => column.visible === visible || !visible && !column.visible) : [];
    }

}
