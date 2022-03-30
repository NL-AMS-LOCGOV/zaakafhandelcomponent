/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
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
import {AbstractFileFormField} from '../../shared/material-form-builder/model/abstract-file-form-field';

@Component({
    templateUrl: './informatie-object-create.component.html',
    styleUrls: ['./informatie-object-create.component.less']
})
export class InformatieObjectCreateComponent implements OnInit {

    @ViewChild(FormComponent) form: FormComponent;

    zaakUuid: string;
    zaak: Zaak;
    fields: Array<AbstractFormField[]>;
    informatieobjecttypes: Informatieobjecttype[];
    formConfig: FormConfig;
    ingelogdeMedewerker: Medewerker;
    inhoudField: AbstractFileFormField;

    constructor(private zakenService: ZakenService,
                private informatieObjectenService: InformatieObjectenService,
                private route: ActivatedRoute,
                private router: Router,
                private navigation: NavigationService,
                public utilService: UtilService,
                private configuratieService: ConfiguratieService,
                private translateService: TranslateService,
                private identityService: IdentityService) {
    }

    ngOnInit(): void {
        this.formConfig = new FormConfigBuilder().saveText('actie.toevoegen').cancelText('actie.annuleren').build();
        this.zaakUuid = this.route.snapshot.paramMap.get('zaakUuid');
        this.getIngelogdeMedewerker();

        const vertrouwelijkheidsAanduidingen = this.utilService.getEnumAsSelectList('vertrouwelijkheidaanduiding', Vertrouwelijkheidaanduiding);
        const informatieobjectStatussen = this.utilService.getEnumAsSelectList('informatieobject.status', InformatieobjectStatus);

        this.zakenService.readZaak(this.zaakUuid).subscribe(zaak => {
            this.zaak = zaak;
            this.utilService.setTitle('title.document.aanmaken', {zaak: zaak.identificatie});

            const titel = new InputFormFieldBuilder().id('titel').label('titel')
                                                     .validators(Validators.required)
                                                     .build();

            const beschrijving = new InputFormFieldBuilder().id('beschrijving')
                                                            .label('beschrijving')
                                                            .build();

            this.inhoudField = new FileFormFieldBuilder().id('bestandsnaam').label('bestandsnaam')
                                                         .uploadURL(
                                                             this.informatieObjectenService.getUploadURL(this.zaakUuid))
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
                                                                     zaak.zaaktype.uuid))
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
                [[this.inhoudField], [titel], [beschrijving], [documentType, vertrouwelijk, beginRegistratie], [auteur, status, taal], [nogmaals]];

            let vorigeBestandsnaam = null;
            this.inhoudField.fileUploaded.subscribe(bestandsnaam => {
                const titelCtrl = titel.formControl;
                if (!titelCtrl.value || titelCtrl.value === vorigeBestandsnaam) {
                    titelCtrl.setValue(bestandsnaam.replace(/\.[^/.]+$/, ''));
                    vorigeBestandsnaam = '' + titelCtrl.value;
                }
            });

            documentType.formControl.valueChanges.subscribe(value => {
                vertrouwelijk.formControl.setValue(vertrouwelijkheidsAanduidingen.find(option => option.value === value.vertrouwelijkheidaanduiding));
            });
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
                .subscribe(() => {
                    this.utilService.openSnackbar('msg.document.toegevoegd.aan.zaak');
                    if (formGroup.get('nogmaals').value) {
                        this.resetForm(formGroup);
                    } else {
                        this.router.navigate(['/zaken/', this.zaak.identificatie]);
                    }
                });
        } else {
            this.navigation.back();
        }
    }

    resetForm(formGroup: FormGroup) {
        this.inhoudField.reset();
        this.inhoudField.formControl.setErrors(null);
        formGroup.get('titel').reset();
        formGroup.get('titel').setErrors(null);
        formGroup.get('beschrijving').reset();
        formGroup.get('nogmaals').setValue(false);
        this.form.reset();
        formGroup.setErrors({invalid: true});
    }

    private getIngelogdeMedewerker() {
        this.identityService.readIngelogdeMedewerker().subscribe(ingelogdeMedewerker => {
            this.ingelogdeMedewerker = ingelogdeMedewerker;
        });
    }
}
