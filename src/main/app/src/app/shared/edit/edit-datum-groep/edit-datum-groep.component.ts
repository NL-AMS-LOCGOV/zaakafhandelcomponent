/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Input} from '@angular/core';
import {EditComponent} from '../edit.component';
import {MaterialFormBuilderService} from '../../material-form-builder/material-form-builder.service';
import {DateFormField} from '../../material-form-builder/form-components/date/date-form-field';
import {UtilService} from '../../../core/service/util.service';
import {FormItem} from '../../material-form-builder/model/form-item';
import {TextIcon} from '../text-icon';
import {FormControl} from '@angular/forms';
import {InputFormField} from '../../material-form-builder/form-components/input/input-form-field';
import {Moment} from 'moment/moment';
import * as moment from 'moment/moment';

@Component({
    selector: 'zac-edit-datum-groep',
    templateUrl: './edit-datum-groep.component.html',
    styleUrls: ['../../static-text/static-text.component.less', '../edit.component.less']
})
export class EditDatumGroepComponent extends EditComponent {

    @Input() formField: DateFormField;
    @Input() startDatumField: DateFormField;
    @Input() streefDatumField: DateFormField;
    @Input() fataleDatumField: DateFormField;
    @Input() streefDatumIcon: TextIcon;
    @Input() fataleDatumIcon: TextIcon;
    @Input() reasonField: InputFormField;

    reasonItem: FormItem;
    startDatumItem: FormItem;
    streefDatumItem: FormItem;
    fataleDatumItem: FormItem;
    showStreefDatumIcon: boolean;
    showFataleDatumIcon: boolean;
    errors: string[];
    startDatum: string;
    streefDatum: string;
    fataleDatum: string;

    editFormFieldIcons: Map<string, TextIcon> = new Map<string, TextIcon>();

    constructor(mfbService: MaterialFormBuilderService, utilService: UtilService) {
        super(mfbService, utilService);
    }

    ngAfterViewInit(): void {
        this.startDatumItem = this.mfbService.getFormItem(this.startDatumField);
        this.streefDatumItem = this.mfbService.getFormItem(this.streefDatumField);
        this.fataleDatumItem = this.mfbService.getFormItem(this.fataleDatumField);

        if (this.reasonField) {
            this.reasonItem = this.mfbService.getFormItem(this.reasonField);
        }
    }

    ngOnInit(): void {
        this.showStreefDatumIcon = this.streefDatumIcon?.showIcon(new FormControl(this.streefDatumField.formControl.value));
        this.showFataleDatumIcon = this.fataleDatumIcon?.showIcon(new FormControl(this.fataleDatumField.formControl.value));
        this.startDatum = this.startDatumField.formControl.value;
        this.streefDatum = this.streefDatumField.formControl.value;
        this.fataleDatum = this.fataleDatumField.formControl.value;
    }

    init(formField: DateFormField): void {
    }

    save(): void {
        this.validate();

        if (this.errors.length === 0 && this.startDatumItem.data.formControl.valid &&
            this.streefDatumItem.data.formControl.valid && this.fataleDatumItem.data.formControl.valid) {
            this.onSave.emit({
                startdatum: this.startDatumItem.data.formControl.value,
                einddatumGepland: this.streefDatumItem.data.formControl.value,
                uiterlijkeEinddatumAfdoening: this.fataleDatumItem.data.formControl.value,
                reden: this.reasonItem?.data.formControl.value
            });
            this.startDatum = this.startDatumItem.data.formControl.value;
            this.streefDatum = this.streefDatumItem.data.formControl.value;
            this.fataleDatum = this.fataleDatumItem.data.formControl.value;
            this.showStreefDatumIcon = this.streefDatumIcon?.showIcon(new FormControl(this.streefDatumItem.data.formControl.value));
            this.showFataleDatumIcon = this.fataleDatumIcon?.showIcon(new FormControl(this.fataleDatumItem.data.formControl.value));
            this.editing = false;
        }
    }

    validate(): void {
        this.errors = [];
        const start: Moment = moment(this.startDatumItem.data.formControl.value);
        const streef: Moment = moment(this.streefDatumItem.data.formControl.value);
        const fatale: Moment = moment(this.fataleDatumItem.data.formControl.value);

        if (streef.isBefore(start)) {
            this.errors.push('msg.error.date.invalid.streef');
        }
        if (fatale.isBefore(streef)) {
            this.errors.push('msg.error.date.invalid.fatale');
        }
    }

    edit(editing: boolean): void {
        if (!this.readonly && !this.utilService.hasEditOverlay()) {
            this.editing = editing;
            this.startDatumItem.data.formControl.markAsUntouched();
            this.streefDatumItem.data.formControl.markAsUntouched();
            this.startDatumItem.data.formControl.markAsUntouched();
            this.reasonItem.data.formControl.setValue(null);
        }
    }

    cancel(): void {
        this.startDatumItem.data.formControl.setValue(this.startDatum);
        this.startDatumField.formControl.setValue(this.startDatum);
        this.streefDatumItem.data.formControl.setValue(this.streefDatum);
        this.streefDatumField.formControl.setValue(this.streefDatum);
        this.fataleDatumItem.data.formControl.setValue(this.fataleDatum);
        this.fataleDatumField.formControl.setValue(this.fataleDatum);
        this.errors = [];

        this.editing = false;
    }
}
