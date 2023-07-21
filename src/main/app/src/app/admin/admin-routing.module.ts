/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { ParametersComponent } from "./parameters/parameters.component";
import { ParameterEditComponent } from "./parameter-edit/parameter-edit.component";
import { ZaakafhandelParametersResolver } from "./zaakafhandel-parameters-resolver.service";
import { GroepSignaleringenComponent } from "./groep-signaleringen/groep-signaleringen.component";
import { FormulierDefinitiesComponent } from "./formulier-definities/formulier-definities.component";
import { ReferentieTabelResolver } from "./referentie-tabel-resolver.service";
import { ReferentieTabelComponent } from "./referentie-tabel/referentie-tabel.component";
import { InrichtingscheckComponent } from "./inrichtingscheck/inrichtingscheck.component";
import { MailtemplatesComponent } from "./mailtemplates/mailtemplates.component";
import { MailtemplateComponent } from "./mailtemplate/mailtemplate.component";
import { MailtemplateResolver } from "./mailtemplate-resolver.service";
import { ReferentieTabellenComponent } from "./referentie-tabellen/referentie-tabellen.component";
import { FormulierDefinitieResolverService } from "./formulier-definitie-edit/formulier-definitie-resolver.service";
import { FormulierDefinitieEditComponent } from "./formulier-definitie-edit/formulier-definitie-edit.component";

const routes: Routes = [
  {
    path: "admin",
    children: [
      { path: "", redirectTo: "check", pathMatch: "full" },
      { path: "formulierdefinities", component: FormulierDefinitiesComponent },
      {
        path: "formulierdefinities/:id",
        component: FormulierDefinitieEditComponent,
        resolve: { definitie: FormulierDefinitieResolverService },
      },
      { path: "groepen", component: GroepSignaleringenComponent },
      { path: "parameters", component: ParametersComponent },
      {
        path: "parameters/:uuid",
        component: ParameterEditComponent,
        resolve: { parameters: ZaakafhandelParametersResolver },
      },
      { path: "referentietabellen", component: ReferentieTabellenComponent },
      {
        path: "referentietabellen/:id",
        component: ReferentieTabelComponent,
        resolve: { tabel: ReferentieTabelResolver },
      },
      { path: "check", component: InrichtingscheckComponent },
      { path: "mailtemplates", component: MailtemplatesComponent },
      {
        path: "mailtemplate/:id",
        component: MailtemplateComponent,
        resolve: { template: MailtemplateResolver },
      },
      { path: "mailtemplate", component: MailtemplateComponent },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AdminRoutingModule {}
