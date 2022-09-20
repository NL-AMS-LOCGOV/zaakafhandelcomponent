import {AbstractFormFieldBuilder} from '../../model/abstract-form-field-builder';
import {HiddenFormField} from './hidden-form-field';

export class HiddenFormFieldBuilder extends AbstractFormFieldBuilder {
    protected readonly formField: HiddenFormField;

    constructor(value?: any) {
        super();
        this.formField = new HiddenFormField();
        this.formField.initFormControl(value);
    }
}
