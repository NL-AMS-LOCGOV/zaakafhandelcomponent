/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { AbstractFormFieldBuilder } from "../../model/abstract-form-field-builder";
import { MedewerkerGroepFormField } from "./medewerker-groep-form-field";
import { ValidatorFn, Validators } from "@angular/forms";
import { AbstractFormField } from "../../model/abstract-form-field";
import { User } from "../../../../identity/model/user";
import { Group } from "../../../../identity/model/group";

export class MedewerkerGroepFieldBuilder extends AbstractFormFieldBuilder {
  readonly formField: MedewerkerGroepFormField;

  constructor(groep?: Group, medewerker?: User) {
    super();
    this.formField = new MedewerkerGroepFormField();

    this.formField.initControl({
      groep: AbstractFormField.formControlInstance(groep),
      medewerker: AbstractFormField.formControlInstance(medewerker),
    });
  }

  groepLabel(groepLabel: string): this {
    this.formField.groepLabel = groepLabel;
    return this;
  }

  medewerkerLabel(medewerkerLabel: string): this {
    this.formField.medewerkerLabel = medewerkerLabel;
    return this;
  }

  validators(...validators: ValidatorFn[]): this {
    throw new Error("Not implemented");
  }

  groepRequired(): this {
    this.formField.groep.setValidators(Validators.required);
    this.formField.required = true;
    return this;
  }

  medewerkerRequired(): this {
    this.formField.medewerker.setValidators(Validators.required);
    this.formField.required = true;
    return this;
  }

  maxlength(maxlength: number): this {
    this.formField.maxlength = maxlength;
    return this;
  }

  validate() {
    if (!this.formField.id) {
      throw new Error("id is required");
    }
  }
}
