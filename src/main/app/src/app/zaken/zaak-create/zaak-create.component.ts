/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {FormGroup, Validators} from '@angular/forms';
import {Zaak} from '../model/zaak';
import {ZakenService} from '../zaken.service';
import {Router} from '@angular/router';
import {FormConfig} from '../../shared/material-form-builder/model/form-config';
import * as moment from 'moment/moment';
import {NavigationService} from '../../shared/navigation/navigation.service';
import {UtilService} from '../../core/service/util.service';
import {Vertrouwelijkheidaanduiding} from '../../informatie-objecten/model/vertrouwelijkheidaanduiding.enum';
import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';
import {of} from 'rxjs';
import {HeadingFormFieldBuilder} from '../../shared/material-form-builder/form-components/heading/heading-form-field-builder';
import {SelectFormFieldBuilder} from '../../shared/material-form-builder/form-components/select/select-form-field-builder';
import {DateFormFieldBuilder} from '../../shared/material-form-builder/form-components/date/date-form-field-builder';
import {InputFormFieldBuilder} from '../../shared/material-form-builder/form-components/input/input-form-field-builder';
import {TextareaFormFieldBuilder} from '../../shared/material-form-builder/form-components/textarea/textarea-form-field-builder';
import {FormConfigBuilder} from '../../shared/material-form-builder/model/form-config-builder';
import {TranslateService} from '@ngx-translate/core';

@Component({
    templateUrl: './zaak-create.component.html',
    styleUrls: ['./zaak-create.component.less']
})
export class ZaakCreateComponent implements OnInit {

    createZaakFields: Array<AbstractFormField[]>;
    formConfig: FormConfig;

    constructor(private zakenService: ZakenService, private router: Router, private navigation: NavigationService, private utilService: UtilService,
                private translate: TranslateService) {
    }

    ngOnInit(): void {
        this.utilService.setTitle('title.zaak.aanmaken');

        this.formConfig = new FormConfigBuilder().saveText('actie.aanmaken').cancelText('actie.annuleren').build();
        const communicatiekanalen = of([{id: 'test1', doel: 'test1'}, {id: 'test2', doel: 'test2'}]);
        const vertrouwelijkheidaanduidingen = this.utilService.getEnumAsSelectList('vertrouwelijkheidaanduiding',
            Vertrouwelijkheidaanduiding);

        const titel = new HeadingFormFieldBuilder().id('aanmakenZaak').label('actie.zaak.aanmaken').level('1').build();

        const tussenTitel = new HeadingFormFieldBuilder().id('overigegegevens').label('gegevens.overig').level('2').build();

        const zaaktype = new SelectFormFieldBuilder().id('zaaktype').label('zaaktype')
                                                     .validators(Validators.required)
                                                     .optionLabel('omschrijving').options(this.zakenService.listZaaktypes())
                                                     .build();

        const startdatum = new DateFormFieldBuilder().id('startdatum').label('startdatum')
                                                     .value(moment()).validators(Validators.required).build();

        const registratiedatum = new DateFormFieldBuilder().id('registratiedatum').label('registratiedatum').value(moment()).build();

        const communicatiekanaal = new SelectFormFieldBuilder().id('communicatiekanaal').label('communicatiekanaal')
                                                               .optionLabel('doel').options(communicatiekanalen)
                                                               .build();

        const vertrouwelijkheidaanduiding = new SelectFormFieldBuilder().id('vertrouwelijkheidaanduiding').label('vertrouwelijkheidaanduiding')
                                                                        .optionLabel('label').options(vertrouwelijkheidaanduidingen).build();

        const omschrijving = new InputFormFieldBuilder().id('omschrijving').label('omschrijving').build();
        const toelichting = new TextareaFormFieldBuilder().id('toelichting').label('toelichting').build();
        this.createZaakFields = [[titel], [zaaktype], [startdatum, registratiedatum], [tussenTitel],
            [communicatiekanaal, vertrouwelijkheidaanduiding], [omschrijving], [toelichting]];
    }

    onFormSubmit(formGroup: FormGroup): void {
        if (formGroup) {
            const zaak: Zaak = new Zaak();
            Object.keys(formGroup.controls).forEach((key) => {
                if (key === 'vertrouwelijkheidaanduiding') {
                    zaak[key] = formGroup.controls[key].value?.value;
                } else {
                    zaak[key] = formGroup.controls[key].value;
                }
            });
            this.zakenService.createZaak(zaak).subscribe(newZaak => {
                this.router.navigate(['/zaken/', newZaak.uuid]);
            });
        } else {
            this.navigation.back();
        }
    }
}

