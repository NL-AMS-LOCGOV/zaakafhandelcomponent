/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ZaakViewComponent} from './zaak-view/zaak-view.component';
import {ZaakCreateComponent} from './zaak-create/zaak-create.component';
import {ZaakResolver} from './zaak.resolver';
import {ZakenWerkvoorraadComponent} from './zaken-werkvoorraad/zaken-werkvoorraad.component';
import {ZakenMijnComponent} from './zaken-mijn/zaken-mijn.component';
import {ZaakEditComponent} from './zaak-edit/zaak-edit.component';
import {ZaakToekennenComponent} from './zaak-toekennen/zaak-toekennen.component';

const routes: Routes = [
    {
        path: 'zaken', children: [
            {path: '', redirectTo: 'werkvoorraad', pathMatch: 'full'},
            {path: 'mijn', component: ZakenMijnComponent},
            {path: 'werkvoorraad', component: ZakenWerkvoorraadComponent},
            {path: 'create', component: ZaakCreateComponent},
            {path: ':uuid', component: ZaakViewComponent, resolve: {zaak: ZaakResolver}},
            {path: ':uuid/edit', component: ZaakEditComponent, resolve: {zaak: ZaakResolver}},
            {path: ':uuid/toekennen', component: ZaakToekennenComponent, resolve: {zaak: ZaakResolver}}
        ]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class ZakenRoutingModule {
}
