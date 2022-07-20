/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AbstractFormField} from '../../../shared/material-form-builder/model/abstract-form-field';
import {InputFormFieldBuilder} from '../../../shared/material-form-builder/form-components/input/input-form-field-builder';
import {MatTableDataSource} from '@angular/material/table';
import {CustomValidators} from '../../../shared/validators/customValidators';
import {Bedrijf} from '../../model/bedrijven/bedrijf';
import {SelectFormFieldBuilder} from '../../../shared/material-form-builder/form-components/select/select-form-field-builder';
import {ListBedrijvenParameters} from '../../model/bedrijven/list-bedrijven-parameters';
import {KlantenService} from '../../klanten.service';
import {SelectFormField} from '../../../shared/material-form-builder/form-components/select/select-form-field';

@Component({
    selector: 'zac-bedrijf-zoek',
    templateUrl: './bedrijf-zoek.component.html',
    styleUrls: ['./bedrijf-zoek.component.less']
})
export class BedrijfZoekComponent implements OnInit {
    @Input() betrokkeneTypeField: SelectFormField;
    @Output() bedrijf = new EventEmitter<Bedrijf>();
    bedrijven: MatTableDataSource<Bedrijf> = new MatTableDataSource<Bedrijf>();
    foutmelding: string;
    formGroup: FormGroup;
    bedrijfColumns: string[] = ['naam', 'kvk', 'vestigingsnummer', 'type', 'adres', 'acties'];
    loading = false;

    types = ['HOOFDVESTIGING', 'NEVENVESTIGING'];

    kvkFormField: AbstractFormField;
    vestigingsnummerFormField: AbstractFormField;
    handelsnaamFormField: AbstractFormField;
    typeFormField: AbstractFormField;
    postcodeFormField: AbstractFormField;
    huisnummerFormField: AbstractFormField;
    plaatsFormField: AbstractFormField;

    constructor(private klantenService: KlantenService, private formBuilder: FormBuilder) {
    }

    ngOnInit(): void {
        this.handelsnaamFormField = new InputFormFieldBuilder().id('handelsnaam').label('handelsnaam').maxlength(100)
                                                               .validators(CustomValidators.handelsnaam).build();
        this.kvkFormField = new InputFormFieldBuilder().id('kvknummer').label('kvknummer')
                                                       .validators(CustomValidators.kvk).maxlength(8).build();
        this.vestigingsnummerFormField = new InputFormFieldBuilder().id('vestigingsnummer').label('vestigingsnummer')
                                                                    .validators(CustomValidators.vestigingsnummer)
                                                                    .maxlength(100).build();
        this.postcodeFormField = new InputFormFieldBuilder().id('postcode').label('postcode')
                                                            .validators(CustomValidators.postcode).maxlength(7).build();
        this.typeFormField = new SelectFormFieldBuilder().id('type').label('type').options(this.types).build();
        this.huisnummerFormField = new InputFormFieldBuilder().id('huisnummer').label('huisnummer')
                                                              .validators(Validators.min(1), Validators.max(99999))
                                                              .maxlength(5).build();
        this.plaatsFormField = new InputFormFieldBuilder().id('plaats').label('plaats').maxlength(50).build();
        this.formGroup = this.formBuilder.group({
            kvkNummer: this.kvkFormField.formControl,
            handelsnaam: this.handelsnaamFormField.formControl,
            vestigingsnummer: this.vestigingsnummerFormField.formControl,
            postcode: this.postcodeFormField.formControl,
            huisnummer: this.huisnummerFormField.formControl,
            plaats: this.plaatsFormField.formControl,
            type: this.typeFormField.formControl
        });
    }

    isValid(): boolean {
        if (!this.formGroup.valid || (this.betrokkeneTypeField && !this.betrokkeneTypeField.formControl.valid)) {
            return false;
        }

        const kvkNummer = this.kvkFormField.formControl.value;
        const handelsnaam = this.handelsnaamFormField.formControl.value;
        const vestigingsnummer = this.vestigingsnummerFormField.formControl.value;
        const postcode = this.postcodeFormField.formControl.value;
        const huisnummer = this.huisnummerFormField.formControl.value;

        return postcode ? huisnummer : huisnummer ? postcode : kvkNummer || handelsnaam || vestigingsnummer;
    }

    createListParameters(): ListBedrijvenParameters {
        return this.formGroup.value as ListBedrijvenParameters;
    }

    zoekBedrijven() {
        this.loading = true;
        this.bedrijven.data = [];
        this.klantenService.listBedrijven(this.createListParameters()).subscribe(bedrijven => {
            this.bedrijven.data = bedrijven.resultaten;
            this.foutmelding = bedrijven.foutmelding;
            this.loading = false;
        });
    }
}
