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
import {InformatieobjectZoekParameters} from '../../informatie-objecten/model/informatieobject-zoek-parameters';
import {HtmlEditorFormFieldBuilder} from '../../shared/material-form-builder/form-components/html-editor/html-editor-form-field-builder';
import {AbstractFormControlField} from '../../shared/material-form-builder/model/abstract-form-control-field';

@Component({
    selector: 'zac-mail-create',
    templateUrl: './mail-create.component.html',
    styleUrls: ['./mail-create.component.less']
})
export class MailCreateComponent implements OnInit {

    fieldNames = {
        ONTVANGER: 'ontvanger',
        BODY: 'body',
        ONDERWERP: 'onderwerp',
        BIJLAGEN: 'bijlagen'
    };

    formConfig: FormConfig;
    @Input() zaak: Zaak;
    @Output() mailVerstuurd = new EventEmitter<boolean>();
    fields: Array<AbstractFormField[]>;
    ingelogdeMedewerker: User;

    ontvangerFormField: AbstractFormControlField;
    onderwerpFormField: AbstractFormControlField;
    bodyFormField: AbstractFormControlField;
    bijlagenFormField: AbstractFormControlField;

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
        const zoekparameters = new InformatieobjectZoekParameters();
        zoekparameters.zaakUUID = this.zaak.uuid;
        const documenten = this.informatieObjectenService.listEnkelvoudigInformatieobjecten(zoekparameters);
        this.ontvangerFormField = new InputFormFieldBuilder()
        .id(this.fieldNames.ONTVANGER)
        .label(this.fieldNames.ONTVANGER)
        .validators(Validators.required, CustomValidators.emails)
        .maxlength(200)
        .build();
        this.onderwerpFormField = new HtmlEditorFormFieldBuilder()
        .id(this.fieldNames.ONDERWERP)
        .label(this.fieldNames.ONDERWERP)
        .emptyToolbar()
        .validators(Validators.required)
        .maxlength(100)
        .build();
        this.bodyFormField = new HtmlEditorFormFieldBuilder()
        .id(this.fieldNames.BODY)
        .label(this.fieldNames.BODY)
        .validators(Validators.required)
        .maxlength(1000)
        .build();
        this.bijlagenFormField = new DocumentenLijstFieldBuilder()
        .id(this.fieldNames.BIJLAGEN)
        .label(this.fieldNames.BIJLAGEN)
        .documenten(documenten)
        .build();
        this.fields = [[this.ontvangerFormField], [this.onderwerpFormField], [this.bodyFormField], [this.bijlagenFormField]];
    }

    onFormSubmit(formGroup: FormGroup): void {
        if (formGroup && formGroup.valid) {
            const mailObject = new MailObject();
            mailObject.ontvanger = this.ontvangerFormField.formControl.value;
            mailObject.onderwerp = this.onderwerpFormField.formControl.value;
            mailObject.body = this.bodyFormField.formControl.value;
            mailObject.bijlagen = this.bijlagenFormField.formControl.value;
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
