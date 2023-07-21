/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Validators } from "@angular/forms";
import { TextareaFormFieldBuilder } from "../../../shared/material-form-builder/form-components/textarea/textarea-form-field-builder";
import { TranslateService } from "@ngx-translate/core";
import { InformatieObjectenService } from "../../../informatie-objecten/informatie-objecten.service";
import { TakenService } from "../../../taken/taken.service";
import { ParagraphFormFieldBuilder } from "../../../shared/material-form-builder/form-components/paragraph/paragraph-form-field-builder";
import { ReadonlyFormFieldBuilder } from "../../../shared/material-form-builder/form-components/readonly/readonly-form-field-builder";
import { InputFormFieldBuilder } from "../../../shared/material-form-builder/form-components/input/input-form-field-builder";
import { AbstractTaakFormulier } from "../abstract-taak-formulier";

export class ExternAdviesVastleggen extends AbstractTaakFormulier {
  fields = {
    VRAAG: "vraag",
    ADVISEUR: "adviseur",
    BRON: "bron",
    EXTERNADVIES: "externAdvies",
  };

  taakinformatieMapping = {
    uitkomst: this.fields.EXTERNADVIES,
  };

  constructor(
    translate: TranslateService,
    public takenService: TakenService,
    public informatieObjectenService: InformatieObjectenService,
  ) {
    super(translate, informatieObjectenService);
  }

  _initStartForm() {
    const fields = this.fields;
    this.form.push(
      [
        new TextareaFormFieldBuilder()
          .id(fields.VRAAG)
          .label(fields.VRAAG)
          .validators(Validators.required)
          .maxlength(1000)
          .build(),
      ],
      [
        new InputFormFieldBuilder()
          .id(fields.ADVISEUR)
          .label(fields.ADVISEUR)
          .validators(Validators.required)
          .maxlength(1000)
          .build(),
      ],
      [
        new TextareaFormFieldBuilder()
          .id(fields.BRON)
          .label(fields.BRON)
          .validators(Validators.required)
          .maxlength(1000)
          .build(),
      ],
    );
  }

  _initBehandelForm() {
    const fields = this.fields;
    this.form.push(
      [
        new ParagraphFormFieldBuilder()
          .text(
            this.translate.instant("msg.extern.advies.vastleggen.behandelen"),
          )
          .build(),
      ],
      [
        new ReadonlyFormFieldBuilder(this.getDataElement(fields.VRAAG))
          .id(fields.VRAAG)
          .label(fields.VRAAG)
          .build(),
      ],
      [
        new ReadonlyFormFieldBuilder(this.getDataElement(fields.ADVISEUR))
          .id(fields.ADVISEUR)
          .label(fields.ADVISEUR)
          .build(),
      ],
      [
        new ReadonlyFormFieldBuilder(this.getDataElement(fields.BRON))
          .id(fields.BRON)
          .label(fields.BRON)
          .build(),
      ],
      [
        new TextareaFormFieldBuilder(this.getDataElement(fields.EXTERNADVIES))
          .id(fields.EXTERNADVIES)
          .label(fields.EXTERNADVIES)
          .validators(Validators.required)
          .readonly(this.readonly)
          .maxlength(1000)
          .build(),
      ],
    );
  }

  getBehandelTitel(): string {
    return this.translate.instant("title.taak.extern-advies.verwerken");
  }
}
