/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {FormGroup, Validators} from '@angular/forms';
import {FormConfigBuilder} from '../../shared/material-form-builder/model/form-config-builder';
import {InputFormFieldBuilder} from '../../shared/material-form-builder/form-components/input/input-form-field-builder';
import {EnkelvoudigInformatieobject} from '../../informatie-objecten/model/enkelvoudig-informatieobject';
import {FormConfig} from '../../shared/material-form-builder/model/form-config';
import {ActivatedRoute, Router} from '@angular/router';
import {UtilService} from '../../core/service/util.service';
import {Zaak} from '../../zaken/model/zaak';
import {ZakenService} from '../../zaken/zaken.service';
import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';
import {TextareaFormFieldBuilder} from '../../shared/material-form-builder/form-components/textarea/textarea-form-field-builder';
import {InformatieObjectenService} from '../../informatie-objecten/informatie-objecten.service';
import {NavigationService} from '../../shared/navigation/navigation.service';
import {Observable} from 'rxjs';
import {HttpClient, HttpEvent, HttpEventType, HttpHeaders} from '@angular/common/http';
import {TakenService} from '../../taken/taken.service';
import {InformatieobjectStatus} from '../../informatie-objecten/model/informatieobject-status.enum';
import {Vertrouwelijkheidaanduiding} from '../../informatie-objecten/model/vertrouwelijkheidaanduiding.enum';
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
            const ontvanger = new InputFormFieldBuilder().id('ontvanger').label('ontvanger').validators(Validators.required).build();
            const onderwerp = new InputFormFieldBuilder().id('onderwerp').label('onderwerp').validators(Validators.required).build();
            const body = new TextareaFormFieldBuilder().id('body').label('body').validators(Validators.required).build();
            this.fields = [[ontvanger], [onderwerp], [body]];
        });
    }

    onFormSubmit(formGroup: FormGroup): void {
        if (formGroup) {
            const mailObject = new MailObject();
            mailObject.ontvanger = formGroup.controls['ontvanger'].value;
            mailObject.onderwerp = formGroup.controls['onderwerp'].value;
            mailObject.body = formGroup.controls['body'].value;

            // Enkele waarden zijn hardcoded, dit is volgens user story eerste opzet mailfunctionaliteit.
            const infoObject = new EnkelvoudigInformatieobject();
            this.informatieObjectenService.listInformatieobjecttypesForZaak(this.zaakUuid).subscribe(types => {
                types.forEach(type => {
                   if (type.omschrijving === 'e-mail') {
                       infoObject.informatieobjectType = type.url;
                   }
                });
            });
            infoObject.status = InformatieobjectStatus.DEFINITIEF;
            infoObject.titel = mailObject.onderwerp;
            infoObject.bestandsnaam = mailObject.onderwerp + '.txt';
            infoObject.vertrouwelijkheidaanduiding = Vertrouwelijkheidaanduiding.OPENBAAR;
            infoObject.auteur = this.ingelogdeMedewerker.naam;
            infoObject.taal = 'NLD';
            infoObject.creatiedatum = new Date().toISOString();

            this.mailService.sendMail(mailObject).subscribe(status => {
                if (status === 200) {
                    const file = new File([mailObject.body as BlobPart], mailObject.onderwerp + '.txt');

                    this.createRequest(file).subscribe({
                        next: (event: HttpEvent<any>) => {
                            switch (event.type) {
                                case HttpEventType.Response:
                                    this.informatieObjectenService.createEnkelvoudigInformatieobject(this.zaak.uuid,
                                        infoObject).subscribe(newObject => {
                                        this.router.navigate(['/zaken/', this.zaakUuid]);
                                    });
                            }
                        }
                    });
                } else {
                    this.navigation.back();
                }
            });
        } else {
            this.navigation.back();
        }
    }

    createRequest(file: File): Observable<any> {
        const formData: FormData = new FormData();
        formData.append('filename', file.name);
        formData.append('filesize', file.size.toString());
        formData.append('type', 'text/plain');
        formData.append('file', file, file.name);
        const httpHeaders = new HttpHeaders();
        httpHeaders.append('Content-Type', 'multipart/form-data');
        httpHeaders.append('Accept', 'application/json');
        return this.http.post(this.informatieObjectenService.getUploadURL(this.zaakUuid), formData, {
            reportProgress: true,
            observe: 'events',
            headers: httpHeaders
        });
    }
}
