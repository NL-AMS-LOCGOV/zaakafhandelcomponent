/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {FormGroup, Validators} from '@angular/forms';
import {FormConfigBuilder} from '../../shared/material-form-builder/model/form-config-builder';
import {InputFormFieldBuilder} from '../../shared/material-form-builder/form-components/input/input-form-field-builder';
import {FormConfig} from '../../shared/material-form-builder/model/form-config';
import {ActivatedRoute, Router} from '@angular/router';
import {UtilService} from '../../core/service/util.service';
import {Zaak} from '../../zaken/model/zaak';
import {ZakenService} from '../../zaken/zaken.service';
import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';
import {TextareaFormFieldBuilder} from '../../shared/material-form-builder/form-components/textarea/textarea-form-field-builder';
import {InformatieObjectenService} from '../../informatie-objecten/informatie-objecten.service';
import {NavigationService} from '../../shared/navigation/navigation.service';
import {HttpClient} from '@angular/common/http';
import {TakenService} from '../../taken/taken.service';
import {Medewerker} from '../../identity/model/medewerker';
import {IdentityService} from '../../identity/identity.service';
import {MailService} from '../mail.service';
import {MailObject} from '../model/mailobject';

@Component({
    templateUrl: './mail-create.component.html'
})
export class MailCreateComponent implements OnInit {

    zaakUuid: string;
    formConfig: FormConfig;
    zaak: Zaak;
    fields: Array<AbstractFormField[]>;
    ingelogdeMedewerker: Medewerker;

    constructor(private zakenService: ZakenService,
                private informatieObjectenService: InformatieObjectenService,
                private route: ActivatedRoute,
                private router: Router,
                private navigation: NavigationService,
                private http: HttpClient,
                private identityService: IdentityService,
                private mailService: MailService,
                public takenService: TakenService,
                public utilService: UtilService) {
    }

    ngOnInit(): void {
        this.formConfig = new FormConfigBuilder().saveText('actie.versturen').cancelText('actie.annuleren').build();
        this.zaakUuid = this.route.snapshot.paramMap.get('zaakUuid');
        this.identityService.readIngelogdeMedewerker().subscribe(medewerker => {
            this.ingelogdeMedewerker = medewerker;
        });

        this.zakenService.readZaak(this.zaakUuid).subscribe(zaak => {
            this.zaak = zaak;
            this.utilService.setTitle('title.mail.versturen', {zaak: zaak.identificatie});
            const ontvanger = new InputFormFieldBuilder().id('ontvanger').label('ontvanger')
                                                     .validators(Validators.required).build();

            const onderwerp = new InputFormFieldBuilder().id('onderwerp').label('onderwerp')
                                                         .validators(Validators.required).build();


            const body = new TextareaFormFieldBuilder().id('body').label('body')
                                                      .validators(Validators.required).build();

            this.fields = [[ontvanger], [onderwerp], [body]];
        });
    }

    onFormSubmit(formGroup: FormGroup): void {
        if (formGroup) {
            const mailObject = new MailObject();
            mailObject.ontvanger = formGroup.controls['ontvanger'].value;
            mailObject.onderwerp = formGroup.controls['onderwerp'].value;
            mailObject.body = formGroup.controls['body'].value;

            this.mailService.sendMail(this.zaakUuid, mailObject).subscribe(status => {
                this.router.navigate(['/zaken/', this.zaak.identificatie]);
            });
        } else {
            this.navigation.back();
        }
    }
}
