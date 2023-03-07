/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, ViewChild} from '@angular/core';
import {ZakenService} from '../../zaken/zaken.service';
import {InformatieObjectenService} from '../informatie-objecten.service';
import {UtilService} from '../../core/service/util.service';
import {Zaak} from '../../zaken/model/zaak';
import {FormConfig} from '../../shared/material-form-builder/model/form-config';
import {FormGroup, Validators} from '@angular/forms';
import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';
import {DateFormFieldBuilder} from '../../shared/material-form-builder/form-components/date/date-form-field-builder';
import {FormConfigBuilder} from '../../shared/material-form-builder/model/form-config-builder';
import {FormComponent} from '../../shared/material-form-builder/form/form/form.component';
import {MatDrawer} from '@angular/material/sidenav';
import {DocumentSelectFieldBuilder} from '../../shared/material-form-builder/form-components/document-select/document-select-field-builder';
import {DocumentSelectFormField} from '../../shared/material-form-builder/form-components/document-select/document-select-form-field';

@Component({
    selector: 'zac-informatie-verzenden',
    templateUrl: './informatie-object-verzenden.component.html',
    styleUrls: ['./informatie-object-verzenden.component.less']
})
export class InformatieObjectVerzendenComponent implements OnInit, OnChanges {

    @Input() zaak: Zaak;
    @Input() sideNav: MatDrawer;
    @Output() documentVerzonden = new EventEmitter<void>();

    @ViewChild(FormComponent) form: FormComponent;

    fields: Array<AbstractFormField[]>;
    formConfig: FormConfig;
    private documentSelectFormField: DocumentSelectFormField;

    constructor(private zakenService: ZakenService,
                private informatieObjectenService: InformatieObjectenService,
                public utilService: UtilService) {
    }

    ngOnInit(): void {
        this.formConfig = new FormConfigBuilder().saveText('actie.verzenden').cancelText('actie.annuleren').build();

        this.documentSelectFormField = new DocumentSelectFieldBuilder()
        .id('documenten')
        .label('documenten')
        .removeColumn('status')
        .validators(Validators.required)
        .documenten(this.informatieObjectenService.listInformatieobjectenVoorVerzenden(this.zaak.uuid))
        .build();

        const verzendDatum = new DateFormFieldBuilder(new Date())
        .id('verzenddatum')
        .validators(Validators.required)
        .label('verzenddatum')
        .build();

        this.fields = [[this.documentSelectFormField], [verzendDatum]];

    }

    onFormSubmit(formGroup: FormGroup): void {
        if (formGroup) {
            this.sideNav.close();
            // todo rest-call verzenden

        } else {
            this.sideNav.close();
        }
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes.zaak.previousValue) {
            this.documentSelectFormField.updateDocumenten(this.informatieObjectenService.listInformatieobjectenVoorVerzenden(this.zaak.uuid));
        }
    }

}
