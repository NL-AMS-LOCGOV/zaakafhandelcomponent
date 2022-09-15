/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AbstractFormField} from '../../../shared/material-form-builder/model/abstract-form-field';
import {DateFormFieldBuilder} from '../../../shared/material-form-builder/form-components/date/date-form-field-builder';
import {InputFormFieldBuilder} from '../../../shared/material-form-builder/form-components/input/input-form-field-builder';
import {ListPersonenParameters} from '../../model/personen/list-personen-parameters';
import {KlantenService} from '../../klanten.service';
import {Persoon} from '../../model/personen/persoon';
import {MatTableDataSource} from '@angular/material/table';
import {CustomValidators} from '../../../shared/validators/customValidators';
import {SelectFormField} from '../../../shared/material-form-builder/form-components/select/select-form-field';
import {InputFormField} from '../../../shared/material-form-builder/form-components/input/input-form-field';

@Component({
    selector: 'zac-persoon-zoek',
    templateUrl: './persoon-zoek.component.html',
    styleUrls: ['./persoon-zoek.component.less']
})
export class PersoonZoekComponent implements OnInit {
    @Input() betrokkeneRoltypeField: SelectFormField;
    @Input() betrokkeneToelichtingField: InputFormField;
    @Output() persoon = new EventEmitter<Persoon>();
    foutmelding: string;
    bsnFormField: AbstractFormField;
    geslachtsnaamFormField: AbstractFormField;
    voornamenFormField: AbstractFormField;
    voorvoegselFormField: AbstractFormField;
    geboortedatumFormField: AbstractFormField;
    gemeenteCodeFormField: AbstractFormField;
    straatFormFiled: AbstractFormField;
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
        this.voornamenFormField = new InputFormFieldBuilder().id('voornamen').label('voornamen').maxlength(50).build();
        this.geslachtsnaamFormField = new InputFormFieldBuilder('Me*').id('achternaam').label('achternaam')
                                                                 .maxlength(50).build();
        this.voorvoegselFormField = new InputFormFieldBuilder().id('voorvoegsel').label('voorvoegsel').maxlength(10).build();
        this.geboortedatumFormField = new DateFormFieldBuilder().id('geboortedatum').label('geboortedatum').build();
        this.gemeenteCodeFormField = new InputFormFieldBuilder('0599').id('gemeenteCode').label('gemeenteCode')
                                                                .validators(Validators.min(1), Validators.max(9999))
                                                                .maxlength(4).build();
        this.straatFormFiled = new InputFormFieldBuilder().id('straat').label('straat').maxlength(55).build();
        this.postcodeFormField = new InputFormFieldBuilder().id('postcode').label('postcode')
                                                            .validators(CustomValidators.postcode).maxlength(7).build();

        this.huisnummerFormField = new InputFormFieldBuilder().id('huisnummer').label('huisnummer')
                                                              .validators(Validators.min(1), Validators.max(99999))
                                                              .maxlength(5).build();

        this.formGroup = this.formBuilder.group({
            bsn: this.bsnFormField.formControl,
            geslachtsnaam: this.geslachtsnaamFormField.formControl,
            voornamen: this.voornamenFormField.formControl,
            voorvoegsel: this.voorvoegselFormField.formControl,
            geboortedatum: this.geboortedatumFormField.formControl,
            gemeenteVanInschrijving: this.gemeenteCodeFormField.formControl,
            straat: this.straatFormFiled.formControl,
            postcode: this.postcodeFormField.formControl,
            huisnummer: this.huisnummerFormField.formControl
        });
    }

    isValid(): boolean {
        if (!this.formGroup.valid ||
            (this.betrokkeneRoltypeField && !this.betrokkeneRoltypeField.formControl.valid) ||
            (this.betrokkeneToelichtingField && !this.betrokkeneToelichtingField.formControl.valid)) {
            return false;
        }

        const bsn = this.bsnFormField.formControl.value;
        const geslachtsnaam = this.geslachtsnaamFormField.formControl.value;
        const geboortedatum = this.geboortedatumFormField.formControl.value;
        const postcode = this.postcodeFormField.formControl.value;
        const huisnummer = this.huisnummerFormField.formControl.value;
        const gemeenteCode = this.gemeenteCodeFormField.formControl.value;
        const straat = this.straatFormFiled.formControl.value;

        return bsn || (geslachtsnaam && (geboortedatum || gemeenteCode)) || (postcode && huisnummer) || (straat && huisnummer && gemeenteCode);
    }

    createListPersonenParameters(): ListPersonenParameters {
        const params = new ListPersonenParameters();
        for (const [k, v] of Object.entries(this.formGroup.value)) {
            if (v) {
                params[k] = v;
            }
        }
        return params;
    }

    zoekPersonen(): void {
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
