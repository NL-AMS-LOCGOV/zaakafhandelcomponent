/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
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
import {Klant} from '../../klanten/model/klanten/klant';
import {SideNavAction} from '../../shared/side-nav/side-nav-action';
import {LocationUtil} from '../../shared/location/location-util';
import {AddressResult} from '../../shared/location/location.service';
import {ZaakActies} from '../model/zaak-acties';

@Component({
    templateUrl: './zaak-create.component.html',
    styleUrls: ['./zaak-create.component.less']
})
export class ZaakCreateComponent implements OnInit {

    createZaakFields: Array<AbstractFormField[]>;
    formConfig: FormConfig;
    @ViewChild('actionsSideNav') actionsSidenav: MatSidenav;
    action: string;
    acties: ZaakActies;
    private initiatorField: InputFormField;
    private locatieField: InputFormField;

    private initiatorToevoegenIcon = new ActionIcon('person', new Subject<void>());
    private locatieToevoegenIcon = new ActionIcon('place', new Subject<void>());

    private locatie: AddressResult;

    constructor(private zakenService: ZakenService, private router: Router, private navigation: NavigationService, private utilService: UtilService) {
    }

    ngOnInit(): void {
        // Dummy policy acties, since there is no way yet to get acties for a new not yet existing case.
        this.acties = new ZaakActies();
        this.acties.toevoegenInitiatorPersoon = true;
        this.acties.toevoegenInitiatorBedrijf = true;

        this.initiatorToevoegenIcon.iconClicked.subscribe(this.iconNext(SideNavAction.ZOEK_INITIATOR));
        this.locatieToevoegenIcon.iconClicked.subscribe(this.iconNext(SideNavAction.ZOEK_LOCATIE));

        this.utilService.setTitle('title.zaak.aanmaken');

        this.formConfig = new FormConfigBuilder().saveText('actie.aanmaken').cancelText('actie.annuleren').build();
        const communicatiekanalen = this.zakenService.listCommunicatiekanalen();
        const vertrouwelijkheidaanduidingen = this.utilService.getEnumAsSelectList('vertrouwelijkheidaanduiding',
            Vertrouwelijkheidaanduiding);

        const titel = new HeadingFormFieldBuilder().id('aanmakenZaak').label('actie.zaak.aanmaken').level('1').build();

        const tussenTitel = new HeadingFormFieldBuilder().id('overigegegevens').label('gegevens.overig').level('2').build();

        const zaaktype = new SelectFormFieldBuilder().id('zaaktype').label('zaaktype')
                                                     .validators(Validators.required)
                                                     .optionLabel('omschrijving')
                                                     .options(this.zakenService.listZaaktypes())
                                                     .build();

        const startdatum = new DateFormFieldBuilder().id('startdatum').label('startdatum')
                                                     .value(moment()).validators(Validators.required)
                                                     .build();

        const registratiedatum = new DateFormFieldBuilder().id('registratiedatum').label('registratiedatum')
                                                           .value(moment()).build();

        this.initiatorField = new InputFormFieldBuilder().id('initiatorIdentificatie')
                                                         .icon(this.initiatorToevoegenIcon)
                                                         .label('initiator')
                                                         .validators(CustomValidators.bsnOrVesPrefixed)
                                                         .maxlength(70)
                                                         .build();

        const communicatiekanaal = new SelectFormFieldBuilder().id('communicatiekanaal').label('communicatiekanaal')
                                                               .optionLabel('naam').options(communicatiekanalen)
                                                               .build();

        const vertrouwelijkheidaanduiding = new SelectFormFieldBuilder().id('vertrouwelijkheidaanduiding')
                                                                        .label('vertrouwelijkheidaanduiding')
                                                                        .optionLabel('label')
                                                                        .options(vertrouwelijkheidaanduidingen).build();

        const omschrijving = new InputFormFieldBuilder().id('omschrijving').label('omschrijving').maxlength(80)
                                                        .build();
        const toelichting = new TextareaFormFieldBuilder().id('toelichting').label('toelichting').maxlength(1000)
                                                          .build();

        this.locatieField = new InputFormFieldBuilder().id('zaakgeometrie')
                                                       .icon(this.locatieToevoegenIcon)
                                                       .label('locatie')
                                                       .maxlength(100)
                                                       .build();
        const zaaktypeEnInitiator: AbstractFormField[] = [zaaktype];
        if (this.acties.toevoegenInitiatorPersoon || this.acties.toevoegenInitiatorBedrijf) {
            zaaktypeEnInitiator.push(this.initiatorField);
        }
        this.createZaakFields = [[titel], zaaktypeEnInitiator, [startdatum, registratiedatum, this.locatieField], [tussenTitel],
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

                if (key === 'zaakgeometrie' && formGroup.controls[key].value) {
                    zaak[key] = LocationUtil.point(this.locatie.centroide_ll);
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

    locatieGeselecteerd(locatie: AddressResult): void {
        this.locatie = locatie;
        this.locatieField.formControl.setValue(locatie?.weergavenaam);
        this.actionsSidenav.close();
    }

    private iconNext(action) {
        return () => {
            this.action = action;
            this.actionsSidenav.open();
        };
    }

}

