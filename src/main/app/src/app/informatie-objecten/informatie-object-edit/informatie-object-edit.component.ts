/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {EnkelvoudigInformatieobject} from '../model/enkelvoudig-informatieobject';
import {ZakenService} from '../../zaken/zaken.service';
import {InformatieObjectenService} from '../informatie-objecten.service';
import {UtilService} from '../../core/service/util.service';
import {MatSidenav} from '@angular/material/sidenav';
import {InformatieobjectStatus} from '../model/informatieobject-status.enum';
import {FormConfigBuilder} from '../../shared/material-form-builder/model/form-config-builder';
import {Vertrouwelijkheidaanduiding} from '../model/vertrouwelijkheidaanduiding.enum';
import {InputFormFieldBuilder} from '../../shared/material-form-builder/form-components/input/input-form-field-builder';
import {FormGroup, Validators} from '@angular/forms';
import {DateFormFieldBuilder} from '../../shared/material-form-builder/form-components/date/date-form-field-builder';
import {SelectFormFieldBuilder} from '../../shared/material-form-builder/form-components/select/select-form-field-builder';
import {NavigationService} from '../../shared/navigation/navigation.service';
import {ConfiguratieService} from '../../configuratie/configuratie.service';
import {TranslateService} from '@ngx-translate/core';
import {IdentityService} from '../../identity/identity.service';
import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';
import {Informatieobjecttype} from '../model/informatieobjecttype';
import {FormConfig} from '../../shared/material-form-builder/model/form-config';
import {User} from '../../identity/model/user';
import {FormComponent} from '../../shared/material-form-builder/form/form/form.component';
import {ZaakInformatieobject} from '../model/zaak-informatieobject';
import {EnkelvoudigInformatieObjectVersieGegevens} from '../model/enkelvoudig-informatie-object-versie-gegevens';
import {FileFormFieldBuilder} from '../../shared/material-form-builder/form-components/file/file-form-field-builder';

@Component({
    selector: 'zac-informatie-object-edit',
    templateUrl: './informatie-object-edit.component.html',
    styleUrls: ['./informatie-object-edit.component.less']
})
export class InformatieObjectEditComponent implements OnInit {

    @Input() infoObject: EnkelvoudigInformatieObjectVersieGegevens;
    @Input() sideNav: MatSidenav;
    @Input() zaken: ZaakInformatieobject[];
    @Output() document = new EventEmitter<EnkelvoudigInformatieobject>();

    @ViewChild(FormComponent) form: FormComponent;

    fields: Array<AbstractFormField[]>;
    informatieobjecttypes: Informatieobjecttype[];
    formConfig: FormConfig;
    ingelogdeMedewerker: User;

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

        const inhoudField = new FileFormFieldBuilder().id('bestandsnaam').label('bestandsnaam')
                                                      .uploadURL(
                                                          this.informatieObjectenService.getUploadURL(this.zaken[0].zaakUuid))
                                                      .validators(Validators.required)
                                                      .build();

        const titel = new InputFormFieldBuilder().id('titel').label('titel')
                                                 .validators(Validators.required)
                                                 .value(this.infoObject.titel)
                                                 .build();

        const beschrijving = new InputFormFieldBuilder().id('beschrijving')
                                                        .label('beschrijving')
                                                        .value(this.infoObject.beschrijving)
                                                        .build();

        const taal = new SelectFormFieldBuilder().id('taal').label('taal')
                                                 .value({
                                                     naam: this.translateService.instant(this.infoObject.taal.naam),
                                                     value: this.infoObject.taal
                                                 })
                                                 .optionLabel('naam').options(this.configuratieService.listTalen())
                                                 .validators(Validators.required)
                                                 .build();

        let status;
        if (this.infoObject.status) {
            status = new SelectFormFieldBuilder().id('status').label('status')
                                                       .validators(Validators.required)
                                                       .optionLabel('label').options(informatieobjectStatussen)
                                                       .value({
                                                           label: this.translateService.instant(
                                                               'informatieobject.status.' + this.infoObject.status.toUpperCase()),
                                                           value: this.infoObject.status.toUpperCase()
                                                       })
                                                       .build();
        } else {
            status = new SelectFormFieldBuilder().id('status').label('status')
                                                 .validators(Validators.required)
                                                 .optionLabel('label').options(informatieobjectStatussen)
                                                 .build();
        }

        const verzenddatum = new DateFormFieldBuilder().id('verzenddatum')
                                                       .label('verzenddatum')
                                                       .value(this.infoObject.verzenddatum)
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
                                                          .value({
                                                              label: this.translateService.instant(
                                                                  'vertrouwelijkheidaanduiding.' + this.infoObject.vertrouwelijkheidaanduiding.toUpperCase()),
                                                              value: this.infoObject.vertrouwelijkheidaanduiding.toUpperCase()
                                                          })
                                                          .build();

        const toelichting = new InputFormFieldBuilder().id('toelichting').label('toelichting').build();

        let vorigeBestandsnaam = null;
        inhoudField.fileUploaded.subscribe(bestandsnaam => {
            const titelCtrl = titel.formControl;
            titelCtrl.setValue(bestandsnaam.replace(/\.[^/.]+$/, ''));
            vorigeBestandsnaam = '' + titelCtrl.value;
        });

        this.fields = [[inhoudField], [titel], [beschrijving], [status, vertrouwelijk], [auteur], [verzenddatum, taal], [toelichting]];
    }

    onFormSubmit(formGroup: FormGroup): void {
        if (formGroup) {
            const nieuweVersie = new EnkelvoudigInformatieObjectVersieGegevens();
            nieuweVersie.uuid = this.infoObject.uuid;
            nieuweVersie.zaakUuid = this.zaken[0].zaakUuid;
            Object.keys(formGroup.controls).forEach((key) => {
                const control = formGroup.controls[key];
                const value = control.value;
                if (key === 'status' || key === 'vertrouwelijkheidaanduiding') {
                    nieuweVersie[key] = this.infoObject[key] = value.value.toUpperCase();
                } else {
                    nieuweVersie[key] = this.infoObject[key] = value;
                }
            });

            this.informatieObjectenService.partialUpdateEnkelvoudigInformatieobject(nieuweVersie)
                .subscribe((document) => {
                    this.document.emit(document);
                    this.utilService.openSnackbar('msg.document.nieuwe.versie.toegevoegd');
                    this.ngOnInit();
                    this.sideNav.close();
                    this.form.reset();
                });
        } else {
            this.sideNav.close();
        }
    }

    private getIngelogdeMedewerker() {
        this.identityService.readLoggedInUser().subscribe(ingelogdeMedewerker => {
            this.ingelogdeMedewerker = ingelogdeMedewerker;
        });
    }

}
