import { FieldType } from "../../model/field-type.enum";
import { AbstractFormControlField } from "../../model/abstract-form-control-field";

export class HiddenFormField extends AbstractFormControlField {
  fieldType: FieldType = FieldType.HIDDEN;

  constructor() {
    super();
  }

  initControl(value?: any) {
    super.initControl(value);
    this.label = "hidden";
  }
}
