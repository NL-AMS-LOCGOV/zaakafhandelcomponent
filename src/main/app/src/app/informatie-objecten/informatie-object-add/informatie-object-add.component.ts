/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, OnDestroy, OnInit, Output, ViewChild} from '@angular/core';
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
import {User} from '../../identity/model/user';
import {IdentityService} from '../../identity/identity.service';
import {CheckboxFormFieldBuilder} from '../../shared/material-form-builder/form-components/checkbox/checkbox-form-field-builder';
import {FormComponent} from '../../shared/material-form-builder/form/form/form.component';
import {MatDrawer} from '@angular/material/sidenav';
import {Taak} from '../../taken/model/taak';
import {FormFieldHint} from '../../shared/material-form-builder/model/form-field-hint';
import {Subscription} from 'rxjs';

@Component({
    selector: 'zac-informatie-object-add',
    templateUrl: './informatie-object-add.component.html',
    styleUrls: ['./informatie-object-add.component.less']
})
export class InformatieObjectAddComponent implements OnInit, OnDestroy {

    @Input() zaak: Zaak;
    @Input() taak: Taak;
    @Input() sideNav: MatDrawer;
    @Output() document = new EventEmitter<EnkelvoudigInformatieobject>();

    @ViewChild(FormComponent) form: FormComponent;

    fields: Array<AbstractFormField[]>;
    informatieobjecttypes: Informatieobjecttype[];
    formConfig: FormConfig;
    ingelogdeMedewerker: User;

    private subscriptions$: Subscription[] = [];

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
                                                 .maxlength(100)
                                                 .build();

        const beschrijving = new InputFormFieldBuilder().id('beschrijving')
                                                        .label('beschrijving')
                                                        .maxlength(100)
                                                        .build();

        const inhoudField = new FileFormFieldBuilder().id('bestandsnaam').label('bestandsnaam')
                                                      .uploadURL(this.zaak ?
                                                          this.informatieObjectenService.getUploadURL(this.zaak.uuid) :
                                                      this.informatieObjectenService.getUploadURL(this.taak.id))
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

        const informatieobjectType = new SelectFormFieldBuilder().id('informatieobjectTypeUUID')
                                                                 .label('informatieobjectType')
                                                                 .options(this.zaak ?
                                                                     this.informatieObjectenService.listInformatieobjecttypesForZaak(this.zaak.uuid) :
                                                                     this.informatieObjectenService.listInformatieobjecttypesForZaak(this.taak.zaakUuid))
                                                                 .optionLabel('omschrijving')
                                                                 .validators(Validators.required)
                                                                 .build();

        const auteur = new InputFormFieldBuilder().id('auteur').label('auteur')
                                                  .validators(Validators.required, Validators.pattern('\\S.*'))
                                                  .value(this.ingelogdeMedewerker.naam)
                                                  .maxlength(50)
                                                  .build();

        const vertrouwelijk = new SelectFormFieldBuilder().id('vertrouwelijkheidaanduiding')
                                                          .label('vertrouwelijkheidaanduiding')
                                                          .optionLabel('label')
                                                          .options(vertrouwelijkheidsAanduidingen)
                                                          .validators(Validators.required)
                                                          .build();

        const ontvangstDatum = new DateFormFieldBuilder().id('ontvangstdatum')
                                                         .label('ontvangstdatum')
                                                         .hint(new FormFieldHint(this.translateService.instant(
                                                             'msg.document.ontvangstdatum.hint')))
                                                         .build();

        const verzendDatum = new DateFormFieldBuilder().id('verzenddatum')
                                                       .label('verzenddatum')
                                                       .build();

        const nogmaals = new CheckboxFormFieldBuilder().id('nogmaals')
                                                       .label(this.translateService.instant(
                                                           'actie.document.toevoegen.nogmaals'))
                                                       .build();

        if (this.zaak) {
            this.fields =
                [[inhoudField], [titel], [beschrijving], [informatieobjectType, vertrouwelijk],
                    [status, beginRegistratie], [auteur, taal], [ontvangstDatum, verzendDatum], [nogmaals]];
        } else if (this.taak) {
            this.fields = [[inhoudField], [titel], [informatieobjectType], [ontvangstDatum, verzendDatum], [nogmaals]];
        }


        let vorigeBestandsnaam = null;
        this.subscriptions$.push(inhoudField.fileUploaded.subscribe(bestandsnaam => {
            const titelCtrl = titel.formControl;
            if (!titelCtrl.value || titelCtrl.value === vorigeBestandsnaam) {
                titelCtrl.setValue(bestandsnaam.replace(/\.[^/.]+$/, ''));
                vorigeBestandsnaam = '' + titelCtrl.value;
            }
        }));

        this.subscriptions$.push(informatieobjectType.formControl.valueChanges.subscribe(value => {
            if (value) {
                vertrouwelijk.formControl.setValue(vertrouwelijkheidsAanduidingen.find(option => option.value === value.vertrouwelijkheidaanduiding));
            }
        }));

        this.subscriptions$.push(ontvangstDatum.formControl.valueChanges.subscribe(value => {
            if (value && verzendDatum.formControl.enabled) {
                status.formControl.setValue(
                    informatieobjectStatussen.find(option => option.value === InformatieobjectStatus.DEFINITIEF));
                status.formControl.disable();
                verzendDatum.formControl.disable();
            } else if (!value && verzendDatum.formControl.disabled) {
                status.formControl.enable();
                verzendDatum.formControl.enable();
            }
        }));

        this.subscriptions$.push(verzendDatum.formControl.valueChanges.subscribe(value => {
            if (value && ontvangstDatum.formControl.enabled) {
                ontvangstDatum.formControl.disable();
            } else if (!value && ontvangstDatum.formControl.disabled) {
                ontvangstDatum.formControl.enable();
            }
        }));
    }

    ngOnDestroy(): void {
        for (const subscription of this.subscriptions$) {
            subscription.unsubscribe();
        }
    }

    onFormSubmit(formGroup: FormGroup): void {
        if (formGroup) {
            const infoObject = new EnkelvoudigInformatieobject();
            Object.keys(formGroup.controls).forEach((key) => {
                const control = formGroup.controls[key];
                const value = control.value;
                if (value instanceof moment) {
                    infoObject[key] = value; // conversie niet nodig, ISO-8601 in UTC gaat goed met java ZonedDateTime.parse
                } else if (key === 'informatieobjectTypeUUID') {
                    infoObject[key] = value.uuid;
                } else if (key === 'taal') {
                    infoObject[key] = value.code;
                } else if (key === 'status' || key === 'vertrouwelijkheidaanduiding') {
                    infoObject[key] = value.value;
                } else {
                    infoObject[key] = value;
                }
            });

            this.informatieObjectenService.createEnkelvoudigInformatieobject(this.zaak ? this.zaak.uuid : this.taak.zaakUuid,
                this.zaak ? this.zaak.uuid : this.taak.id, infoObject, !!this.taak)
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
        } else {
            this.resetAndClose();
        }
    }

    private resetAndClose() {
        this.fields.forEach(row => row.forEach(field => {
            // Alles leeg maken behalve de volgende 3 velden
            if (field.id !== 'auteur' && field.id !== 'creatiedatum' && field.id !== 'taal') {
                field.formControl.reset();
            }
        }));
        this.sideNav.close();
    }

    resetForm(formGroup: FormGroup) {
        if (this.zaak) {
            formGroup.get('beschrijving').reset();
        }
        formGroup.get('bestandsnaam').reset();
        formGroup.get('bestandsnaam').setErrors(null);
        formGroup.get('titel').reset();
        formGroup.get('titel').setErrors(null);
        formGroup.get('nogmaals').setValue(false);
        this.form.reset();
        formGroup.setErrors({invalid: true});
    }

    clearForm(formGroup: FormGroup) {
        if (this.zaak) {
            formGroup.get('status').reset();
            formGroup.get('status').setErrors(null);
            formGroup.get('vertrouwelijkheidaanduiding').reset();
            formGroup.get('vertrouwelijkheidaanduiding').setErrors(null);
        }
        formGroup.get('informatieobjectTypeUUID').reset();
        formGroup.get('informatieobjectTypeUUID').setErrors(null);
        formGroup.get('ontvangstdatum').reset();
        formGroup.get('ontvangstdatum').setErrors(null);
        formGroup.get('verzenddatum').reset();
        formGroup.get('verzenddatum').setErrors(null);
        this.resetForm(formGroup);
    }

    private getIngelogdeMedewerker() {
        this.identityService.readLoggedInUser().subscribe(ingelogdeMedewerker => {
            this.ingelogdeMedewerker = ingelogdeMedewerker;
        });
    }
}
