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
import {Subject, Subscription} from 'rxjs';
import {HeadingFormFieldBuilder} from '../../shared/material-form-builder/form-components/heading/heading-form-field-builder';
import {SelectFormFieldBuilder} from '../../shared/material-form-builder/form-components/select/select-form-field-builder';
import {DateFormFieldBuilder} from '../../shared/material-form-builder/form-components/date/date-form-field-builder';
import {InputFormFieldBuilder} from '../../shared/material-form-builder/form-components/input/input-form-field-builder';
import {TextareaFormFieldBuilder} from '../../shared/material-form-builder/form-components/textarea/textarea-form-field-builder';
import {FormConfigBuilder} from '../../shared/material-form-builder/model/form-config-builder';
import {MatSidenav} from '@angular/material/sidenav';
import {InputFormField} from '../../shared/material-form-builder/form-components/input/input-form-field';
import {ActionIcon} from '../../shared/edit/action-icon';
import {Klant} from '../../klanten/model/klanten/klant';
import {SideNavAction} from '../../shared/side-nav/side-nav-action';
import {LocationUtil} from '../../shared/location/location-util';
import {AddressResult} from '../../shared/location/location.service';
import {ZaakActies} from '../../policy/model/zaak-acties';
import {ZaakafhandelParametersService} from '../../admin/zaakafhandel-parameters.service';
import {Zaaktype} from '../model/zaaktype';
import {IdentityService} from '../../identity/identity.service';
import {MedewerkerGroepFieldBuilder} from '../../shared/material-form-builder/form-components/select-medewerker/medewerker-groep-field-builder';
import {MedewerkerGroepFormField} from '../../shared/material-form-builder/form-components/select-medewerker/medewerker-groep-form-field';
import {FieldType} from '../../shared/material-form-builder/model/field-type.enum';
import {Group} from '../../identity/model/group';
import {SelectFormField} from '../../shared/material-form-builder/form-components/select/select-form-field';

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
    private medewerkerGroepFormField: MedewerkerGroepFormField;
    private vertrouwelijkheidaanduidingField: SelectFormField;
    private vertrouwelijkheidaanduidingen: { label: string, value: string }[];
    private subscription$: Subscription;

    private initiatorToevoegenIcon = new ActionIcon('person', 'actie.initiator.toevoegen', new Subject<void>());
    private locatieToevoegenIcon = new ActionIcon('place', 'actie.locatie.toevoegen', new Subject<void>());

    private initiator: Klant;
    private locatie: AddressResult;

    constructor(private zakenService: ZakenService,
                private router: Router,
                private navigation: NavigationService,
                private utilService: UtilService,
                private identityService: IdentityService,
                private zaakafhandelParametersService: ZaakafhandelParametersService) {
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
        this.vertrouwelijkheidaanduidingen = this.utilService.getEnumAsSelectList('vertrouwelijkheidaanduiding',
            Vertrouwelijkheidaanduiding);

        const titel = new HeadingFormFieldBuilder().id('aanmakenZaak').label('actie.zaak.aanmaken').level('1').build();

        const toekennenGegevensTitel = new HeadingFormFieldBuilder().id('toekennengegevens').label('gegevens.toekennen').level('2').build();

        const overigeGegevensTitel = new HeadingFormFieldBuilder().id('overigegegevens').label('gegevens.overig').level('2').build();

        const zaaktype = new SelectFormFieldBuilder().id('zaaktype').label('zaaktype')
                                                     .validators(Validators.required)
                                                     .optionLabel('omschrijving')
                                                     .options(this.zakenService.listZaaktypes())
                                                     .build();

        this.subscription$ = zaaktype.formControl.valueChanges.subscribe(v => this.zaaktypeGeselecteerd(v));

        const startdatum = new DateFormFieldBuilder(moment()).id('startdatum').label('startdatum')
                                                     .validators(Validators.required)
                                                     .build();

        this.medewerkerGroepFormField = this.getMedewerkerGroupFormField();

        this.initiatorField = new InputFormFieldBuilder().id('initiatorIdentificatie')
                                                         .icon(this.initiatorToevoegenIcon)
                                                         .label('initiator')
                                                         .build();
        this.initiatorField.formControl.disable({onlySelf: true});

        const communicatiekanaal = new SelectFormFieldBuilder().id('communicatiekanaal').label('communicatiekanaal')
                                                               .optionLabel('naam').options(communicatiekanalen)
                                                               .validators(Validators.required)
                                                               .build();

        this.vertrouwelijkheidaanduidingField = new SelectFormFieldBuilder().id('vertrouwelijkheidaanduiding')
                                                                        .label('vertrouwelijkheidaanduiding')
                                                                        .optionLabel('label')
                                                                        .options(this.vertrouwelijkheidaanduidingen)
                                                                        .validators(Validators.required)
                                                                        .build();

        const omschrijving = new InputFormFieldBuilder().id('omschrijving').label('omschrijving').maxlength(80)
                                                        .validators(Validators.required)
                                                        .build();
        const toelichting = new TextareaFormFieldBuilder().id('toelichting').label('toelichting').maxlength(1000)
                                                          .build();

        this.locatieField = new InputFormFieldBuilder().id('zaakgeometrie')
                                                       .icon(this.locatieToevoegenIcon)
                                                       .label('locatie')
                                                       .build();
        this.locatieField.formControl.disable({onlySelf: true});
        const zaaktypeEnInitiator: AbstractFormField[] = [zaaktype];
        if (this.acties.toevoegenInitiatorPersoon || this.acties.toevoegenInitiatorBedrijf) {
            zaaktypeEnInitiator.push(this.initiatorField);
        }

        this.createZaakFields = [
            [titel],
            zaaktypeEnInitiator,
            [startdatum, this.locatieField],
            [toekennenGegevensTitel],
            [this.medewerkerGroepFormField],
            [overigeGegevensTitel],
            [communicatiekanaal, this.vertrouwelijkheidaanduidingField],
            [omschrijving],
            [toelichting]
        ];

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
                if (key === 'initiatorIdentificatie' && this.initiator != null) {
                    zaak['initiatorIdentificatieType'] = this.initiator.identificatieType;
                    zaak[key] = this.initiator.identificatie;
                }
                if (key === 'zaakgeometrie' && this.locatie != null) {
                    zaak[key] = LocationUtil.point(this.locatie.centroide_ll);
                }
                if (key === 'toekenning') {
                    if (this.medewerkerGroepFormField.formControl.value.medewerker) {
                        zaak.behandelaar = this.medewerkerGroepFormField.formControl.value.medewerker;
                    }
                    if (this.medewerkerGroepFormField.formControl.value.groep) {
                        zaak.groep = this.medewerkerGroepFormField.formControl.value.groep;
                    }
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
        this.initiator = initiator;
        this.initiatorField.formControl.setValue(initiator?.naam);
        this.actionsSidenav.close();
    }

    locatieGeselecteerd(locatie: AddressResult): void {
        this.locatie = locatie;
        this.locatieField.formControl.setValue(locatie?.weergavenaam);
        this.actionsSidenav.close();
    }

    getMedewerkerGroupFormField(groep?: Group): MedewerkerGroepFormField {
        return new MedewerkerGroepFieldBuilder().id('toekenning')
                                                .groepLabel('actie.zaak.toekennen.groep').defaultGroep(groep).groepOptioneel()
                                                .medewerkerLabel('actie.zaak.toekennen.medewerker').maxlength(50)
                                                .build();
    }

    zaaktypeGeselecteerd(zaaktype: Zaaktype): void {
        this.zaakafhandelParametersService.readZaakafhandelparameters(zaaktype.uuid).subscribe(
            zaakafhandelParameters => {
                this.medewerkerGroepFormField = this.getMedewerkerGroupFormField(zaakafhandelParameters.defaultGroep);
                const index = this.createZaakFields.findIndex(formRow => formRow.find(formField => formField.fieldType === FieldType.MEDEWERKER_GROEP));
                this.createZaakFields[index] = [this.medewerkerGroepFormField];
            }
        );
        this.vertrouwelijkheidaanduidingField.formControl.setValue(this.vertrouwelijkheidaanduidingen.find(o => o.value === zaaktype.vertrouwelijkheidaanduiding));
    }

    private iconNext(action) {
        return () => {
            this.action = action;
            this.actionsSidenav.open();
        };
    }

}

