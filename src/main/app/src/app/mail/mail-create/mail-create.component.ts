/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
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
import {User} from '../../identity/model/user';
import {IdentityService} from '../../identity/identity.service';
import {MailService} from '../mail.service';
import {MailObject} from '../model/mailobject';
import {CustomValidators} from '../../shared/validators/customValidators';
import {DocumentenLijstFieldBuilder} from '../../shared/material-form-builder/form-components/documenten-lijst/documenten-lijst-field-builder';
import {EnkelvoudigInformatieObjectZoekParameters} from '../../informatie-objecten/model/enkelvoudig-informatie-object-zoek-parameters';

@Component({
    selector: 'zac-mail-create',
    templateUrl: './mail-create.component.html',
    styleUrls: ['./mail-create.component.less']
})
export class MailCreateComponent implements OnInit {

    formConfig: FormConfig;
    @Input() zaak: Zaak;
    @Output() mailVerstuurd = new EventEmitter<boolean>();
    fields: Array<AbstractFormField[]>;
    ingelogdeMedewerker: User;

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
        this.identityService.readLoggedInUser().subscribe(medewerker => {
            this.ingelogdeMedewerker = medewerker;
        });
        const zoekparameters = new EnkelvoudigInformatieObjectZoekParameters();
        zoekparameters.zaakUUID = this.zaak.uuid;
        const documenten = this.informatieObjectenService.listEnkelvoudigInformatieobjecten(zoekparameters);
        const ontvanger = new InputFormFieldBuilder().id('ontvanger').label('ontvanger')
                                                     .validators(Validators.required, CustomValidators.emails)
                                                     .maxlength(200).build();
        const onderwerp = new InputFormFieldBuilder().id('onderwerp').label('onderwerp').validators(Validators.required)
                                                     .maxlength(100).build();
        const body = new TextareaFormFieldBuilder().id('body').label('body').validators(Validators.required)
                                                   .maxlength(1000).build();
        const bijlagen = new DocumentenLijstFieldBuilder().id('bijlagen').label('bijlagen')
                                          .documenten(documenten).build();
        this.fields = [[ontvanger], [onderwerp], [body], [bijlagen]];
    }

    onFormSubmit(formGroup: FormGroup): void {
        if (formGroup) {
            const mailObject = new MailObject();
            mailObject.ontvanger = formGroup.controls['ontvanger'].value;
            mailObject.onderwerp = formGroup.controls['onderwerp'].value;
            mailObject.body = formGroup.controls['body'].value;
            mailObject.bijlagen = JSON.parse(formGroup.controls['bijlagen'].value)?.selection;
            mailObject.createDocumentFromMail = true;

            this.mailService.sendMail(this.zaak.uuid, mailObject).subscribe(() => {
                this.utilService.openSnackbar('msg.email.verstuurd');
                this.mailVerstuurd.emit(true);
            });
        } else {
            this.mailVerstuurd.emit(false);
        }
    }
}
