/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { NgModule } from "@angular/core";

import { SharedModule } from "../shared/shared.module";
import { ParametersComponent } from "./parameters/parameters.component";
import { AdminRoutingModule } from "./admin-routing.module";
import { ParameterEditComponent } from "./parameter-edit/parameter-edit.component";
import { ZoekenModule } from "../zoeken/zoeken.module";
import { GroepSignaleringenComponent } from "./groep-signaleringen/groep-signaleringen.component";
import { FormulierDefinitiesComponent } from "./formulier-definities/formulier-definities.component";
import { ReferentieTabelComponent } from "./referentie-tabel/referentie-tabel.component";
import { InrichtingscheckComponent } from "./inrichtingscheck/inrichtingscheck.component";
import { MailtemplatesComponent } from "./mailtemplates/mailtemplates.component";
import { MailtemplateComponent } from "./mailtemplate/mailtemplate.component";
import { NgxEditorModule } from "ngx-editor";
import { ReferentieTabellenComponent } from "./referentie-tabellen/referentie-tabellen.component";
import { FormulierDefinitieEditComponent } from "./formulier-definitie-edit/formulier-definitie-edit.component";
import { TekstvlakEditDialogComponent } from "./formulier-definitie-edit/tekstvlak-edit-dialog/tekstvlak-edit-dialog.component";

@NgModule({
  declarations: [
    GroepSignaleringenComponent,
    ParametersComponent,
    ParameterEditComponent,
    FormulierDefinitiesComponent,
    FormulierDefinitieEditComponent,
    ReferentieTabellenComponent,
    ReferentieTabelComponent,
    InrichtingscheckComponent,
    MailtemplatesComponent,
    MailtemplateComponent,
    TekstvlakEditDialogComponent,
  ],
  exports: [],
  imports: [SharedModule, ZoekenModule, AdminRoutingModule, NgxEditorModule],
})
export class AdminModule {}
