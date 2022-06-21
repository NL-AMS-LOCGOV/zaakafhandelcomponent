/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {EditComponent} from '../edit.component';
import {MaterialFormBuilderService} from '../../material-form-builder/material-form-builder.service';
import {DateFormField} from '../../material-form-builder/form-components/date/date-form-field';
import {UtilService} from '../../../core/service/util.service';
import {TextIcon} from '../text-icon';
import {FormControl, Validators} from '@angular/forms';
import {InputFormField} from '../../material-form-builder/form-components/input/input-form-field';
import * as moment from 'moment/moment';
import {Moment} from 'moment/moment';
import {DialogComponent} from '../../dialog/dialog.component';
import {MatDialog} from '@angular/material/dialog';
import {DialogData} from '../../dialog/dialog-data';
import {ZakenService} from '../../../zaken/zaken.service';
import {InputFormFieldBuilder} from '../../material-form-builder/form-components/input/input-form-field-builder';
import {TranslateService} from '@ngx-translate/core';
import {Observable, of, Subscription} from 'rxjs';
import {CheckboxFormFieldBuilder} from '../../material-form-builder/form-components/checkbox/checkbox-form-field-builder';
import {CheckboxFormField} from '../../material-form-builder/form-components/checkbox/checkbox-form-field';
import {HiddenFormFieldBuilder} from '../../material-form-builder/form-components/hidden/hidden-form-field-builder';
import {HiddenFormField} from '../../material-form-builder/form-components/hidden/hidden-form-field';
import {AbstractFormField} from '../../material-form-builder/model/abstract-form-field';

@Component({
    selector: 'zac-edit-datum-groep',
    templateUrl: './edit-datum-groep.component.html',
    styleUrls: ['../../static-text/static-text.component.less', '../edit.component.less', './edit-datum-groep.component.less']
})
export class EditDatumGroepComponent extends EditComponent implements OnInit {

    @Input() formField: DateFormField;
    @Input() startDatumField: DateFormField;
    @Input() einddatumGeplandField: DateFormField;
    @Input() uiterlijkeEinddatumAfdoeningField: DateFormField;
    @Input() einddatumGeplandIcon: TextIcon;
    @Input() uiterlijkeEinddatumAfdoeningIcon: TextIcon;
    @Input() reasonField: InputFormField;
    @Input() opgeschort: boolean;
    @Input() opschortReden: string;
    @Input() opschortDuur: number;
    @Input() opschortDatumTijd: string;
    @Output() doOpschorting: EventEmitter<any> = new EventEmitter<any>();
    @Input() verlengReden: string;
    @Input() verlengDuur: string;
    @Input() opschortenEnVerlengenReadonly: boolean;
    @Output() doVerlenging: EventEmitter<any> = new EventEmitter<any>();

    showEinddatumGeplandIcon: boolean;
    showUiterlijkeEinddatumAfdoeningIcon: boolean;
    showEinddatumGeplandError: boolean;
    showUiterlijkeEinddatumAfdoeningError: boolean;

    startDatum: string;
    einddatumGeplandDatum: string;
    uiterlijkeEinddatumAfdoeningDatum: string;

    duurField: InputFormField;
    werkelijkeOpschortDuur: number;

    editFormFieldIcons: Map<string, TextIcon> = new Map<string, TextIcon>();

    constructor(mfbService: MaterialFormBuilderService,
                utilService: UtilService,
                private zakenService: ZakenService,
                private dialog: MatDialog,
                private translate: TranslateService) {
        super(mfbService, utilService);
    }

    ngOnInit(): void {
        this.updateGroep();
    }

    updateGroep(): void {
        this.startDatum = this.startDatumField.formControl.value;
        this.einddatumGeplandDatum = this.einddatumGeplandField.formControl.value;
        this.uiterlijkeEinddatumAfdoeningDatum = this.uiterlijkeEinddatumAfdoeningField.formControl.value;
        this.showEinddatumGeplandIcon = this.einddatumGeplandIcon?.showIcon(
            new FormControl(this.einddatumGeplandField.formControl.value));
        this.showUiterlijkeEinddatumAfdoeningIcon = this.uiterlijkeEinddatumAfdoeningIcon?.showIcon(
            new FormControl(this.uiterlijkeEinddatumAfdoeningField.formControl.value));
    }

    init(formField: DateFormField): void {
    }

    save(): void {
        this.validate();
        if (!this.showEinddatumGeplandError && !this.showUiterlijkeEinddatumAfdoeningError && this.startDatumField.formControl.valid &&
            this.einddatumGeplandField.formControl.valid && this.uiterlijkeEinddatumAfdoeningField.formControl.valid) {
            this.onSave.emit({
                startdatum: this.startDatumField.formControl.value,
                einddatumGepland: this.einddatumGeplandField.formControl.value,
                uiterlijkeEinddatumAfdoening: this.uiterlijkeEinddatumAfdoeningField.formControl.value,
                reden: this.reasonField?.formControl.value
            });
            this.updateGroep();
            this.editing = false;
        }
    }

    validate(): void {
        const start: Moment = moment(this.startDatumField.formControl.value);
        const uiterlijkeEinddatumAfdoening: Moment = moment(this.uiterlijkeEinddatumAfdoeningField.formControl.value);
        if (this.einddatumGeplandField.formControl.value) {
            const einddatumGepland: Moment = moment(this.einddatumGeplandField.formControl.value);
            this.showEinddatumGeplandError = einddatumGepland.isBefore(start) ||
                uiterlijkeEinddatumAfdoening.isBefore(einddatumGepland);
            this.showUiterlijkeEinddatumAfdoeningError = uiterlijkeEinddatumAfdoening.isBefore(start) ||
                uiterlijkeEinddatumAfdoening.isBefore(einddatumGepland);
        } else {
            this.showUiterlijkeEinddatumAfdoeningError = uiterlijkeEinddatumAfdoening.isBefore(start);
        }
        this.dirty = true;
    }

    hasError(): boolean {
        return this.showEinddatumGeplandError || this.showUiterlijkeEinddatumAfdoeningError ||
            this.startDatumField.formControl.invalid ||
            this.einddatumGeplandField.formControl.invalid ||
            this.uiterlijkeEinddatumAfdoeningField.formControl.invalid;
    }

    edit(editing: boolean): void {
        if (!this.readonly && !this.utilService.hasEditOverlay()) {
            this.editing = editing;
            this.startDatumField.formControl.markAsUntouched();
            this.einddatumGeplandField.formControl.markAsUntouched();
            this.uiterlijkeEinddatumAfdoeningField.formControl.markAsUntouched();
            this.reasonField.formControl.setValue(null);
            this.dirty = false;
        }
    }

    cancel(): void {
        this.startDatumField.formControl.setValue(this.startDatum);
        this.einddatumGeplandField.formControl.setValue(this.einddatumGeplandDatum);
        this.uiterlijkeEinddatumAfdoeningField.formControl.setValue(this.uiterlijkeEinddatumAfdoeningDatum);
        this.showEinddatumGeplandError = false;
        this.showUiterlijkeEinddatumAfdoeningError = false;
        this.editing = false;
    }

    private maakDuurField(label: string): InputFormField {
        this.duurField = new InputFormFieldBuilder()
        .id('duurDagen')
        .label(label)
        .validators(Validators.required, Validators.min(1))
        .build();
        return this.duurField;
    }

    private maakHiddenField(field: AbstractFormField): HiddenFormField {
        return new HiddenFormFieldBuilder()
        .id(field.id)
        .label(field.label)
        .value(field.formControl.value)
        .build();
    }

    private maakRedenField(reden: string): InputFormField {
        return new InputFormFieldBuilder()
        .id('reden')
        .label('reden')
        .value(reden)
        .validators(Validators.required)
        .build();
    }

    private maakTakenField(label: string): CheckboxFormField {
        return new CheckboxFormFieldBuilder()
        .id('takenVerlengen')
        .label(label)
        .build();
    }

    opschorten() {
        const heeftEinddatumGepland: boolean = this.einddatumGeplandField.formControl.value;
        const dialogData = new DialogData([
                this.maakDuurField('opschortduur'),
                heeftEinddatumGepland ? this.einddatumGeplandField : this.maakHiddenField(this.einddatumGeplandField),
                this.uiterlijkeEinddatumAfdoeningField,
                this.maakRedenField(this.opschortReden)
            ],
            (results: any[]) => this.saveOpschorting(results),
            this.translate.instant('msg.zaak.opschorten'));
        dialogData.confirmButtonActionKey = 'actie.zaak.opschorten';

        const vorigeEinddatumGepland: Moment = heeftEinddatumGepland ? moment(this.einddatumGeplandField.formControl.value) : null;
        const vorigeUiterlijkeEinddatumAfdoening: Moment = moment(this.uiterlijkeEinddatumAfdoeningField.formControl.value);
        const subscriptions = this.subscribe(vorigeEinddatumGepland, vorigeUiterlijkeEinddatumAfdoening);

        this.dialog.open(DialogComponent, {
            data: dialogData
        }).afterClosed().subscribe(result => {
            this.unsubscribe(subscriptions);
            if (!result) {
                this.resetDatums(vorigeEinddatumGepland, vorigeUiterlijkeEinddatumAfdoening);
            }
            this.updateGroep();
        });
    }

    saveOpschorting(results: any[]): Observable<void> {
        this.doOpschorting.emit(results);
        return of(null);
    }

    hervatten() {
        this.werkelijkeOpschortDuur = moment().diff(moment(this.opschortDatumTijd), 'days');
        const dialogData = new DialogData([
                this.maakRedenField(this.opschortReden)
            ],
            (results: any[]) => this.saveHervatting(results),
            this.translate.instant('msg.zaak.hervatten', {duur: this.werkelijkeOpschortDuur, verwachteDuur: this.opschortDuur}));
        dialogData.confirmButtonActionKey = 'actie.zaak.hervatten';

        this.dialog.open(DialogComponent, {
            data: dialogData
        });
    }

    saveHervatting(results: any[]): Observable<void> {
        const heeftEinddatumGepland: boolean = this.einddatumGeplandField.formControl.value;
        const duurVerschil: number = this.werkelijkeOpschortDuur - this.opschortDuur;
        if (heeftEinddatumGepland) {
            this.einddatumGeplandField.formControl.setValue(moment(this.einddatumGeplandField.formControl.value).add(duurVerschil, 'days'));
        }
        this.uiterlijkeEinddatumAfdoeningField.formControl.setValue(moment(this.uiterlijkeEinddatumAfdoeningField.formControl.value).add(duurVerschil, 'days'));
        this.updateGroep();
        results['duurDagen'] = this.werkelijkeOpschortDuur;
        results['einddatumGepland'] = this.einddatumGeplandField.formControl.value;
        results['uiterlijkeEinddatumAfdoening'] = this.uiterlijkeEinddatumAfdoeningField.formControl.value;
        return this.saveOpschorting(results);
    }

    verlengen() {
        const heeftEinddatumGepland: boolean = this.einddatumGeplandField.formControl.value;
        const dialogData = new DialogData([
                this.maakDuurField('verlengduur'),
                heeftEinddatumGepland ? this.einddatumGeplandField : this.maakHiddenField(this.einddatumGeplandField),
                this.uiterlijkeEinddatumAfdoeningField,
                this.maakRedenField(this.verlengReden),
                this.maakTakenField('taken.verlengen')
            ],
            (results: any[]) => this.saveVerlenging(results),
            this.translate.instant(this.verlengDuur ? 'msg.zaak.verlengen.meer' : 'msg.zaak.verlengen', {eerdereDuur: this.verlengDuur}));
        dialogData.confirmButtonActionKey = 'actie.zaak.verlengen';

        const vorigeEinddatumGepland: Moment = heeftEinddatumGepland ? moment(this.einddatumGeplandField.formControl.value) : null;
        const vorigeUiterlijkeEinddatumAfdoening: Moment = moment(this.uiterlijkeEinddatumAfdoeningField.formControl.value);
        const subscriptions = this.subscribe(vorigeEinddatumGepland, vorigeUiterlijkeEinddatumAfdoening);

        this.dialog.open(DialogComponent, {
            data: dialogData
        }).afterClosed().subscribe(result => {
            this.unsubscribe(subscriptions);
            if (!result) {
                this.resetDatums(vorigeEinddatumGepland, vorigeUiterlijkeEinddatumAfdoening);
            }
            this.updateGroep();
        });
    }

    saveVerlenging(results: any[]): Observable<void> {
        this.doVerlenging.emit(results);
        return of(null);
    }

    private subscribe(vorigeEinddatumGeplandDatum: Moment, vorigeUiterlijkeEinddatumAfdoening: Moment): Subscription[] {
        const subscriptions: Subscription[] = [];
        subscriptions.push(this.duurField.formControl.valueChanges.subscribe(value => {
            this.duurChanged(value, vorigeEinddatumGeplandDatum, vorigeUiterlijkeEinddatumAfdoening);
        }));
        subscriptions.push(this.einddatumGeplandField.formControl.valueChanges.subscribe(value => {
            this.einddatumGeplandChanged(value, vorigeEinddatumGeplandDatum, vorigeUiterlijkeEinddatumAfdoening);
        }));
        subscriptions.push(this.uiterlijkeEinddatumAfdoeningField.formControl.valueChanges.subscribe(value => {
            this.uiterlijkEinddatumAfdoeningChanged(value, vorigeEinddatumGeplandDatum, vorigeUiterlijkeEinddatumAfdoening);
        }));
        return subscriptions;
    }

    private unsubscribe(subscriptions: Subscription[]): void {
        for (const subscription of subscriptions) {
            subscription.unsubscribe();
        }
    }

    duurChanged(value: string, vorigeEinddatumGeplandDatum: Moment, vorigeUiterlijkeEinddatumAfdoening: Moment) {
        let duur: number = Number(value);
        if (value == null || isNaN(duur)) {
            duur = 0;
        }
        this.updateDatums(duur, vorigeEinddatumGeplandDatum, vorigeUiterlijkeEinddatumAfdoening);
    }

    einddatumGeplandChanged(value: any, vorigeEinddatumGeplandDatum: Moment, vorigeUiterlijkeEinddatumAfdoening: Moment) {
        if (value != null) {
            this.updateDatums(moment(value).diff(vorigeEinddatumGeplandDatum, 'days'), vorigeEinddatumGeplandDatum, vorigeUiterlijkeEinddatumAfdoening);
        }
    }

    uiterlijkEinddatumAfdoeningChanged(value: any, vorigeEinddatumGeplandDatum: Moment, vorigeUiterlijkeEinddatumAfdoening: Moment) {
        if (value != null) {
            this.updateDatums(moment(value).diff(vorigeUiterlijkeEinddatumAfdoening, 'days'), vorigeEinddatumGeplandDatum, vorigeUiterlijkeEinddatumAfdoening);
        }
    }

    private updateDatums(duur: number, vorigeEinddatumGeplandDatum: Moment, vorigeUiterlijkeEinddatumAfdoening: Moment) {
        if (0 < duur) {
            this.duurField.formControl.setValue(duur, {emitEvent: false});
            if (vorigeEinddatumGeplandDatum != null) {
                this.einddatumGeplandField.formControl.setValue(moment(vorigeEinddatumGeplandDatum).add(duur, 'days'), {emitEvent: false});
            }
            this.uiterlijkeEinddatumAfdoeningField.formControl.setValue(moment(vorigeUiterlijkeEinddatumAfdoening).add(duur, 'days'), {emitEvent: false});
        } else {
            this.resetDatums(vorigeEinddatumGeplandDatum, vorigeUiterlijkeEinddatumAfdoening);
        }
    }

    private resetDatums(vorigeEinddatumGeplandDatum: Moment, vorigeUiterlijkeEinddatumAfdoening: Moment) {
        this.duurField.formControl.setValue(null, {emitEvent: false});
        this.einddatumGeplandField.formControl.setValue(vorigeEinddatumGeplandDatum, {emitEvent: false});
        this.uiterlijkeEinddatumAfdoeningField.formControl.setValue(vorigeUiterlijkeEinddatumAfdoening, {emitEvent: false});
    }
}
