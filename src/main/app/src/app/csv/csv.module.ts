/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {NgModule} from '@angular/core';
import {SharedModule} from '../shared/shared.module';
import {CsvExportButtonComponent} from './csv-export-button/csv-export-button.component';

@NgModule({
    declarations: [
        CsvExportButtonComponent
    ],
    exports: [
        CsvExportButtonComponent
    ],
    imports: [
        SharedModule
    ]
})
export class CsvModule {}
