/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AbstractFormField} from '../../../shared/material-form-builder/model/abstract-form-field';
import {DateFormFieldBuilder} from '../../../shared/material-form-builder/form-components/date/date-form-field-builder';
import {InputFormFieldBuilder} from '../../../shared/material-form-builder/form-components/input/input-form-field-builder';
import {ListPersonenParameters} from '../../model/personen/list-personen-parameters';
import {KlantenService} from '../../klanten.service';
import {Persoon} from '../../model/personen/persoon';
import {MatTableDataSource} from '@angular/material/table';
import {CustomValidators} from '../../../shared/validators/customValidators';

@Component({
    selector: 'zac-persoon-zoek',
    templateUrl: './persoon-zoek.component.html',
    styleUrls: ['./persoon-zoek.component.less']
})
export class PersoonZoekComponent implements OnInit {
    @Output() persoon = new EventEmitter<Persoon>();
    foutmelding: string;
    bsnFormField: AbstractFormField;
    geslachtsnaamFormField: AbstractFormField;
    geboortedatumFormField: AbstractFormField;
    gemeenteCodeFormField: AbstractFormField;
    postcodeFormField: AbstractFormField;
    huisnummerFormField: AbstractFormField;
    formGroup: FormGroup;
    personen: MatTableDataSource<Persoon> = new MatTableDataSource<Persoon>();
    persoonColumns: string[] = ['bsn', 'naam', 'geboortedatum', 'inschrijfadres', 'acties'];
    loading = false;

    constructor(private klantenService: KlantenService, private formBuilder: FormBuilder) {
    }

    ngOnInit(): void {
        this.bsnFormField = new InputFormFieldBuilder().id('bsn').label('bsn').validators(CustomValidators.bsn)
                                                       .maxlength(9).build();
        this.geslachtsnaamFormField = new InputFormFieldBuilder().id('geslachtsnaam').label('geslachtsnaam')
                                                                 .value('Me*').maxlength(50).build();
        this.geboortedatumFormField = new DateFormFieldBuilder().id('geboortedatum').label('geboortedatum')
                                                                .maxlength(10).build();
        this.gemeenteCodeFormField = new InputFormFieldBuilder().id('gemeenteCode').label('gemeenteCode')
                                                                .value('0599')
                                                                .validators(Validators.min(1), Validators.max(9999))
                                                                .maxlength(4).build();
        this.postcodeFormField = new InputFormFieldBuilder().id('postcode').label('postcode')
                                                            .validators(CustomValidators.postcode).maxlength(7).build();
        this.huisnummerFormField = new InputFormFieldBuilder().id('huisnummer').label('huisnummer')
                                                              .validators(Validators.min(1), Validators.max(99999))
                                                              .maxlength(5).build();

        this.formGroup = this.formBuilder.group({
            bsn: this.bsnFormField.formControl,
            geslachtsnaam: this.geslachtsnaamFormField.formControl,
            geboortedatum: this.geboortedatumFormField.formControl,
            gemeenteVanInschrijving: this.gemeenteCodeFormField.formControl,
            postcode: this.postcodeFormField.formControl,
            huisnummer: this.huisnummerFormField.formControl
        });
    }

    isValid(): boolean {
        if (!this.formGroup.valid) {
            return false;
        }
        const bsn = this.bsnFormField.formControl.value;
        const geslachtsnaam = this.geslachtsnaamFormField.formControl.value;
        const geboortedatum = this.geboortedatumFormField.formControl.value;
        const postcode = this.postcodeFormField.formControl.value;
        const huisnummer = this.huisnummerFormField.formControl.value;
        const gemeenteCode = this.gemeenteCodeFormField.formControl.value;
        return bsn || geboortedatum && geslachtsnaam || geslachtsnaam && gemeenteCode || postcode && huisnummer;
    }

    createListPersonenParameters(): ListPersonenParameters {
        return this.formGroup.value as ListPersonenParameters;
    }

    zoekPersonen() {
        this.loading = true;
        this.personen.data = [];
        this.klantenService.listPersonen(this.createListPersonenParameters()).subscribe(personen => {
            this.personen.data = personen.resultaten;
            this.foutmelding = personen.foutmelding;
            this.loading = false;
        });
    }

    selectPersoon(persoon: Persoon): void {
        this.persoon.emit(persoon);
    }
}
