/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {OntkoppeldeDocumentenListComponent} from './ontkoppelde-documenten-list/ontkoppelde-documenten-list.component';

const routes: Routes = [
    {
        path: 'ontkoppelde-documenten', children: [
            {path: '', component: OntkoppeldeDocumentenListComponent}
        ]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class OntkoppeldeDocumentenRoutingModule {
}
