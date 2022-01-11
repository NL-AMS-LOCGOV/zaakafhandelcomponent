/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable, Type} from '@angular/core';
import {FieldType} from './model/field-type.enum';
import {FormItem} from './model/form-item';
import {AbstractFormField} from './model/abstract-form-field';
import {InputComponent} from './form-components/input/input.component';
import {IFormComponent} from './model/iform-component';
import {DateComponent} from './form-components/date/date.component';
import {TextareaComponent} from './form-components/textarea/textarea.component';
import {HeadingComponent} from './form-components/heading/heading.component';
import {SelectComponent} from './form-components/select/select.component';
import {GoogleMapsComponent} from './form-components/google-maps/google-maps.component';
import {ReadonlyComponent} from './form-components/readonly/readonly.component';
import {FileComponent} from './form-components/file/file.component';
import {AutocompleteComponent} from './form-components/autocomplete/autocomplete.component';
import {CheckboxComponent} from './form-components/checkbox/checkbox.component';

@Injectable({
    providedIn: 'root'
})
export class MaterialFormBuilderService {

    constructor() {
    }

    public createForm(formFields: Array<AbstractFormField[]>): Array<FormItem[]> {
        let form: Array<FormItem[]> = [];

        formFields.forEach(formFieldRow => {
            form.push(this.createFormRow(formFieldRow));
        });

        return form;
    }

    public getFormItem(formField: AbstractFormField): FormItem {
        return new FormItem(MaterialFormBuilderService.getType(formField.fieldType), formField);
    }

    private createFormRow(formFields: AbstractFormField[]): FormItem[] {
        let formRow: FormItem[] = [];
        formFields.forEach(formField => {
            formRow.push(this.getFormItem(formField));
        });
        return formRow;
    }

    private static getType(type: FieldType): Type<IFormComponent> {
        switch (type) {
            case FieldType.AUTOCOMPLETE:
                return AutocompleteComponent;
            case FieldType.READONLY:
                return ReadonlyComponent;
            case FieldType.DATE:
                return DateComponent;
            case FieldType.INPUT:
                return InputComponent;
            case FieldType.FILE:
                return FileComponent;
            case FieldType.TEXTAREA:
                return TextareaComponent;
            case FieldType.HEADING:
                return HeadingComponent;
            case FieldType.SELECT:
                return SelectComponent;
            case FieldType.CHECKBOX:
                return CheckboxComponent;
            case FieldType.GOOGLEMAPS:
                return GoogleMapsComponent;
            default:
                throw new Error(`Unknown type: '${type}'`);
        }
    }
}

