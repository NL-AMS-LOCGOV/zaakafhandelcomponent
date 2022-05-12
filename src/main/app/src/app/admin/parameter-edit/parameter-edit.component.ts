/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit, ViewChild} from '@angular/core';
import {UtilService} from '../../core/service/util.service';
import {ActivatedRoute} from '@angular/router';
import {ZaakafhandelParameters} from '../model/zaakafhandel-parameters';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {Observable} from 'rxjs';
import {CaseDefinition} from '../model/case-definition';
import {ZaakafhandelParametersService} from '../zaakafhandel-parameters.service';
import {Group} from '../../identity/model/group';
import {IdentityService} from '../../identity/identity.service';
import {User} from '../../identity/model/user';
import {HumanTaskParameter} from '../model/human-task-parameter';
import {MatSelectChange} from '@angular/material/select';
import {HeaderMenuItem} from '../../shared/side-nav/menu-item/header-menu-item';
import {LinkMenuItem} from '../../shared/side-nav/menu-item/link-menu-item';
import {MenuItem} from '../../shared/side-nav/menu-item/menu-item';
import {MatSidenav, MatSidenavContainer} from '@angular/material/sidenav';
import {ZaakbeeindigParameter} from '../model/zaakbeeindig-parameter';
import {ZaakbeeindigReden} from '../model/zaakbeeindig-reden';
import {ZaakResultaat} from '../../zaken/model/zaak-resultaat';
import {SelectionModel} from '@angular/cdk/collections';
import {MatCheckboxChange} from '@angular/material/checkbox';
import {ViewComponent} from '../../shared/abstract-view/view-component';
import {UserEventListenerParameter} from '../model/user-event-listener-parameter';

@Component({
    templateUrl: './parameter-edit.component.html',
    styleUrls: ['./parameter-edit.component.less']
})
export class ParameterEditComponent extends ViewComponent implements OnInit {

    @ViewChild('sideNavContainer') sideNavContainer: MatSidenavContainer;
    @ViewChild('menuSidenav') menuSidenav: MatSidenav;
    menu: MenuItem[] = [];

    parameters: ZaakafhandelParameters;
    humanTaskParameters: HumanTaskParameter[] = [];
    userEventListenerParameters: UserEventListenerParameter[] = [];
    zaakbeeindigParameters: ZaakbeeindigParameter[] = [];
    selection = new SelectionModel<ZaakbeeindigParameter>(true);

    algemeenFormGroup: FormGroup;
    humanTaskFormGroup: FormGroup;
    UserEventListenerFormGroup: FormGroup;
    zaakbeeindigFormGroup: FormGroup;

    caseDefinitionControl = new FormControl(null, [Validators.required]);
    groepControl = new FormControl(null, [Validators.required]);
    behandelaarControl = new FormControl();
    einddatumGeplandWaarschuwingControl = new FormControl();
    uiterlijkeEinddatumAfdoeningWaarschuwingControl = new FormControl();

    caseDefinitions: Observable<CaseDefinition[]>;
    formulierDefinities: Observable<string[]>;
    groepen: Observable<Group[]>;
    medewerkers: Observable<User[]>;
    zaakResultaten: Observable<ZaakResultaat[]>;

    constructor(public utilService: UtilService, private adminService: ZaakafhandelParametersService,
                private identityService: IdentityService,
                private route: ActivatedRoute, private formBuilder: FormBuilder) {
        super();
        this.route.data.subscribe(data => {
            this.parameters = data.parameters;
            this.userEventListenerParameters = this.parameters.userEventListenerParameters;
            this.humanTaskParameters = this.parameters.humanTaskParameters;
            this.caseDefinitionControl.setValue(this.parameters.caseDefinition);
            this.groepControl.setValue(this.parameters.defaultGroep);
            this.behandelaarControl.setValue(this.parameters.defaultBehandelaar);
            this.einddatumGeplandWaarschuwingControl.setValue(this.parameters.einddatumGeplandWaarschuwing);
            this.uiterlijkeEinddatumAfdoeningWaarschuwingControl.setValue(this.parameters.uiterlijkeEinddatumAfdoeningWaarschuwing);
            this.zaakResultaten = adminService.listZaakResultaten(this.parameters.zaaktype.uuid);
            adminService.listZaakbeeindigRedenen().subscribe(redenen => {
                this.readZaakbeeindigRedenen(redenen);
            });
        });
        this.caseDefinitions = adminService.listCaseDefinitions();
        this.groepen = identityService.listGroups();
        this.medewerkers = identityService.listUsers();
        this.formulierDefinities = adminService.listFormulierDefinities();
    }

    ngOnInit(): void {
        this.utilService.setTitle('title.parameters.wijzigen');
        this.setupMenu();
        this.createForm();
    }

    setupMenu() {
        this.menu = [];
        this.menu.push(new HeaderMenuItem('title.parameters'));
        this.menu.push(new LinkMenuItem('parameters', '/admin/parameters', 'tune'));
    }

    readHumanTaskParameters(event: MatSelectChange): void {
        this.humanTaskParameters = [];
        const caseDefinition = event.value;
        if (this.compareObject(caseDefinition, this.parameters.caseDefinition)) {
            this.humanTaskParameters = this.parameters.humanTaskParameters;
            this.updateHumanTaskForm();
        } else {
            this.adminService.readCaseDefinition(caseDefinition.key).subscribe(data => {
                data.planItemDefinitions.forEach(planItemDefinition => {
                    const humanTaskParameter: HumanTaskParameter = new HumanTaskParameter();
                    humanTaskParameter.planItemDefinition = planItemDefinition;
                    humanTaskParameter.defaultGroep = this.parameters.defaultGroep;
                    this.humanTaskParameters.push(humanTaskParameter);
                });
                this.updateHumanTaskForm();
            });
        }
    }

    getHumanTaskControl(parameter: HumanTaskParameter, field: string): FormControl {
        return this.humanTaskFormGroup.get(`${parameter.planItemDefinition.id}__${field}`) as FormControl;
    }

    createForm() {
        this.algemeenFormGroup = this.formBuilder.group({
            caseDefinitionControl: this.caseDefinitionControl,
            groepControl: this.groepControl,
            behandelaarControl: this.behandelaarControl,
            einddatumGeplandWaarschuwingControl: this.einddatumGeplandWaarschuwingControl,
            uiterlijkeEinddatumAfdoeningWaarschuwingControl: this.uiterlijkeEinddatumAfdoeningWaarschuwingControl
        });
        this.updateHumanTaskForm();
        this.updateUserEventListenerForm();
        this.createZaakbeeindigForm();
    }

    updateHumanTaskForm() {
        this.humanTaskFormGroup = this.formBuilder.group({});
        this.humanTaskParameters.forEach(parameter => {
            this.humanTaskFormGroup.addControl(parameter.planItemDefinition.id + '__defaultGroep', new FormControl(parameter.defaultGroep));
            this.humanTaskFormGroup.addControl(parameter.planItemDefinition.id + '__formulierDefinitie',
                new FormControl(parameter.formulierDefinitie, [Validators.required]));
            this.humanTaskFormGroup.addControl(parameter.planItemDefinition.id + '__doorlooptijd',
                new FormControl(parameter.doorlooptijd, [Validators.min(0)]));
        });
    }

    isHumanTaskParameterValid(humanTaskParameter: HumanTaskParameter): boolean {
        return this.getHumanTaskControl(humanTaskParameter, 'defaultGroep').valid &&
            this.getHumanTaskControl(humanTaskParameter, 'formulierDefinitie').valid &&
            this.getHumanTaskControl(humanTaskParameter, 'doorlooptijd').valid;
    }

    getActieControl(parameter: UserEventListenerParameter, field: string): FormControl {
        return this.UserEventListenerFormGroup.get(`${parameter.id}__${field}`) as FormControl;
    }

    updateUserEventListenerForm() {
        this.UserEventListenerFormGroup = this.formBuilder.group({});
        this.userEventListenerParameters.forEach(parameter => {
            this.UserEventListenerFormGroup.addControl(parameter.id + '__toelichting', new FormControl(parameter.toelichting));
        });
    }

    createZaakbeeindigForm() {
        this.zaakbeeindigFormGroup = this.formBuilder.group({});
    }

    readZaakbeeindigRedenen(zaakbeeindigRedenen: ZaakbeeindigReden[]): void {
        for (const reden of zaakbeeindigRedenen) {
            const parameter: ZaakbeeindigParameter = this.getZaakbeeindigParameter(reden);
            this.zaakbeeindigParameters.push(parameter);
            this.zaakbeeindigFormGroup.addControl(parameter.zaakbeeindigReden.id + '__beeindigResultaat', new FormControl(parameter.zaakResultaat));
            this.updateZaakbeeindigForm(parameter);
        }
    }

    private getZaakbeeindigParameter(reden: ZaakbeeindigReden): ZaakbeeindigParameter {
        let parameter: ZaakbeeindigParameter = null;
        for (const item of this.parameters.zaakbeeindigParameters) {
            if (this.compareObject(item.zaakbeeindigReden, reden)) {
                parameter = item;
                this.selection.select(parameter);
                break;
            }
        }
        if (parameter === null) {
            parameter = new ZaakbeeindigParameter();
            parameter.zaakbeeindigReden = reden;
        }
        return parameter;
    }

    updateZaakbeeindigForm(parameter: ZaakbeeindigParameter) {
        const control: FormControl = this.getZaakbeeindigControl(parameter, 'beeindigResultaat');
        if (this.selection.isSelected(parameter)) {
            control.addValidators([Validators.required]);
        } else {
            control.clearValidators();
        }
        control.updateValueAndValidity({emitEvent: false});
    }

    changeSelection($event: MatCheckboxChange, parameter: ZaakbeeindigParameter): void {
        if ($event) {
            this.selection.toggle(parameter);
            this.updateZaakbeeindigForm(parameter);
        }
    }

    getZaakbeeindigControl(parameter: ZaakbeeindigParameter, field: string): FormControl {
        return this.zaakbeeindigFormGroup.get(`${parameter.zaakbeeindigReden.id}__${field}`) as FormControl;
    }

    isValid(): boolean {
        return this.algemeenFormGroup.valid &&
            this.humanTaskFormGroup.valid &&
            this.zaakbeeindigFormGroup.valid;
    }

    opslaan(): void {
        this.parameters.caseDefinition = this.caseDefinitionControl.value;
        this.parameters.defaultGroep = this.groepControl.value;
        this.parameters.defaultBehandelaar = this.behandelaarControl.value;
        this.parameters.einddatumGeplandWaarschuwing = this.einddatumGeplandWaarschuwingControl.value;
        this.parameters.uiterlijkeEinddatumAfdoeningWaarschuwing = this.uiterlijkeEinddatumAfdoeningWaarschuwingControl.value;

        this.humanTaskParameters.forEach(param => {
            param.defaultGroep = this.getHumanTaskControl(param, 'defaultGroep').value;
            param.formulierDefinitie = this.getHumanTaskControl(param, 'formulierDefinitie').value;
            param.doorlooptijd = this.getHumanTaskControl(param, 'doorlooptijd').value;
        });
        this.parameters.humanTaskParameters = this.humanTaskParameters;

        this.userEventListenerParameters.forEach(param => {
            param.toelichting = this.getActieControl(param, 'toelichting').value;
        });
        this.parameters.userEventListenerParameters = this.userEventListenerParameters;

        this.selection.selected.forEach(param => {
            param.zaakResultaat = this.getZaakbeeindigControl(param, 'beeindigResultaat').value;
        });
        this.parameters.zaakbeeindigParameters = this.selection.selected;

        this.adminService.updateZaakafhandelparameters(this.parameters).subscribe(() => {
            this.utilService.openSnackbar('msg.zaakafhandelparameters.opgeslagen');
        });
    }

    compareObject(object1: any, object2: any): boolean {
        if (typeof object1 === 'string') {
            return object1 === object2;
        }
        if (object1 && object2) {
            if (object1.hasOwnProperty('key')) {
                return object1.key === object2.key;
            } else if (object1.hasOwnProperty('id')) {
                return object1.id === object2.id;
            } else if (object1.hasOwnProperty('naam')) {
                return object1.naam === object2.naam;
            } else if (object1.hasOwnProperty('name')) {
                return object1.name === object2.name;
            }
            return object1 === object2;
        }
        return false;
    }
}
