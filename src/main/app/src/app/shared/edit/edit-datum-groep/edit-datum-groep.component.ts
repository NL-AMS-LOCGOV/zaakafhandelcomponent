/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Input, ViewEncapsulation} from '@angular/core';
import {EditComponent} from '../edit.component';
import {MaterialFormBuilderService} from '../../material-form-builder/material-form-builder.service';
import {DateFormField} from '../../material-form-builder/form-components/date/date-form-field';
import {UtilService} from '../../../core/service/util.service';
import {TextIcon} from '../text-icon';
import {FormControl} from '@angular/forms';
import {InputFormField} from '../../material-form-builder/form-components/input/input-form-field';
import {Moment} from 'moment/moment';
import * as moment from 'moment/moment';

@Component({
    selector: 'zac-edit-datum-groep',
    templateUrl: './edit-datum-groep.component.html',
    encapsulation: ViewEncapsulation.None,
    styleUrls: ['../../static-text/static-text.component.less', '../edit.component.less', './edit-datum-groep.component.less']
})
export class EditDatumGroepComponent extends EditComponent {

    @Input() formField: DateFormField;
    @Input() startDatumField: DateFormField;
    @Input() streefDatumField: DateFormField;
    @Input() fataleDatumField: DateFormField;
    @Input() streefDatumIcon: TextIcon;
    @Input() fataleDatumIcon: TextIcon;
    @Input() reasonField: InputFormField;

    showStreefDatumIcon: boolean;
    showFataleDatumIcon: boolean;
    showStreefError: boolean;
    showFataleError: boolean;
    startDatum: string;
    streefDatum: string;
    fataleDatum: string;

    editFormFieldIcons: Map<string, TextIcon> = new Map<string, TextIcon>();

    constructor(mfbService: MaterialFormBuilderService, utilService: UtilService) {
        super(mfbService, utilService);
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

        if (!this.showStreefError && !this.showFataleError && this.startDatumField.formControl.valid &&
            this.streefDatumField.formControl.valid && this.fataleDatumField.formControl.valid) {
            this.onSave.emit({
                startdatum: this.startDatumField.formControl.value,
                einddatumGepland: this.streefDatumField.formControl.value,
                uiterlijkeEinddatumAfdoening: this.fataleDatumField.formControl.value,
                reden: this.reasonField?.formControl.value
            });
            this.startDatum = this.startDatumField.formControl.value;
            this.streefDatum = this.streefDatumField.formControl.value;
            this.fataleDatum = this.fataleDatumField.formControl.value;
            this.showStreefDatumIcon = this.streefDatumIcon?.showIcon(new FormControl(this.streefDatumField.formControl.value));
            this.showFataleDatumIcon = this.fataleDatumIcon?.showIcon(new FormControl(this.fataleDatumField.formControl.value));
            this.editing = false;
        }
    }

    validate(): void {
        const start: Moment = moment(this.startDatumField.formControl.value);
        const streef: Moment = moment(this.streefDatumField.formControl.value);
        const fatale: Moment = moment(this.fataleDatumField.formControl.value);

        this.showStreefError = streef.isBefore(start);
        this.showFataleError = fatale.isBefore(streef);
        this.dirty = true;
    }

    hasError(): boolean {
        return this.showStreefError || this.showFataleError || this.startDatumField.formControl.invalid ||
            this.streefDatumField.formControl.invalid || this.fataleDatumField.formControl.invalid;
    }

    edit(editing: boolean): void {
        if (!this.readonly && !this.utilService.hasEditOverlay()) {
            this.editing = editing;
            this.startDatumField.formControl.markAsUntouched();
            this.streefDatumField.formControl.markAsUntouched();
            this.startDatumField.formControl.markAsUntouched();
            this.reasonField.formControl.setValue(null);
            this.dirty = false;
        }
    }

    cancel(): void {
        this.startDatumField.formControl.setValue(this.startDatum);
        this.streefDatumField.formControl.setValue(this.streefDatum);
        this.fataleDatumField.formControl.setValue(this.fataleDatum);
        this.showStreefError = false;
        this.showFataleError = false;

        this.editing = false;
    }
}
