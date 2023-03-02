/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {OntkoppeldeDocumentenListComponent} from './ontkoppelde-documenten-list/ontkoppelde-documenten-list.component';
import {InboxDocumentenListComponent} from './inbox-documenten-list/inbox-documenten-list.component';
import {TabelGegevensResolver} from '../shared/dynamic-table/datasource/tabel-gegevens-resolver.service';

const routes: Routes = [
    {
        path: 'documenten', children: [
            {path: 'ontkoppelde', component: OntkoppeldeDocumentenListComponent, resolve: {tabelGegevens: TabelGegevensResolver}},
            {path: 'inbox', component: InboxDocumentenListComponent, resolve: {tabelGegevens: TabelGegevensResolver}}
        ]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class DocumentenRoutingModule {
}
