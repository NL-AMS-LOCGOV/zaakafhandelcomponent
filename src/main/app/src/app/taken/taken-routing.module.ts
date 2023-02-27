/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {TaakViewComponent} from './taak-view/taak-view.component';
import {TaakResolver} from './taak.resolver';
import {TakenMijnComponent} from './taken-mijn/taken-mijn.component';
import {TakenWerkvoorraadComponent} from './taken-werkvoorraad/taken-werkvoorraad.component';
import {TabelGegevensResolver} from '../shared/dynamic-table/datasource/tabel-gegevens-resolver.service';

const routes: Routes = [
    {
        path: 'taken', children: [
            {path: '', redirectTo: 'werkvoorraad', pathMatch: 'full'},
            {path: 'werkvoorraad', component: TakenWerkvoorraadComponent, resolve: {tabelGegevens: TabelGegevensResolver}},
            {path: 'mijn', component: TakenMijnComponent, resolve: {tabelGegevens: TabelGegevensResolver}},
            {path: ':id', component: TaakViewComponent, resolve: {taak: TaakResolver}}
        ]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class TakenRoutingModule {
}
