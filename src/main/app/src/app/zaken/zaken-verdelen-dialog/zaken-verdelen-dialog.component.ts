/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {IdentityService} from '../../identity/identity.service';
import {ZakenService} from '../zaken.service';
import {ZaakOverzicht} from '../model/zaak-overzicht';
import {FormItem} from '../../shared/material-form-builder/model/form-item';
import {AutocompleteFormFieldBuilder} from '../../shared/material-form-builder/form-components/autocomplete/autocomplete-form-field-builder';
import {MaterialFormBuilderService} from '../../shared/material-form-builder/material-form-builder.service';
import {TextareaFormFieldBuilder} from '../../shared/material-form-builder/form-components/textarea/textarea-form-field-builder';
import {Observable, Subscription} from 'rxjs';
import {Medewerker} from '../../identity/model/medewerker';
import {CheckboxFormFieldBuilder} from '../../shared/material-form-builder/form-components/checkbox/checkbox-form-field-builder';

@Component({
    selector: 'werkvoorraad-verdelen-dialog',
    templateUrl: 'zaken-verdelen-dialog.component.html',
    styleUrls: ['./zaken-verdelen-dialog.component.less']
})
export class ZakenVerdelenDialogComponent implements OnInit {

    groepFormItem: FormItem;
    filterFormItem: FormItem;
    medewerkerFormItem: FormItem;
    redenFormItem: FormItem;
    loading: boolean;
    private subscriptions$: Subscription[] = [];

    constructor(
        public dialogRef: MatDialogRef<ZakenVerdelenDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: ZaakOverzicht[],
        private mfbService: MaterialFormBuilderService,
        private zakenService: ZakenService,
        private identityService: IdentityService) {
    }

    close(): void {
        this.dialogRef.close();
    }

    ngOnInit(): void {
        this.groepFormItem = this.mfbService.getFormItem(new AutocompleteFormFieldBuilder().id('groep')
                                                                                           .label('groep')
                                                                                           .optionLabel('naam')
                                                                                           .options(this.identityService.listGroepen())
                                                                                           .build());
        this.filterFormItem = this.mfbService.getFormItem(new CheckboxFormFieldBuilder().id('filter')
                                                                                        .label('filter.groep')
                                                                                        .value(true)
                                                                                        .build());
        this.redenFormItem = this.mfbService.getFormItem(new TextareaFormFieldBuilder().id('reden')
                                                                                       .label('reden')
                                                                                       .build());
        this.subscriptions$.push(this.groepFormItem.data.formControl.valueChanges.subscribe(value => {
            this.laadMedewerkers();
        }));
        this.subscriptions$.push(this.filterFormItem.data.formControl.valueChanges.subscribe(value => {
            this.laadMedewerkers();
        }));
        this.laadMedewerkers();
    }

    private laadMedewerkers(): void {
        var behandelaar: Medewerker = this.medewerkerFormItem?.data.formControl.value;
        var medewerkers: Observable<Medewerker[]>;
        if (this.groepFormItem.data.formControl.value && this.filterFormItem.data.formControl.value) {
            medewerkers = this.identityService.listMedewerkersInGroep(this.groepFormItem.data.formControl.value.id);
        } else {
            medewerkers = this.identityService.listMedewerkers();
        }
        this.medewerkerFormItem = this.mfbService.getFormItem(new AutocompleteFormFieldBuilder().id('behandelaar')
                                                                                                .label('behandelaar')
                                                                                                .optionLabel('naam')
                                                                                                .options(medewerkers)
                                                                                                .value(behandelaar)
                                                                                                .build());
    }

    ngOnDestroy(): void {
        this.subscriptions$.forEach(subscription$ => subscription$.unsubscribe());
    }

    verdeel(): void {
        this.dialogRef.disableClose = true;
        this.loading = true;
        this.zakenService.verdelen(
            this.data,
            this.groepFormItem.data.formControl.value,
            this.medewerkerFormItem.data.formControl.value,
            this.redenFormItem.data.formControl.value
        ).subscribe(() => {
            this.dialogRef.close(this.groepFormItem.data.formControl.value || this.medewerkerFormItem.data.formControl.value);
        });
    }
}
