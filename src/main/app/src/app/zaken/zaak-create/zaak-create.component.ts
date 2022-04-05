/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit, ViewChild} from '@angular/core';
import {FormGroup, Validators} from '@angular/forms';
import {Zaak} from '../model/zaak';
import {ZakenService} from '../zaken.service';
import {Router} from '@angular/router';
import {FormConfig} from '../../shared/material-form-builder/model/form-config';
import * as moment from 'moment/moment';
import {NavigationService} from '../../shared/navigation/navigation.service';
import {UtilService} from '../../core/service/util.service';
import {Vertrouwelijkheidaanduiding} from '../../informatie-objecten/model/vertrouwelijkheidaanduiding.enum';
import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';
import {Subject} from 'rxjs';
import {HeadingFormFieldBuilder} from '../../shared/material-form-builder/form-components/heading/heading-form-field-builder';
import {SelectFormFieldBuilder} from '../../shared/material-form-builder/form-components/select/select-form-field-builder';
import {DateFormFieldBuilder} from '../../shared/material-form-builder/form-components/date/date-form-field-builder';
import {InputFormFieldBuilder} from '../../shared/material-form-builder/form-components/input/input-form-field-builder';
import {TextareaFormFieldBuilder} from '../../shared/material-form-builder/form-components/textarea/textarea-form-field-builder';
import {FormConfigBuilder} from '../../shared/material-form-builder/model/form-config-builder';
import {MatSidenav} from '@angular/material/sidenav';
import {InputFormField} from '../../shared/material-form-builder/form-components/input/input-form-field';
import {CustomValidators} from '../../shared/validators/customValidators';
import {ActionIcon} from '../../shared/edit/action-icon';
import {Geometry} from '../model/geometry';
import {GeometryCoordinate} from '../model/geometryCoordinate';
import {GeometryType} from '../model/geometryType';
import {Klant} from '../../klanten/model/klant';

@Component({
    templateUrl: './zaak-create.component.html',
    styleUrls: ['./zaak-create.component.less']
})
export class ZaakCreateComponent implements OnInit {

    createZaakFields: Array<AbstractFormField[]>;
    formConfig: FormConfig;
    @ViewChild('actionsSideNav') actionsSidenav: MatSidenav;
    actions = {
        GEEN: 'GEEN',
        ZOEK_PERSOON: 'ZOEK_PERSOON',
        ZOEK_BEDRIJF: 'ZOEK_BEDRIJF',
        ZOEK_LOCATIE: 'ZOEK_LOCATIE'
    };
    action: string;
    private initiatorField: InputFormField;
    private locatieField: InputFormField;

    private persoonToevoegenIcon = new ActionIcon('emoji_people', new Subject<void>());
    private bedrijfToevoegenIcon = new ActionIcon('business', new Subject<void>());
    private locatieToevoegenIcon = new ActionIcon('place', new Subject<void>());

    private locatie: { naam: string, coordinates: GeometryCoordinate };

    constructor(private zakenService: ZakenService, private router: Router, private navigation: NavigationService, private utilService: UtilService) {
    }

    ngOnInit(): void {
        this.persoonToevoegenIcon.iconClicked.subscribe(this.iconNext(this.actions.ZOEK_PERSOON));
        this.bedrijfToevoegenIcon.iconClicked.subscribe(this.iconNext(this.actions.ZOEK_BEDRIJF));
        this.locatieToevoegenIcon.iconClicked.subscribe(this.iconNext(this.actions.ZOEK_LOCATIE));

        this.utilService.setTitle('title.zaak.aanmaken');

        this.formConfig = new FormConfigBuilder().saveText('actie.aanmaken').cancelText('actie.annuleren').build();
        const communicatiekanalen = this.zakenService.listCommunicatiekanalen();
        const vertrouwelijkheidaanduidingen = this.utilService.getEnumAsSelectList('vertrouwelijkheidaanduiding',
            Vertrouwelijkheidaanduiding);

        const titel = new HeadingFormFieldBuilder().id('aanmakenZaak').label('actie.zaak.aanmaken').level('1').build();

        const tussenTitel = new HeadingFormFieldBuilder().id('overigegegevens').label('gegevens.overig').level('2').build();

        const zaaktype = new SelectFormFieldBuilder().id('zaaktype').label('zaaktype')
                                                     .validators(Validators.required)
                                                     .optionLabel('omschrijving').options(this.zakenService.listZaaktypes())
                                                     .build();

        const startdatum = new DateFormFieldBuilder().id('startdatum').label('startdatum')
                                                     .value(moment()).validators(Validators.required).build();

        const registratiedatum = new DateFormFieldBuilder().id('registratiedatum').label('registratiedatum').value(moment()).build();

        this.initiatorField = new InputFormFieldBuilder().id('initiatorIdentificatie').icons([this.bedrijfToevoegenIcon, this.persoonToevoegenIcon])
                                                         .label('initiator')
                                                         .validators(CustomValidators.bsnOrVesPrefixed).build();

        const communicatiekanaal = new SelectFormFieldBuilder().id('communicatiekanaal').label('communicatiekanaal')
                                                               .optionLabel('naam').options(communicatiekanalen)
                                                               .build();

        const vertrouwelijkheidaanduiding = new SelectFormFieldBuilder().id('vertrouwelijkheidaanduiding').label('vertrouwelijkheidaanduiding')
                                                                        .optionLabel('label').options(vertrouwelijkheidaanduidingen).build();

        const omschrijving = new InputFormFieldBuilder().id('omschrijving').label('omschrijving').build();
        const toelichting = new TextareaFormFieldBuilder().id('toelichting').label('toelichting').build();

        this.locatieField = new InputFormFieldBuilder().id('zaakgeometrie').icon(this.locatieToevoegenIcon).label('locatie').build();

        this.createZaakFields = [[titel], [zaaktype, this.initiatorField], [startdatum, registratiedatum, this.locatieField], [tussenTitel],
            [communicatiekanaal, vertrouwelijkheidaanduiding], [omschrijving], [toelichting]];

    }

    onFormSubmit(formGroup: FormGroup): void {
        if (formGroup) {
            const zaak: Zaak = new Zaak();
            Object.keys(formGroup.controls).forEach((key) => {
                if (key === 'vertrouwelijkheidaanduiding') {
                    zaak[key] = formGroup.controls[key].value?.value;
                } else {
                    zaak[key] = formGroup.controls[key].value;
                }
                if (key === 'initiatorIdentificatie' && formGroup.controls[key].value) {
                    const val = formGroup.controls[key].value;
                    const prefix = val.indexOf('|');
                    zaak[key] = val.substring(0, prefix !== -1 ? prefix : val.length).trim();
                }

                if (key === 'zaakgeometrie') {
                    const zaakgeometrie = new Geometry(GeometryType.POINT);
                    zaakgeometrie.point = new GeometryCoordinate(this.locatie.coordinates[0], this.locatie.coordinates[1]);
                    zaak[key] = zaakgeometrie;
                }
            });
            this.zakenService.createZaak(zaak).subscribe(newZaak => {
                this.router.navigate(['/zaken/', newZaak.identificatie]);
            });
        } else {
            this.navigation.back();
        }
    }

    initiatorGeselecteerd(initiator: Klant): void {
        this.initiatorField.formControl.setValue(initiator.identificatie + ' | ' + initiator.naam);
        this.actionsSidenav.close();
    }

    locatieGeselecteerd(locatie: { naam: string, coordinates: GeometryCoordinate }): void {
        this.locatie = locatie;
        this.locatieField.formControl.setValue(locatie.naam);
        this.actionsSidenav.close();
    }

    private iconNext(action) {
        return () => {
            this.action = action;
            this.actionsSidenav.open();
        };
    }

}

