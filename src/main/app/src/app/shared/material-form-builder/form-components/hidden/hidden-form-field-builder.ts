import { AbstractFormFieldBuilder } from "../../model/abstract-form-field-builder";
import { HiddenFormField } from "./hidden-form-field";

export class HiddenFormFieldBuilder extends AbstractFormFieldBuilder {
  readonly formField: HiddenFormField;

  constructor(value?: any) {
    super();
    this.formField = new HiddenFormField();
    this.formField.initControl(value);
  }
}
