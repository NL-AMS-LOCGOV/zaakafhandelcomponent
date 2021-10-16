/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable, Type} from '@angular/core';
import {FieldType} from './model/field-type.enum';
import {InputFormField} from './form-components/input/input-form-field';
import {FormItem} from './model/form-item';
import {AbstractFormField} from './model/abstract-form-field';
import {InputComponent} from './form-components/input/input.component';
import {IFormComponent} from './model/iform-component';
import {SelectFormField} from './form-components/select/select-form-field';
import {HeadingFormField} from './form-components/heading/heading-form-field';
import {DateComponent} from './form-components/date/date.component';
import {TextareaComponent} from './form-components/textarea/textarea.component';
import {HeadingComponent} from './form-components/heading/heading.component';
import {SelectComponent} from './form-components/select/select.component';
import {DateFormField} from './form-components/date/date-form-field';
import {TextareaFormField} from './form-components/textarea/textarea-form-field';
import {FormFieldConfig} from './model/form-field-config';
import {GoogleMapsComponent} from './form-components/google-maps/google-maps.component';
import {GoogleMapsFormField} from './form-components/google-maps/google-maps-form-field';
import {ReadonlyComponent} from './form-components/readonly/readonly.component';
import {ReadonlyFormField} from './form-components/readonly/readonly-form-field';
import {FileComponent} from './form-components/file/file.component';
import {FileFormField} from './form-components/file/file-form-field';
import {Moment} from 'moment';
import {FileFieldConfig} from './model/file-field-config';

@Injectable({
    providedIn: 'root'
})
export class MaterialFormBuilderService {

    constructor() {
    }

    public createInputFormItem(id: string, label: string, value: string | null, config?: FormFieldConfig): FormItem {
        return MaterialFormBuilderService.createFormItem(FieldType.INPUT, id, label, value, config);
    }

    public createFileFormItem(id: string, label: string, config: FileFieldConfig): FormItem {
        return MaterialFormBuilderService.createFormItem(FieldType.FILE, id, label, null, config);
    }

    public createDateFormItem(id: string, label: string, value: Moment | null, config?: FormFieldConfig): FormItem {
        return MaterialFormBuilderService.createFormItem(FieldType.DATE, id, label, value, config);
    }

    public createTextareaFormItem(id: string, label: string, value: string | null, config?: FormFieldConfig): FormItem {
        return MaterialFormBuilderService.createFormItem(FieldType.TEXTAREA, id, label, value, config);
    }

    public createSelectFormItem(id: string, label: string, value: any, optionLabel: string | null, options: any[],
                                config?: FormFieldConfig): FormItem {
        return MaterialFormBuilderService.createChoicesFormItem(FieldType.SELECT, id, label, value, optionLabel, options, config);
    }

    public createHeadingFormItem(id: string, label: string, level: string): FormItem {
        const formItem = MaterialFormBuilderService.createFormItem(FieldType.HEADING, id, label, null);

        (formItem.data as HeadingFormField).level = level;
        return formItem;
    }

    public createGoogleMapsFormItem(id: string, label: string, value: string | null, config?: FormFieldConfig): FormItem {
        return MaterialFormBuilderService.createFormItem(FieldType.GOOGLEMAPS, id, label, value, config);
    }

    public createReadonlyFormItem(id: string, label: string, value: string | null): FormItem {
        return MaterialFormBuilderService.createFormItem(FieldType.READONLY, id, label, value);
    }

    private static createChoicesFormItem(type: FieldType, id: string, label: string, value: any, optionLabel: string | null, options: any[], config?: FormFieldConfig): FormItem {
        const iFormComponent = MaterialFormBuilderService.createChoicesInstance(type, id, label, value, optionLabel, options, config);
        return new FormItem(MaterialFormBuilderService.getType(type), iFormComponent);
    }

    private static createFormItem(type: FieldType, id: string, label: string, value: any, config?: FormFieldConfig): FormItem {
        const iFormComponent = MaterialFormBuilderService.createInstance(type, id, label, value, config);
        return new FormItem(MaterialFormBuilderService.getType(type), iFormComponent);
    }

    private static getType(type: FieldType): Type<IFormComponent> {
        switch (type) {
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
            case FieldType.GOOGLEMAPS:
                return GoogleMapsComponent;
            default:
                throw new Error(`Unknown type: '${type}'`);
        }
    }

    private static createInstance(type: FieldType, id: string, label: string, value: any, config?: FormFieldConfig): AbstractFormField {
        switch (type) {
            case FieldType.READONLY:
                return new ReadonlyFormField(id, label, value);
            case FieldType.DATE:
                return new DateFormField(id, label, value, config);
            case FieldType.INPUT:
                return new InputFormField(id, label, value, config);
            case FieldType.FILE:
                return new FileFormField(id, label, value, config as FileFieldConfig);
            case FieldType.TEXTAREA:
                return new TextareaFormField(id, label, value, config);
            case FieldType.HEADING:
                return new HeadingFormField(id, label, value, '1');
            case FieldType.GOOGLEMAPS:
                return new GoogleMapsFormField(id, label, value, config);
            default:
                throw new Error(`Unknown type: '${type}'`);
        }
    }

    private static createChoicesInstance(type: FieldType, id: string, label: string, value: any, optionLabel: string | null, options: any[], config?: FormFieldConfig): AbstractFormField {
        switch (type) {
            case FieldType.SELECT:
                return new SelectFormField(id, label, value, optionLabel, options, config);
            default:
                throw new Error(`Unknown choices type: '${type}'`);
        }
    }
}
