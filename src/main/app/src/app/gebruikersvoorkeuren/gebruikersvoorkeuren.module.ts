/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {NgModule} from '@angular/core';
import {SharedModule} from '../shared/shared.module';
import {ZoekopdrachtSaveDialogComponent} from './zoekopdracht-save-dialog/zoekopdracht-save-dialog.component';

@NgModule({
    declarations: [
        ZoekopdrachtSaveDialogComponent
    ],
    imports: [
        SharedModule
    ]
})
export class GebruikersvoorkeurenModule {
}
