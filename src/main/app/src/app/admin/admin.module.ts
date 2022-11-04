/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {NgModule} from '@angular/core';

import {SharedModule} from '../shared/shared.module';
import {ParametersComponent} from './parameters/parameters.component';
import {AdminRoutingModule} from './admin-routing.module';
import {ParameterEditComponent} from './parameter-edit/parameter-edit.component';
import {ZoekenModule} from '../zoeken/zoeken.module';
import {GroepSignaleringenComponent} from './groep-signaleringen/groep-signaleringen.component';
import {ReferentieTabellenComponent} from './referentie-tabellen/referentie-tabellen.component';
import {ReferentieTabelComponent} from './referentie-tabel/referentie-tabel.component';
import {InrichtingscheckComponent} from './inrichtingscheck/inrichtingscheck.component';
import {MailtemplatesComponent} from './mailtemplates/mailtemplates.component';
import {MailtemplateComponent} from './mailtemplate/mailtemplate.component';

@NgModule({
    declarations: [
        GroepSignaleringenComponent,
        ParametersComponent,
        ParameterEditComponent,
        ReferentieTabellenComponent,
        ReferentieTabelComponent,
        InrichtingscheckComponent,
        MailtemplatesComponent,
        MailtemplateComponent
    ],
    exports: [],
    imports: [
        SharedModule,
        ZoekenModule,
        AdminRoutingModule
    ]
})
export class AdminModule {
}
