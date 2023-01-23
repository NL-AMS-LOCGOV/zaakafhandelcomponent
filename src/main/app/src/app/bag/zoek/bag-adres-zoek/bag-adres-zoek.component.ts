/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {Adres} from '../../model/adres';
import {MatLegacyTableDataSource as MatTableDataSource} from '@angular/material/legacy-table';
import {InputFormFieldBuilder} from '../../../shared/material-form-builder/form-components/input/input-form-field-builder';
import {CustomValidators} from '../../../shared/validators/customValidators';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {BAGService} from '../../bag.service';
import {ListAdressenParameters} from '../../model/list-adressen-parameters';
import {ConfirmDialogComponent, ConfirmDialogData} from '../../../shared/confirm-dialog/confirm-dialog.component';
import {MatLegacyDialog as MatDialog} from '@angular/material/legacy-dialog';
import {TranslateService} from '@ngx-translate/core';
import {AbstractFormControlField} from '../../../shared/material-form-builder/model/abstract-form-control-field';

@Component({
    selector: 'zac-bag-adres-zoek',
    templateUrl: './bag-adres-zoek.component.html',
    styleUrls: ['./bag-adres-zoek.component.less']
})
export class BagAdresZoekComponent implements OnInit {

    @Output() bagObject = new EventEmitter<Adres>();

    postcodeFormField: AbstractFormControlField;
    huisnummerFormField: AbstractFormControlField;
    adressen: MatTableDataSource<Adres> = new MatTableDataSource<Adres>();
    loading = false;
    formGroup: FormGroup;
    adressenColumns: string[] = ['straat', 'huisnummer', 'postcode', 'woonplaats', 'acties'];

    constructor(private bagService: BAGService, private formBuilder: FormBuilder, private dialog: MatDialog, private translate: TranslateService) { }

    ngOnInit(): void {
        this.postcodeFormField = new InputFormFieldBuilder().id('postcode').label('postcode')
                                                            .validators(Validators.required, CustomValidators.postcode)
                                                            .maxlength(7)
                                                            .build();
        this.huisnummerFormField = new InputFormFieldBuilder().id('huisnummer').label('huisnummer')
                                                              .validators(Validators.required, Validators.min(1), Validators.max(99999))
                                                              .maxlength(5)
                                                              .build();

        this.formGroup = this.formBuilder.group({
            postcode: this.postcodeFormField.formControl,
            huisnummer: this.huisnummerFormField.formControl
        });
    }

    isValid(): boolean {
        return this.formGroup.valid;
    }

    zoekAdres(): void {
        this.loading = true;
        this.adressen.data = [];
        this.bagService.listAdressen(
            new ListAdressenParameters(this.postcodeFormField.formControl.value, this.huisnummerFormField.formControl.value))
            .subscribe(adressen => {
                this.adressen.data = adressen.resultaten;
                this.loading = false;
            });
    }

    selectAdres(adres: Adres): void {
        this.dialog.open(ConfirmDialogComponent, {
            data: new ConfirmDialogData(
                this.translate.instant('msg.bagobject.koppelen.bevestigen'))
        }).afterClosed().subscribe(confirmed => {
            if (confirmed) {
                this.bagObject.emit(adres);
            }
        });
    }
}
