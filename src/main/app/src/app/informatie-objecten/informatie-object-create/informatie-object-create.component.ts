/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ZakenService} from '../../zaken/zaken.service';
import {InformatieObjectenService} from '../informatie-objecten.service';
import {UtilService} from '../../core/service/util.service';
import {Zaak} from '../../zaken/model/zaak';
import {FormConfig} from '../../shared/material-form-builder/model/form-config';
import {FormGroup, Validators} from '@angular/forms';
import {FormFieldConfig} from '../../shared/material-form-builder/model/form-field-config';
import {EnkelvoudigInformatieObject} from '../model/enkelvoudig-informatie-object';
import * as moment from 'moment/moment';
import {Informatieobjecttype} from '../model/informatieobjecttype';
import {Taal} from '../model/taal.enum';
import {Vertrouwelijkheidaanduiding} from '../model/vertrouwelijkheidaanduiding.enum';
import {InformatieobjectStatus} from '../model/informatieobject-status.enum';
import {NavigationService} from '../../shared/navigation/navigation.service';
import {FileFieldConfig} from '../../shared/material-form-builder/model/file-field-config';
import {InputFormField} from '../../shared/material-form-builder/form-components/input/input-form-field';
import {FileFormField} from '../../shared/material-form-builder/form-components/file/file-form-field';
import {DateFormField} from '../../shared/material-form-builder/form-components/date/date-form-field';
import {SelectFormField} from '../../shared/material-form-builder/form-components/select/select-form-field';
import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';
import {Observable, of} from 'rxjs';

@Component({
    templateUrl: './informatie-object-create.component.html',
    styleUrls: ['./informatie-object-create.component.less']
})
export class InformatieObjectCreateComponent implements OnInit {

    zaakUuid: string;
    zaak: Zaak;
    fields: Array<AbstractFormField[]>;
    informatieobjecttypes: Informatieobjecttype[];
    formConfig: FormConfig;

    constructor(private zakenService: ZakenService,
                private informatieObjectenService: InformatieObjectenService,
                private route: ActivatedRoute,
                private router: Router,
                private navigation: NavigationService,
                public utilService: UtilService) {
    }

    ngOnInit(): void {
        this.formConfig = new FormConfig('actie.versturen', 'actie.annuleren');
        this.zaakUuid = this.route.snapshot.paramMap.get('zaakUuid');

        let vertrouwelijkheidsAanduidingen = this.utilService.getEnumAsSelectList('vertrouwelijkheidaanduiding', Vertrouwelijkheidaanduiding);
        let talen = this.utilService.getEnumAsSelectList('taal', Taal);
        let informatieobjectStatussen = this.utilService.getEnumAsSelectList('informatieobject.status', InformatieobjectStatus);
        this.zakenService.getZaak(this.zaakUuid).subscribe(zaak => {
            this.zaak = zaak;
            this.utilService.setTitle('title.document.aanmaken', {zaak: zaak.identificatie});
            const types = this.getTypes(zaak);
            const titel = new InputFormField('titel', 'titel', null, this.required());
            const beschrijving = new InputFormField('beschrijving', 'beschrijving', null);
            const inhoud = new FileFormField('bestandsnaam', 'bestandsnaam', null, this.fileUploadConfig());
            const beginRegistratie = new DateFormField('creatiedatum', 'creatiedatum', moment(), this.required());
            const taal = new SelectFormField('taal', 'taal', talen[0], 'label', talen, this.required());
            const status = new SelectFormField('status', 'status', informatieobjectStatussen[0], 'label',
                informatieobjectStatussen);
            const documentType = new SelectFormField('informatieobjectType', 'informatieobjectType', null, null, types, this.required());
            const auteur = new InputFormField('auteur', 'auteur', null, this.required());
            const vertrouwelijk = new SelectFormField('vertrouwelijkheidaanduiding', 'vertrouwelijkheidaanduiding',
                vertrouwelijkheidsAanduidingen[0],
                'label', vertrouwelijkheidsAanduidingen, this.required());

            this.fields =
                [[titel], [beschrijving], [inhoud], [documentType, vertrouwelijk, beginRegistratie], [auteur, status, taal]];
        });
    }

    onFormSubmit(formGroup: FormGroup): void {
        if (formGroup) {
            const infoObject = new EnkelvoudigInformatieObject();
            Object.keys(formGroup.controls).forEach((key) => {
                let control = formGroup.controls[key];
                let value = control.value;
                if (value instanceof moment) {
                    infoObject[key] = value; //conversie niet nodig, ISO-8601 in UTC gaat goed met java ZonedDateTime.parse
                } else if (key == 'informatieobjectType') {
                    infoObject[key] = this.informatieobjecttypes[value].url;
                } else if (key == 'taal' || key == 'status' || key == 'vertrouwelijkheidaanduiding') {
                    infoObject[key] = value.value;
                } else {
                    infoObject[key] = value;
                }
            });

            this.informatieObjectenService.postEnkelvoudigInformatieObject(this.zaak.uuid, infoObject).subscribe(newObject => {
                this.router.navigate(['/informatie-objecten', newObject]);
            });
        } else {
            this.navigation.back();
        }
    }

    getTypes(zaak): Observable<string[]> {
        let types = [];
        this.informatieObjectenService.getInformatieobjecttypes(zaak.zaaktype.uuid).subscribe(response => {
            this.informatieobjecttypes = [];
            response.forEach(type => {
                this.informatieobjecttypes[type.omschrijving] = type;
                types.push(type.omschrijving);
            });
        });
        return of(types);
    }

    required(): FormFieldConfig {
        return new FormFieldConfig([Validators.required]);
    }

    fileUploadConfig(): FileFieldConfig {
        const uploadUrl = this.informatieObjectenService.uploadUrl.replace('{zaakUuid}', this.zaakUuid);
        return new FileFieldConfig(uploadUrl, [Validators.required], 1);
    }
}
