/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, ComponentFactoryResolver, Input, ViewChild} from '@angular/core';
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

    @Input() field: FormItem;
    @ViewChild(FormFieldDirective) formField: FormFieldDirective;

    constructor(private componentFactoryResolver: ComponentFactoryResolver) {
    }

    ngAfterViewInit() {
        this.loadComponent();
    }

    loadComponent() {
        if (this.field.data.readonly) {
            this.field = new FormItem(ReadonlyComponent,
                new ReadonlyFormFieldBuilder().id(this.field.data.id).label(this.field.data.label).value(this.field.data.formControl.value)
                                              .build());
        }
        // create component with ComponentFactory
        const componentFactory = this.componentFactoryResolver.resolveComponentFactory(this.field.component);
        const componentRef = this.formField.viewContainerRef.createComponent(componentFactory);

        // apply data and activate change detection
        componentRef.instance.data = this.field.data;
        componentRef.changeDetectorRef.detectChanges();
    }
}
