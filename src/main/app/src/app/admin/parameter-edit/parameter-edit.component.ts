/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {UtilService} from '../../core/service/util.service';
import {ActivatedRoute} from '@angular/router';
import {ZaakafhandelParameters} from '../model/zaakafhandel-parameters';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {Observable} from 'rxjs';
import {CaseDefinition} from '../model/case-definition';
import {AdminService} from '../admin.service';
import {Groep} from '../../identity/model/groep';
import {IdentityService} from '../../identity/identity.service';
import {Medewerker} from '../../identity/model/medewerker';
import {PlanItemParameter} from '../model/plan-item-parameter';
import {FormulierDefinitieVerwijzing} from '../model/formulier-definitie-verwijzing';

@Component({
    templateUrl: './parameter-edit.component.html',
    styleUrls: ['./parameter-edit.component.less']
})
export class ParameterEditComponent implements OnInit {

    parameters: ZaakafhandelParameters;
    algemeenFormGroup: FormGroup;
    planItemParametersFormGroup: FormGroup;

    caseDefinitions: Observable<CaseDefinition[]>;
    formulierDefinities: Observable<FormulierDefinitieVerwijzing[]>;
    caseDefinitionControl = new FormControl(null, [Validators.required]);

    groepen: Observable<Groep[]>;
    groepControl = new FormControl(null, [Validators.required]);

    medewerkers: Observable<Medewerker[]>;
    behandelaarControl = new FormControl();
    planItemParameters: PlanItemParameter[];
    caseDefinition: CaseDefinition;

    constructor(private utilService: UtilService, private adminService: AdminService, private identityService: IdentityService, private route: ActivatedRoute, private _formBuilder: FormBuilder) {
        this.route.data.subscribe(data => {
            this.parameters = data.parameters;
            this.caseDefinition = this.parameters.caseDefinition;
            this.planItemParameters = this.parameters.planItemParameters;
            this.caseDefinitionControl.setValue(this.parameters.caseDefinition);
        });
        this.caseDefinitions = adminService.listCaseDefinitions();
        this.groepen = identityService.listGroepen();
        this.medewerkers = identityService.listMedewerkers();
        this.formulierDefinities = adminService.listFormulierDefinities();
    }

    ngOnInit(): void {
        this.utilService.setTitle('title.parameters.edit');
        this.createForm();
    }

    readPlanItemParameters(cd: CaseDefinition): void {
        this.caseDefinition = cd;
        if (this.compareObject(this.caseDefinition, this.parameters.caseDefinition)) {
            this.planItemParameters = this.parameters.planItemParameters;
        } else {
            this.adminService.readCaseDefinition(this.parameters.caseDefinition.key).subscribe(data => {
                this.planItemParameters = [];
                data.planItemDefinitions.forEach(planItemDefinition => {
                    let planItemParameter: PlanItemParameter = new PlanItemParameter();
                    planItemParameter.planItemDefinition = planItemDefinition;
                    planItemParameter.defaultGroep = this.parameters.defaultGroep;
                    this.planItemParameters.push(planItemParameter);
                });
            });
        }
        this.updateForm();
    }

    getControl(planItemParameter: PlanItemParameter, field: string): FormControl {
        return <FormControl>this.planItemParametersFormGroup.get(`${planItemParameter.planItemDefinition.id}__${field}`);
    }

    createForm() {
        this.algemeenFormGroup = this._formBuilder.group({
            caseDefinitionControl: this.caseDefinitionControl,
            groepControl: this.groepControl,
            behandelaarControl: this.behandelaarControl
        });
        this.updateForm();
    }

    updateForm() {
        this.planItemParametersFormGroup = this._formBuilder.group({});
        this.planItemParameters.forEach(params => {
            this.planItemParametersFormGroup.addControl(params.planItemDefinition.id + '__defaultGroep', new FormControl(params.defaultGroep));
            this.planItemParametersFormGroup.addControl(params.planItemDefinition.id + '__formulierDefinitie',
                new FormControl(params.formulierDefinitie, [Validators.required]));
            this.planItemParametersFormGroup.addControl(params.planItemDefinition.id + '__doorlooptijd',
                new FormControl(params.doorlooptijd, [Validators.required, Validators.min(0)]));
        });
    }

    isPlanItemParameterValid(p: PlanItemParameter): boolean {
        return this.getControl(p, 'defaultGroep').valid &&
            this.getControl(p, 'formulierDefinitie').valid &&
            this.getControl(p, 'doorlooptijd').valid;
    }

    opslaan(): void {
        console.log(this.parameters);
    }

    compareObject = (object1: any, object2: any): boolean => {
        if (typeof object1 === 'string') {
            return object1 == object2;
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
            return object1 == object2;
        }
        return false;
    };

}
