/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {AbstractFormField} from '../../../shared/material-form-builder/model/abstract-form-field';
import {Nummeraanduiding} from '../../model/nummeraanduiding';
import {MatTableDataSource} from '@angular/material/table';
import {InputFormFieldBuilder} from '../../../shared/material-form-builder/form-components/input/input-form-field-builder';
import {CustomValidators} from '../../../shared/validators/customValidators';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {BAGService} from '../../bag.service';
import {ListNummeraanduidingenParameters} from '../../model/list-nummeraanduidingen-parameters';

@Component({
    selector: 'zac-bag-zoek',
    templateUrl: './bag-zoek.component.html',
    styleUrls: ['./bag-zoek.component.less']
})
export class BagZoekComponent implements OnInit {

    @Output() bagObject = new EventEmitter<Nummeraanduiding>();

    postcodeFormField: AbstractFormField;
    huisnummerFormField: AbstractFormField;
    nummeraanduidingen: MatTableDataSource<Nummeraanduiding> = new MatTableDataSource<Nummeraanduiding>();
    loading = false;
    formGroup: FormGroup;
    nummeraanduidingColumns: string[] = ['straat', 'huisnummer', 'postcode', 'woonplaats', 'acties'];

    constructor(private bagService: BAGService, private formBuilder: FormBuilder) { }

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

    zoekNummeraanduidingen(): void {
        this.loading = true;
        this.nummeraanduidingen.data = [];
        this.bagService.listNummeraanduidingen(
            new ListNummeraanduidingenParameters(this.postcodeFormField.formControl.value, this.huisnummerFormField.formControl.value))
            .subscribe(nummeraanduidingen => {
                this.nummeraanduidingen.data = nummeraanduidingen.resultaten;
                this.loading = false;
            });
    }

    selectNummeraanduiding(nummeraanduiding: Nummeraanduiding): void {
        this.bagObject.emit(nummeraanduiding);
    }
}
