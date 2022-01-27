/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, Input, ViewChild} from '@angular/core';
import {FormItem} from '../../model/form-item';
import {FormFieldDirective} from './form-field.directive';
import {ReadonlyFormFieldBuilder} from '../../form-components/readonly/readonly-form-field-builder';
import {ReadonlyComponent} from '../../form-components/readonly/readonly.component';

@Component({
    selector: 'mfb-form-field',
    templateUrl: './form-field.component.html',
    styleUrls: ['./form-field.component.less']
})
export class FormFieldComponent implements AfterViewInit {

    @Input() set field(item: FormItem) {
        this._field = item;
        this.refreshComponent();
    }

    private _field: FormItem;
    private loaded: boolean;

    @ViewChild(FormFieldDirective) formField: FormFieldDirective;

    ngAfterViewInit() {
        this.loadComponent();
    }

    refreshComponent() {
        if (this.loaded) {
            this.formField.viewContainerRef.clear();
            this.loadComponent();
        }
    }

    loadComponent() {
        if (this._field.data.readonly && !this._field.data.hasReadonlyView()) {
            this._field = new FormItem(ReadonlyComponent,
                new ReadonlyFormFieldBuilder().id(this._field.data.id)
                                              .label(this._field.data.label)
                                              .value(this._field.data.formControl.value)
                                              .build());
        }
        const componentRef = this.formField.viewContainerRef.createComponent(this._field.component);

        // apply data and activate change detection
        componentRef.instance.data = this._field.data;
        componentRef.changeDetectorRef.detectChanges();
        this.loaded = true;
    }
}
