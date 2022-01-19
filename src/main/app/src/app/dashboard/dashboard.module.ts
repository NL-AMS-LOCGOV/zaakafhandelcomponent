/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {DashboardComponent} from './dashboard.component';
import {SharedModule} from '../shared/shared.module';
import {ZakenCardComponent} from './zaken-card/zaken-card.component';
import {RouterModule} from '@angular/router';

@NgModule({
    declarations: [DashboardComponent, ZakenCardComponent],
    exports: [
        DashboardComponent
    ],
    imports: [
        CommonModule,
        SharedModule,
        RouterModule
    ]
})
export class DashboardModule {
}
