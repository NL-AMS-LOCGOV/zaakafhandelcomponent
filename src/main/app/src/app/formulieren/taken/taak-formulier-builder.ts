/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { PlanItem } from "../../plan-items/model/plan-item";
import { Taak } from "../../taken/model/taak";
import { MedewerkerGroepFieldBuilder } from "../../shared/material-form-builder/form-components/medewerker-groep/medewerker-groep-field-builder";
import { HumanTaskData } from "../../plan-items/model/human-task-data";
import { DividerFormFieldBuilder } from "../../shared/material-form-builder/form-components/divider/divider-form-field-builder";
import { TaakStatus } from "../../taken/model/taak-status.enum";
import { Group } from "../../identity/model/group";
import { AbstractTaakFormulier } from "./abstract-taak-formulier";
import { Zaak } from "../../zaken/model/zaak";

export class TaakFormulierBuilder {
  protected readonly _formulier: AbstractTaakFormulier;

  constructor(formulier: AbstractTaakFormulier) {
    this._formulier = formulier;
  }

  startForm(planItem: PlanItem, zaak: Zaak): TaakFormulierBuilder {
    this._formulier.tabellen = planItem.tabellen;
    this._formulier.zaak = zaak;
    this._formulier.taakNaam = planItem.naam;
    this._formulier.humanTaskData = new HumanTaskData();
    this._formulier.humanTaskData.planItemInstanceId = planItem.id;
    if (planItem.fataleDatum) {
      this._formulier.humanTaskData.fataledatum = planItem.fataleDatum;
    }
    this._formulier.initStartForm();
    let groep = null;
    if (planItem.groepId) {
      groep = new Group();
      groep.id = planItem.groepId;
    }
    this._formulier.form.push(
      [new DividerFormFieldBuilder().build()],
      [
        new MedewerkerGroepFieldBuilder(groep)
          .id(AbstractTaakFormulier.TAAK_TOEKENNING)
          .label("actie.taak.toewijzing")
          .groepLabel("actie.taak.toekennen.groep")
          .groepRequired()
          .medewerkerLabel("actie.taak.toekennen.medewerker")
          .build(),
      ],
    );
    return this;
  }

  behandelForm(taak: Taak, zaak: Zaak): TaakFormulierBuilder {
    this._formulier.zaak = zaak;
    this._formulier.taak = taak;
    this._formulier.tabellen = taak.tabellen;
    this._formulier.dataElementen = taak.taakdata;
    this._formulier.initBehandelForm(
      taak.status === TaakStatus.Afgerond || !taak.rechten.wijzigen,
    );
    return this;
  }

  build(): AbstractTaakFormulier {
    return this._formulier;
  }
}
