/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {NgModule} from '@angular/core';

import {TakenRoutingModule} from './taken-routing.module';
import {TaakViewComponent} from './taak-view/taak-view.component';
import {SharedModule} from '../shared/shared.module';
import {ZakenModule} from '../zaken/zaken.module';
import {TakenMijnComponent} from './taken-mijn/taken-mijn.component';
import {TakenWerkvoorraadComponent} from './taken-werkvoorraad/taken-werkvoorraad.component';
import {TaakToekennenComponent} from './taak-toekennen/taak-toekennen.component';
import {TaakBewerkenComponent} from './taak-bewerken/taak-bewerken.component';

@NgModule({
    declarations: [TaakViewComponent, TakenWerkvoorraadComponent, TakenMijnComponent, TaakToekennenComponent, TaakBewerkenComponent],
    imports: [
        SharedModule,
        TakenRoutingModule,
        ZakenModule
    ]
})
export class TakenModule {
}
