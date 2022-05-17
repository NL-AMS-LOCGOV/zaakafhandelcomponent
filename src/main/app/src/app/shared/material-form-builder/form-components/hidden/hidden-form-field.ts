import {AbstractFormField} from '../../model/abstract-form-field';
import {FieldType} from '../../model/field-type.enum';

export class HiddenFormField extends AbstractFormField {
    fieldType: FieldType = FieldType.HIDDEN;

    constructor() {super();}
}
