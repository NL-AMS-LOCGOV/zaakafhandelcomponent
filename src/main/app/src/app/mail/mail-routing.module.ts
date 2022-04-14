/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {OntvangstbevestigingComponent} from './ontvangstbevestiging/ontvangstbevestiging.component';

const routes: Routes = [
    {
        path: 'mail', children: [
            {path: 'ontvangstbevestiging/:zaakUuid', component: OntvangstbevestigingComponent}
        ]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class MailRoutingModule {
}
