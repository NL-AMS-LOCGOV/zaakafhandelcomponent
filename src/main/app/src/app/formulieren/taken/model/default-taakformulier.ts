/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Validators } from "@angular/forms";
import { TextareaFormFieldBuilder } from "../../../shared/material-form-builder/form-components/textarea/textarea-form-field-builder";
import { ReadonlyFormFieldBuilder } from "../../../shared/material-form-builder/form-components/readonly/readonly-form-field-builder";
import { TranslateService } from "@ngx-translate/core";
import { InformatieObjectenService } from "../../../informatie-objecten/informatie-objecten.service";
import { AbstractTaakFormulier } from "../abstract-taak-formulier";

export class DefaultTaakformulier extends AbstractTaakFormulier {
  fields = {
    REDEN_START: "redenStart",
    AFHANDELING: "afhandeling",
  };

  taakinformatieMapping = {
    uitkomst: this.fields.AFHANDELING,
  };

  constructor(
    translate: TranslateService,
    public informatieObjectenService: InformatieObjectenService,
  ) {
    super(translate, informatieObjectenService);
  }

  _initStartForm() {
    const fields = this.fields;
    this.form.push([
      new TextareaFormFieldBuilder(this.getDataElement(fields.REDEN_START))
        .id(fields.REDEN_START)
        .label(fields.REDEN_START)
        .validators(Validators.required)
        .maxlength(1000)
        .build(),
    ]);
  }

  _initBehandelForm() {
    const fields = this.fields;
    this.form.push(
      [
        new ReadonlyFormFieldBuilder(this.getDataElement(fields.REDEN_START))
          .id(fields.REDEN_START)
          .label(fields.REDEN_START)
          .build(),
      ],
      [
        new TextareaFormFieldBuilder(this.getDataElement(fields.AFHANDELING))
          .id(fields.AFHANDELING)
          .label(fields.AFHANDELING)
          .validators(Validators.required)
          .readonly(this.readonly)
          .maxlength(1000)
          .build(),
      ],
    );
  }
}
