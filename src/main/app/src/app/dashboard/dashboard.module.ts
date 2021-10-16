/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {DashboardComponent} from './dashboard.component';
import {MatGridListModule} from '@angular/material/grid-list';
import {MatCardModule} from '@angular/material/card';
import {MatMenuModule} from '@angular/material/menu';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';

@NgModule({
    declarations: [DashboardComponent],
    exports: [
        DashboardComponent
    ],
    imports: [
        CommonModule,
        MatGridListModule,
        MatCardModule,
        MatIconModule,
        MatMenuModule,
        MatButtonModule
    ]
})
export class DashboardModule {
}
