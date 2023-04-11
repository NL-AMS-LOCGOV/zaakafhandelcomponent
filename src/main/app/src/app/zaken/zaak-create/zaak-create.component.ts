/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
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
import {Observable, Subject} from 'rxjs';
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
import {Zaaktype} from '../model/zaaktype';
import {MedewerkerGroepFieldBuilder} from '../../shared/material-form-builder/form-components/medewerker-groep/medewerker-groep-field-builder';
import {MedewerkerGroepFormField} from '../../shared/material-form-builder/form-components/medewerker-groep/medewerker-groep-form-field';
import {FieldType} from '../../shared/material-form-builder/model/field-type.enum';
import {SelectFormField} from '../../shared/material-form-builder/form-components/select/select-form-field';
import {IdentityService} from '../../identity/identity.service';
import {Group} from '../../identity/model/group';
import {User} from '../../identity/model/user';
import {HeadingLevel} from '../../shared/material-form-builder/form-components/heading/heading-form-field';
import {AutocompleteFormFieldBuilder} from '../../shared/material-form-builder/form-components/autocomplete/autocomplete-form-field-builder';
import {filter, takeUntil} from 'rxjs/operators';
import {InboxProductaanvraag} from '../../productaanvragen/model/inbox-productaanvraag';
import {KlantenService} from '../../klanten/klanten.service';
import {TextareaFormField} from '../../shared/material-form-builder/form-components/textarea/textarea-form-field';
import {ZaakAanmaakGegevens} from '../model/zaak-aanmaak-gegevens';

@Component({
    selector: 'zac-zaak-create',
    templateUrl: './zaak-create.component.html',
    styleUrls: ['./zaak-create.component.less']
})
export class ZaakCreateComponent implements OnInit, OnDestroy {
    private static KANAAL_E_FORMULIER = 'E-formulier';
    createZaakFields: Array<AbstractFormField[]>;
    formConfig: FormConfig;
    @ViewChild('actionsSideNav') actionsSidenav: MatSidenav;
    readonly sideNavAction = SideNavAction;
    action: SideNavAction;
    private initiatorField: InputFormField;
    private toelichtingField: TextareaFormField;
    private locatieField: InputFormField;
    private medewerkerGroepFormField: MedewerkerGroepFormField;
    private vertrouwelijkheidaanduidingField: SelectFormField;
    private vertrouwelijkheidaanduidingen: { label: string, value: string }[];
    private ngDestroy = new Subject<void>();
    private initiatorToevoegenIcon = new ActionIcon('person', 'actie.initiator.toevoegen', new Subject<void>());
    private locatieToevoegenIcon = new ActionIcon('place', 'actie.locatie.toevoegen', new Subject<void>());
    private initiator: Klant;
    private locatie: AddressResult;
    private inboxProductaanvraag: InboxProductaanvraag;
    private communicatiekanalen: Observable<{ naam: string; uuid: string }[]>;
    private communicatiekanaalField: SelectFormField;

    constructor(private zakenService: ZakenService,
                private identityService: IdentityService,
                private router: Router,
                private navigation: NavigationService,
                private klantenService: KlantenService,
                private utilService: UtilService) {
        this.inboxProductaanvraag = this.router.getCurrentNavigation()?.extras?.state?.inboxProductaanvraag;
    }

    ngOnInit(): void {
        this.initiatorToevoegenIcon.iconClicked.subscribe(this.iconNext(SideNavAction.ZOEK_INITIATOR));
        this.locatieToevoegenIcon.iconClicked.subscribe(this.iconNext(SideNavAction.ZOEK_LOCATIE));

        this.utilService.setTitle('title.zaak.aanmaken');

        this.formConfig = new FormConfigBuilder().saveText('actie.aanmaken').cancelText('actie.annuleren').build();
        this.communicatiekanalen = this.zakenService.listCommunicatiekanalen(this.inboxProductaanvraag != null);
        this.vertrouwelijkheidaanduidingen = this.utilService.getEnumAsSelectList('vertrouwelijkheidaanduiding',
            Vertrouwelijkheidaanduiding);

        const titel = new HeadingFormFieldBuilder().id('aanmakenZaak').label('actie.zaak.aanmaken')
                                                   .level(HeadingLevel.H1).build();

        const toekennenGegevensTitel = new HeadingFormFieldBuilder().id('toekennengegevens').label('gegevens.toekennen')
                                                                    .level(HeadingLevel.H2).build();

        const overigeGegevensTitel = new HeadingFormFieldBuilder().id('overigegegevens').label('gegevens.overig')
                                                                  .level(HeadingLevel.H2).build();

        const zaaktype = new AutocompleteFormFieldBuilder().id('zaaktype').label('zaaktype')
                                                           .validators(Validators.required)
                                                           .optionLabel('omschrijving')
                                                           .options(this.zakenService.listZaaktypes())
                                                           .build();

        zaaktype.formControl.valueChanges
                .pipe(filter(zt => typeof zt !== 'string'), takeUntil(this.ngDestroy))
                .subscribe(v => this.zaaktypeGeselecteerd(v));

        const startdatum = new DateFormFieldBuilder(moment()).id('startdatum').label('startdatum')
                                                             .validators(Validators.required)
                                                             .build();

        this.medewerkerGroepFormField = this.getMedewerkerGroupFormField();

        this.initiatorField = new InputFormFieldBuilder().id('initiatorIdentificatie')
                                                         .styleClass('input-fake-enabled')
                                                         .icon(this.initiatorToevoegenIcon)
                                                         .disabled()
                                                         .label('initiator')
                                                         .build();
        this.initiatorField.clicked.subscribe(this.iconNext(SideNavAction.ZOEK_INITIATOR));

        this.communicatiekanaalField = new SelectFormFieldBuilder().id('communicatiekanaal').label('communicatiekanaal')
                                                                   .optionLabel('naam').options(this.communicatiekanalen)
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
        this.toelichtingField = new TextareaFormFieldBuilder().id('toelichting').label('toelichting').maxlength(1000)
                                                              .build();

        this.locatieField = new InputFormFieldBuilder().id('zaakgeometrie')
                                                       .styleClass('input-fake-enabled')
                                                       .icon(this.locatieToevoegenIcon)
                                                       .disabled()
                                                       .label('locatie')
                                                       .build();
        this.locatieField.clicked.subscribe(this.iconNext(SideNavAction.ZOEK_LOCATIE));

        this.createZaakFields = [
            [titel],
            [zaaktype, this.initiatorField],
            [startdatum, this.locatieField],
            [toekennenGegevensTitel],
            [this.medewerkerGroepFormField],
            [overigeGegevensTitel],
            [this.communicatiekanaalField, this.vertrouwelijkheidaanduidingField],
            [omschrijving],
            [this.toelichtingField]
        ];

        if (this.inboxProductaanvraag) {
            this.verwerkInboxProductaanvraagGegevens();
        }
    }

    ngOnDestroy(): void {
        this.ngDestroy.next();
        this.ngDestroy.complete();
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
            this.zakenService.createZaak(new ZaakAanmaakGegevens(zaak, this.inboxProductaanvraag)).subscribe(newZaak => {
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

    getMedewerkerGroupFormField(groepId?: string, medewerkerId?: string): MedewerkerGroepFormField {
        let groep = null;
        let medewerker = null;

        if (groepId) {
            groep = new Group();
            groep.id = groepId;
        }

        if (medewerkerId) {
            medewerker = new User();
            medewerker.id = medewerkerId;
        }
        return new MedewerkerGroepFieldBuilder(groep, medewerker).id('toekenning')
                                                                 .groepLabel('actie.zaak.toekennen.groep')
                                                                 .groepRequired()
                                                                 .medewerkerLabel('actie.zaak.toekennen.medewerker')
                                                                 .maxlength(50)
                                                                 .build();
    }

    zaaktypeGeselecteerd(zaaktype: Zaaktype): void {
        if (zaaktype) {
            this.medewerkerGroepFormField = this.getMedewerkerGroupFormField(
                zaaktype.zaakafhandelparameters.defaultGroepId, zaaktype.zaakafhandelparameters.defaultBehandelaarId);
            const index = this.createZaakFields.findIndex(
                formRow => formRow.find(formField => formField.fieldType === FieldType.MEDEWERKER_GROEP));
            this.createZaakFields[index] = [this.medewerkerGroepFormField];

            // update reference of the array to apply changes
            this.createZaakFields = [...this.createZaakFields];

            this.vertrouwelijkheidaanduidingField.formControl.setValue(
                this.vertrouwelijkheidaanduidingen.find(o => o.value === zaaktype.vertrouwelijkheidaanduiding));
        }
    }

    private iconNext(action) {
        return () => {
            this.action = action;
            this.actionsSidenav.open();
        };
    }

    private verwerkInboxProductaanvraagGegevens(): void {
        const bsnLength = 9;
        const vestigingsnummerLength = 12;
        const defaultToelichting = 'Vanuit productaanvraag van type ' + this.inboxProductaanvraag.type;
        if (this.inboxProductaanvraag.initiatorID) {
            if (this.inboxProductaanvraag.initiatorID.length === bsnLength) {
                this.initiatorField.formControl.setValue(this.inboxProductaanvraag.initiatorID);
                this.klantenService.readPersoon(this.inboxProductaanvraag.initiatorID).subscribe(initiator => {
                    this.initiator = initiator;
                    this.initiatorField.formControl.setValue(initiator.naam);
                });
            } else if (this.inboxProductaanvraag.initiatorID.length === vestigingsnummerLength) {
                this.initiatorField.formControl.setValue(this.inboxProductaanvraag.initiatorID);
                this.klantenService.readVestiging(this.inboxProductaanvraag.initiatorID).subscribe(initiator => {
                    this.initiator = initiator;
                    this.initiatorField.formControl.setValue(initiator.naam);
                });
            }
        }
        this.toelichtingField.formControl.setValue(defaultToelichting);
        this.communicatiekanalen.subscribe(data => {
            this.communicatiekanaalField.value(data.find(c => c.naam === ZaakCreateComponent.KANAAL_E_FORMULIER));
        });
    }
}

