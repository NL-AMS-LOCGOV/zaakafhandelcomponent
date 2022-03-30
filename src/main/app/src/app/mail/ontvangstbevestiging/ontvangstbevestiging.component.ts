/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {FormGroup, Validators} from '@angular/forms';
import {MailObject} from '../model/mailobject';
import {FormConfigBuilder} from '../../shared/material-form-builder/model/form-config-builder';
import {InputFormFieldBuilder} from '../../shared/material-form-builder/form-components/input/input-form-field-builder';
import {CustomValidators} from '../../shared/validators/customValidators';
import {TextareaFormFieldBuilder} from '../../shared/material-form-builder/form-components/textarea/textarea-form-field-builder';
import {ZakenService} from '../../zaken/zaken.service';
import {InformatieObjectenService} from '../../informatie-objecten/informatie-objecten.service';
import {ActivatedRoute, Router} from '@angular/router';
import {NavigationService} from '../../shared/navigation/navigation.service';
import {HttpClient} from '@angular/common/http';
import {MailService} from '../mail.service';
import {TakenService} from '../../taken/taken.service';
import {UtilService} from '../../core/service/util.service';
import {FormConfig} from '../../shared/material-form-builder/model/form-config';
import {Zaak} from '../../zaken/model/zaak';
import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';
import {TranslateService} from '@ngx-translate/core';

@Component({
    templateUrl: './ontvangstbevestiging.component.html'
})
export class OntvangstbevestigingComponent implements OnInit {

    zaakUuid: string;
    formConfig: FormConfig;
    zaak: Zaak;
    fields: Array<AbstractFormField[]>;

    constructor(private zakenService: ZakenService,
                private informatieObjectenService: InformatieObjectenService,
                private route: ActivatedRoute,
                private router: Router,
                private navigation: NavigationService,
                private http: HttpClient,
                private mailService: MailService,
                public takenService: TakenService,
                public utilService: UtilService,
                public translateService: TranslateService) { }

    ngOnInit(): void {
        this.formConfig = new FormConfigBuilder().saveText('actie.versturen').cancelText('actie.annuleren').build();
        this.zaakUuid = this.route.snapshot.paramMap.get('zaakUuid');

        this.zakenService.readZaak(this.zaakUuid).subscribe(zaak => {
            this.zaak = zaak;
            this.utilService.setTitle('title.ontvangstbevestiging.versturen', {zaak: zaak.identificatie});

            const ontvangstOnderwerp = this.translateService.instant('msg.ontvangstbevestiging.onderwerp',
                {zaakIdentificatie: zaak.identificatie});
            const ontvangstBericht = this.translateService.instant('msg.ontvangstbevestiging.bericht',
                {zaakIdentificatie: zaak.identificatie, behandelaar: zaak.behandelaar.naam});

            const ontvanger = new InputFormFieldBuilder().id('ontvanger')
                                                         .label('ontvanger')
                                                         .validators(Validators.required, CustomValidators.email)
                                                         .build();
            const onderwerp = new InputFormFieldBuilder().id('onderwerp')
                                                         .label('onderwerp')
                                                         .value(ontvangstOnderwerp)
                                                         .validators(Validators.required)
                                                         .build();
            const body = new TextareaFormFieldBuilder().id('body')
                                                       .label('body')
                                                       .value(ontvangstBericht)
                                                       .validators(Validators.required)
                                                       .build();

            this.fields = [[ontvanger], [onderwerp], [body]];
        });
    }

    onFormSubmit(formGroup: FormGroup): void {
        if (formGroup) {
            const mailObject = new MailObject();
            mailObject.ontvanger = formGroup.controls['ontvanger'].value;
            mailObject.onderwerp = formGroup.controls['onderwerp'].value;
            mailObject.body = formGroup.controls['body'].value;
            mailObject.createDocumentFromMail = true;

            this.mailService.sendAcknowledgeReceipt(this.zaakUuid, mailObject).subscribe(() => {
                this.utilService.openSnackbar('msg.email.verstuurd');
                this.router.navigate(['/zaken/', this.zaak.identificatie]);
            });
        } else {
            this.navigation.back();
        }
    }

}
