/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {FormGroup, Validators} from '@angular/forms';
import {Zaak} from '../model/zaak';
import {ZakenService} from '../zaken.service';
import {Router} from '@angular/router';
import {Zaaktype} from '../model/zaaktype';
import {MaterialFormBuilderService} from '../../shared/material-form-builder/material-form-builder.service';
import {FormItem} from '../../shared/material-form-builder/model/form-item';
import {FormFieldConfig} from '../../shared/material-form-builder/model/form-field-config';
import {FormConfig} from '../../shared/material-form-builder/model/form-config';
import * as moment from 'moment/moment';
import {NavigationService} from '../../shared/navigation/navigation.service';
import {UtilService} from '../../core/service/util.service';
import {Vertrouwelijkheidaanduiding} from '../../informatie-objecten/model/vertrouwelijkheidaanduiding.enum';

@Component({
    templateUrl: './zaak-create.component.html',
    styleUrls: ['./zaak-create.component.less']
})
export class ZaakCreateComponent implements OnInit {

    createZaakFields: Array<FormItem[]>;
    formConfig: FormConfig;

    constructor(private zakenService: ZakenService, private mfbService: MaterialFormBuilderService, private router: Router, private navigation: NavigationService
        , private utilService: UtilService) {
    }

    ngOnInit(): void {
        this.utilService.setTitle('title.zaak.aanmaken');
        this.formConfig = new FormConfig('actie.versturen', 'actie.annuleren');
        this.zakenService.getZaaktypes().subscribe(value => {
            const zaaktypes: Zaaktype[] = value;
            const communicatiekanalen: object[] = [{id: 'test1', doel: 'test1'}, {id: 'test2', doel: 'test2'}];
            const vertrouwelijkheidaanduidingen = this.utilService.getEnumAsSelectList('vertrouwelijkheidaanduiding',
                Vertrouwelijkheidaanduiding);

            const titel = this.mfbService.createHeadingFormItem('aanmakenZaak', 'actie.zaak.aanmaken', '1');
            const tussenTitel = this.mfbService.createHeadingFormItem('overigegegevens', 'gegevens.overig', '2');

            const zaaktype = this.mfbService.createSelectFormItem('zaaktype', 'zaaktype', null, 'omschrijving', zaaktypes,
                new FormFieldConfig([Validators.required]));

            const startdatum = this.mfbService.createDateFormItem('startdatum', 'startdatum', moment(),
                new FormFieldConfig([Validators.required]));
            const registratiedatum = this.mfbService.createDateFormItem('registratiedatum', 'registratiedatum', moment());

            const communicatiekanaal = this.mfbService.createSelectFormItem('communicatiekanaal', 'communicatiekanaal', null,
                'doel', communicatiekanalen);
            const vertrouwelijkheidaanduiding = this.mfbService.createSelectFormItem('vertrouwelijkheidaanduiding',
                'vertrouwelijkheidaanduiding', null, 'label', vertrouwelijkheidaanduidingen);
            const omschrijving =
                this.mfbService.createInputFormItem('omschrijving', 'omschrijving', null);
            const toelichting =
                this.mfbService.createTextareaFormItem('toelichting', 'toelichting', null);
            this.createZaakFields = [[titel], [zaaktype], [startdatum, registratiedatum], [tussenTitel],
                [communicatiekanaal, vertrouwelijkheidaanduiding], [omschrijving], [toelichting]];
        });
    }

    onFormSubmit(formGroup: FormGroup): void {
        if (formGroup) {
            const zaak: Zaak = new Zaak();
            Object.keys(formGroup.controls).forEach((key) => {
                zaak[key] = formGroup.controls[key].value;
            });
            this.zakenService.postZaak(zaak).subscribe(newZaak => {
                this.router.navigate(['/zaken/', newZaak.uuid]);
            });
        } else {
            this.navigation.back();
        }
    }
}

