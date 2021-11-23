/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {FormGroup, Validators} from '@angular/forms';
import {Zaak} from '../model/zaak';
import {ZakenService} from '../zaken.service';
import {Router} from '@angular/router';
import {FormFieldConfig} from '../../shared/material-form-builder/model/form-field-config';
import {FormConfig} from '../../shared/material-form-builder/model/form-config';
import * as moment from 'moment/moment';
import {NavigationService} from '../../shared/navigation/navigation.service';
import {UtilService} from '../../core/service/util.service';
import {Vertrouwelijkheidaanduiding} from '../../informatie-objecten/model/vertrouwelijkheidaanduiding.enum';
import {HeadingFormField} from '../../shared/material-form-builder/form-components/heading/heading-form-field';
import {SelectFormField} from '../../shared/material-form-builder/form-components/select/select-form-field';
import {DateFormField} from '../../shared/material-form-builder/form-components/date/date-form-field';
import {InputFormField} from '../../shared/material-form-builder/form-components/input/input-form-field';
import {TextareaFormField} from '../../shared/material-form-builder/form-components/textarea/textarea-form-field';
import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';
import {of} from 'rxjs';

@Component({
    templateUrl: './zaak-create.component.html',
    styleUrls: ['./zaak-create.component.less']
})
export class ZaakCreateComponent implements OnInit {

    createZaakFields: Array<AbstractFormField[]>;
    formConfig: FormConfig;

    constructor(private zakenService: ZakenService, private router: Router, private navigation: NavigationService, private utilService: UtilService) {
    }

    ngOnInit(): void {
        this.utilService.setTitle('title.zaak.aanmaken');
        this.formConfig = new FormConfig('actie.versturen', 'actie.annuleren');
        const communicatiekanalen = of([{id: 'test1', doel: 'test1'}, {id: 'test2', doel: 'test2'}]);
        const vertrouwelijkheidaanduidingen = this.utilService.getEnumAsSelectList('vertrouwelijkheidaanduiding',
            Vertrouwelijkheidaanduiding);

        const titel = new HeadingFormField('aanmakenZaak', 'actie.zaak.aanmaken', '1');
        const tussenTitel = new HeadingFormField('overigegegevens', 'gegevens.overig', '2');

        const zaaktype = new SelectFormField('zaaktype', 'zaaktype', null, 'omschrijving', this.zakenService.getZaaktypes(),
            new FormFieldConfig([Validators.required]));

        const startdatum = new DateFormField('startdatum', 'startdatum', moment(),
            new FormFieldConfig([Validators.required]));
        const registratiedatum = new DateFormField('registratiedatum', 'registratiedatum', moment());

        const communicatiekanaal = new SelectFormField('communicatiekanaal', 'communicatiekanaal', null,
            'doel', communicatiekanalen);
        const vertrouwelijkheidaanduiding = new SelectFormField('vertrouwelijkheidaanduiding',
            'vertrouwelijkheidaanduiding', null, 'label', vertrouwelijkheidaanduidingen);
        const omschrijving = new InputFormField('omschrijving', 'omschrijving', null);
        const toelichting = new TextareaFormField('toelichting', 'toelichting', null);
        this.createZaakFields = [[titel], [zaaktype], [startdatum, registratiedatum], [tussenTitel],
            [communicatiekanaal, vertrouwelijkheidaanduiding], [omschrijving], [toelichting]];
    }

    onFormSubmit(formGroup: FormGroup): void {
        if (formGroup) {
            const zaak: Zaak = new Zaak();
            Object.keys(formGroup.controls).forEach((key) => {
                if (key == 'vertrouwelijkheidaanduiding') {
                    zaak[key] = formGroup.controls[key].value?.value;
                } else {
                    zaak[key] = formGroup.controls[key].value;
                }
            });
            this.zakenService.postZaak(zaak).subscribe(newZaak => {
                this.router.navigate(['/zaken/', newZaak.uuid]);
            });
        } else {
            this.navigation.back();
        }
    }
}

