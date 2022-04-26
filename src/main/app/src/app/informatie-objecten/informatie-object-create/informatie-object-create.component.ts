/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {ZakenService} from '../../zaken/zaken.service';
import {InformatieObjectenService} from '../informatie-objecten.service';
import {UtilService} from '../../core/service/util.service';
import {Zaak} from '../../zaken/model/zaak';
import {FormConfig} from '../../shared/material-form-builder/model/form-config';
import {FormGroup, Validators} from '@angular/forms';
import {EnkelvoudigInformatieobject} from '../model/enkelvoudig-informatieobject';
import * as moment from 'moment/moment';
import {Informatieobjecttype} from '../model/informatieobjecttype';
import {Vertrouwelijkheidaanduiding} from '../model/vertrouwelijkheidaanduiding.enum';
import {InformatieobjectStatus} from '../model/informatieobject-status.enum';
import {NavigationService} from '../../shared/navigation/navigation.service';
import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';
import {InputFormFieldBuilder} from '../../shared/material-form-builder/form-components/input/input-form-field-builder';
import {FileFormFieldBuilder} from '../../shared/material-form-builder/form-components/file/file-form-field-builder';
import {DateFormFieldBuilder} from '../../shared/material-form-builder/form-components/date/date-form-field-builder';
import {SelectFormFieldBuilder} from '../../shared/material-form-builder/form-components/select/select-form-field-builder';
import {FormConfigBuilder} from '../../shared/material-form-builder/model/form-config-builder';
import {ConfiguratieService} from '../../configuratie/configuratie.service';
import {TranslateService} from '@ngx-translate/core';
import {Medewerker} from '../../identity/model/medewerker';
import {IdentityService} from '../../identity/identity.service';
import {CheckboxFormFieldBuilder} from '../../shared/material-form-builder/form-components/checkbox/checkbox-form-field-builder';
import {FormComponent} from '../../shared/material-form-builder/form/form/form.component';
import {MatSidenav} from '@angular/material/sidenav';

@Component({
    selector: 'zac-informatie-object-create',
    templateUrl: './informatie-object-create.component.html',
    styleUrls: ['./informatie-object-create.component.less']
})
export class InformatieObjectCreateComponent implements OnInit {

    @Input() zaak: Zaak;
    @Input() sideNav: MatSidenav;
    @Output() document = new EventEmitter<EnkelvoudigInformatieobject>();

    @ViewChild(FormComponent) form: FormComponent;

    fields: Array<AbstractFormField[]>;
    informatieobjecttypes: Informatieobjecttype[];
    formConfig: FormConfig;
    ingelogdeMedewerker: Medewerker;

    constructor(private zakenService: ZakenService,
                private informatieObjectenService: InformatieObjectenService,
                private navigation: NavigationService,
                public utilService: UtilService,
                private configuratieService: ConfiguratieService,
                private translateService: TranslateService,
                private identityService: IdentityService) {
    }

    ngOnInit(): void {
        this.formConfig = new FormConfigBuilder().saveText('actie.toevoegen').cancelText('actie.annuleren').build();
        this.getIngelogdeMedewerker();

        const vertrouwelijkheidsAanduidingen = this.utilService.getEnumAsSelectList('vertrouwelijkheidaanduiding', Vertrouwelijkheidaanduiding);
        const informatieobjectStatussen = this.utilService.getEnumAsSelectList('informatieobject.status', InformatieobjectStatus);

        const titel = new InputFormFieldBuilder().id('titel').label('titel')
                                                 .validators(Validators.required)
                                                 .build();

        const beschrijving = new InputFormFieldBuilder().id('beschrijving')
                                                        .label('beschrijving')
                                                        .build();

        const inhoudField = new FileFormFieldBuilder().id('bestandsnaam').label('bestandsnaam')
                                                      .uploadURL(
                                                          this.informatieObjectenService.getUploadURL(this.zaak.uuid))
                                                      .validators(Validators.required)
                                                      .build();

        const beginRegistratie = new DateFormFieldBuilder().id('creatiedatum')
                                                           .label('creatiedatum')
                                                           .value(moment())
                                                           .validators(Validators.required)
                                                           .build();

        const taal = new SelectFormFieldBuilder().id('taal').label('taal')
                                                 .value$(this.configuratieService.defaultTaal())
                                                 .optionLabel('naam').options(this.configuratieService.listTalen())
                                                 .validators(Validators.required)
                                                 .build();

        const status = new SelectFormFieldBuilder().id('status').label('status')
                                                   .validators(Validators.required)
                                                   .optionLabel('label').options(informatieobjectStatussen)
                                                   .build();

        const documentType = new SelectFormFieldBuilder().id('informatieobjectType')
                                                         .label('informatieobjectType')
                                                         .options(
                                                             this.informatieObjectenService.listInformatieobjecttypes(
                                                                 this.zaak.zaaktype.uuid))
                                                         .optionLabel('omschrijving')
                                                         .validators(Validators.required)
                                                         .build();

        const auteur = new InputFormFieldBuilder().id('auteur').label('auteur')
                                                  .validators(Validators.required)
                                                  .value(this.ingelogdeMedewerker.naam)
                                                  .build();

        const vertrouwelijk = new SelectFormFieldBuilder().id('vertrouwelijkheidaanduiding')
                                                          .label('vertrouwelijkheidaanduiding')
                                                          .optionLabel('label')
                                                          .options(vertrouwelijkheidsAanduidingen)
                                                          .validators(Validators.required)
                                                          .build();

        const nogmaals = new CheckboxFormFieldBuilder().id('nogmaals')
                                                       .label(this.translateService.instant(
                                                           'actie.document.aanmaken.nogmaals'))
                                                       .build();

        this.fields =
            [[inhoudField], [titel], [beschrijving], [documentType, vertrouwelijk], [status, beginRegistratie], [auteur, taal], [nogmaals]];

        let vorigeBestandsnaam = null;
        inhoudField.fileUploaded.subscribe(bestandsnaam => {
            const titelCtrl = titel.formControl;
            if (!titelCtrl.value || titelCtrl.value === vorigeBestandsnaam) {
                titelCtrl.setValue(bestandsnaam.replace(/\.[^/.]+$/, ''));
                vorigeBestandsnaam = '' + titelCtrl.value;
            }
        });

        documentType.formControl.valueChanges.subscribe(value => {
            if (value) {
                vertrouwelijk.formControl.setValue(vertrouwelijkheidsAanduidingen.find(option => option.value === value.vertrouwelijkheidaanduiding));
            }
        });

    }

    onFormSubmit(formGroup: FormGroup): void {
        if (formGroup) {
            const infoObject = new EnkelvoudigInformatieobject();
            Object.keys(formGroup.controls).forEach((key) => {
                const control = formGroup.controls[key];
                const value = control.value;
                if (value instanceof moment) {
                    infoObject[key] = value; // conversie niet nodig, ISO-8601 in UTC gaat goed met java ZonedDateTime.parse
                } else if (key === 'informatieobjectType') {
                    infoObject[key] = value.url;
                } else if (key === 'taal') {
                    infoObject[key] = value.code;
                } else if (key === 'status' || key === 'vertrouwelijkheidaanduiding') {
                    infoObject[key] = value.value;
                } else {
                    infoObject[key] = value;
                }
            });

            this.informatieObjectenService.createEnkelvoudigInformatieobject(this.zaak.uuid, infoObject)
                .subscribe((document) => {
                    this.document.emit(document);
                    this.utilService.openSnackbar('msg.document.toegevoegd.aan.zaak');
                    if (formGroup.get('nogmaals').value) {
                        this.resetForm(formGroup);
                    } else {
                        this.clearForm(formGroup);
                        this.sideNav.close();
                    }
                });
        }
    }

    resetForm(formGroup: FormGroup) {
        formGroup.get('bestandsnaam').reset();
        formGroup.get('bestandsnaam').setErrors(null);
        formGroup.get('titel').reset();
        formGroup.get('titel').setErrors(null);
        formGroup.get('beschrijving').reset();
        formGroup.get('nogmaals').setValue(false);
        this.form.reset();
        formGroup.setErrors({invalid: true});
    }

    clearForm(formGroup: FormGroup) {
        formGroup.get('status').reset();
        formGroup.get('status').setErrors(null);
        formGroup.get('vertrouwelijkheidaanduiding').reset();
        formGroup.get('vertrouwelijkheidaanduiding').setErrors(null);
        formGroup.get('informatieobjectType').reset();
        formGroup.get('informatieobjectType').setErrors(null);
        this.resetForm(formGroup);
    }

    private getIngelogdeMedewerker() {
        this.identityService.readIngelogdeMedewerker().subscribe(ingelogdeMedewerker => {
            this.ingelogdeMedewerker = ingelogdeMedewerker;
        });
    }
}
