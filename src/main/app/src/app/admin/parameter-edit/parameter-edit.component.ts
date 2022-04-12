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
import {Groep} from '../../identity/model/groep';
import {IdentityService} from '../../identity/identity.service';
import {Medewerker} from '../../identity/model/medewerker';
import {PlanItemParameter} from '../model/plan-item-parameter';
import {FormulierDefinitieVerwijzing} from '../model/formulier-definitie-verwijzing';
import {MatSelectChange} from '@angular/material/select';
import {HeaderMenuItem} from '../../shared/side-nav/menu-item/header-menu-item';
import {LinkMenuTitem} from '../../shared/side-nav/menu-item/link-menu-titem';
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
    planItemParameters: PlanItemParameter[] = [];
    userEventListenerParameters: UserEventListenerParameter[] = [];
    zaakbeeindigParameters: ZaakbeeindigParameter[] = [];
    selection = new SelectionModel<ZaakbeeindigParameter>(true);

    algemeenFormGroup: FormGroup;
    planItemFormGroup: FormGroup;
    UserEventListenerFormGroup: FormGroup;
    zaakbeeindigFormGroup: FormGroup;

    caseDefinitionControl = new FormControl(null, [Validators.required]);
    groepControl = new FormControl(null, [Validators.required]);
    behandelaarControl = new FormControl();

    caseDefinitions: Observable<CaseDefinition[]>;
    formulierDefinities: Observable<FormulierDefinitieVerwijzing[]>;
    groepen: Observable<Groep[]>;
    medewerkers: Observable<Medewerker[]>;
    zaakResultaten: Observable<ZaakResultaat[]>;

    constructor(public utilService: UtilService, private adminService: ZaakafhandelParametersService,
                private identityService: IdentityService,
                private route: ActivatedRoute, private formBuilder: FormBuilder) {
        super();
        this.route.data.subscribe(data => {
            this.parameters = data.parameters;
            this.userEventListenerParameters = this.parameters.userEventListenerParameters;
            this.planItemParameters = this.parameters.planItemParameters;
            this.caseDefinitionControl.setValue(this.parameters.caseDefinition);
            this.groepControl.setValue(this.parameters.defaultGroep);
            this.behandelaarControl.setValue(this.parameters.defaultBehandelaar);
            this.zaakResultaten = adminService.listZaakResultaten(this.parameters.zaaktype.uuid);
            adminService.listZaakbeeindigRedenen().subscribe(redenen => {
                this.readZaakbeeindigRedenen(redenen);
            });
        });
        this.caseDefinitions = adminService.listCaseDefinitions();
        this.groepen = identityService.listGroepen();
        this.medewerkers = identityService.listMedewerkers();
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
        this.menu.push(new LinkMenuTitem('parameters', '/admin/parameters', 'tune'));
    }

    readPlanItemParameters(event: MatSelectChange): void {
        this.planItemParameters = [];
        const caseDefinition = event.value;
        if (this.compareObject(caseDefinition, this.parameters.caseDefinition)) {
            this.planItemParameters = this.parameters.planItemParameters;
            this.updatePlanItemForm();
        } else {
            this.adminService.readCaseDefinition(caseDefinition.key).subscribe(data => {
                data.planItemDefinitions.forEach(planItemDefinition => {
                    const planItemParameter: PlanItemParameter = new PlanItemParameter();
                    planItemParameter.planItemDefinition = planItemDefinition;
                    planItemParameter.defaultGroep = this.parameters.defaultGroep;
                    this.planItemParameters.push(planItemParameter);
                });
                this.updatePlanItemForm();
            });
        }
    }

    getPlanItemControl(parameter: PlanItemParameter, field: string): FormControl {
        return this.planItemFormGroup.get(`${parameter.planItemDefinition.id}__${field}`) as FormControl;
    }

    createForm() {
        this.algemeenFormGroup = this.formBuilder.group({
            caseDefinitionControl: this.caseDefinitionControl,
            groepControl: this.groepControl,
            behandelaarControl: this.behandelaarControl
        });
        this.updatePlanItemForm();
        this.updateUserEventListenerForm();
        this.createZaakbeeindigForm();
    }

    updatePlanItemForm() {
        this.planItemFormGroup = this.formBuilder.group({});
        this.planItemParameters.forEach(parameter => {
            this.planItemFormGroup.addControl(parameter.planItemDefinition.id + '__defaultGroep', new FormControl(parameter.defaultGroep));
            this.planItemFormGroup.addControl(parameter.planItemDefinition.id + '__formulierDefinitie',
                new FormControl(parameter.formulierDefinitie, [Validators.required]));
            this.planItemFormGroup.addControl(parameter.planItemDefinition.id + '__doorlooptijd',
                new FormControl(parameter.doorlooptijd, [Validators.required, Validators.min(0)]));
        });
    }

    isPlanItemParameterValid(planItemParameter: PlanItemParameter): boolean {
        return this.getPlanItemControl(planItemParameter, 'defaultGroep').valid &&
            this.getPlanItemControl(planItemParameter, 'formulierDefinitie').valid &&
            this.getPlanItemControl(planItemParameter, 'doorlooptijd').valid;
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
            this.planItemFormGroup.valid &&
            this.zaakbeeindigFormGroup.valid;
    }

    opslaan(): void {
        this.parameters.caseDefinition = this.caseDefinitionControl.value;
        this.parameters.defaultGroep = this.groepControl.value;
        this.parameters.defaultBehandelaar = this.behandelaarControl.value;

        this.planItemParameters.forEach(param => {
            param.defaultGroep = this.getPlanItemControl(param, 'defaultGroep').value;
            param.formulierDefinitie = this.getPlanItemControl(param, 'formulierDefinitie').value;
            param.doorlooptijd = this.getPlanItemControl(param, 'doorlooptijd').value;
        });
        this.parameters.planItemParameters = this.planItemParameters;

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
