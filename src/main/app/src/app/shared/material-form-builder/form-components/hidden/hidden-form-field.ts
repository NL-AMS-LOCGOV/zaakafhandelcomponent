import {FieldType} from '../../model/field-type.enum';
import {AbstractFormControlFormField} from '../../model/abstract-form-control-form-field';

export class HiddenFormField extends AbstractFormControlFormField {
    fieldType: FieldType = FieldType.HIDDEN;

    constructor() {super();}
}
