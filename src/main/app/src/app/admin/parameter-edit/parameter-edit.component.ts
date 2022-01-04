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
import {MatSelectChange} from '@angular/material/select';

@Component({
    templateUrl: './parameter-edit.component.html',
    styleUrls: ['./parameter-edit.component.less']
})
export class ParameterEditComponent implements OnInit {

    parameters: ZaakafhandelParameters;
    planItemParameters: PlanItemParameter[] = [];

    algemeenFormGroup: FormGroup;
    planItemFormGroup: FormGroup;
    caseDefinitionControl = new FormControl(null, [Validators.required]);
    groepControl = new FormControl(null, [Validators.required]);
    behandelaarControl = new FormControl();

    caseDefinitions: Observable<CaseDefinition[]>;
    formulierDefinities: Observable<FormulierDefinitieVerwijzing[]>;
    groepen: Observable<Groep[]>;
    medewerkers: Observable<Medewerker[]>;

    constructor(private utilService: UtilService, private adminService: AdminService, private identityService: IdentityService,
                private route: ActivatedRoute, private formBuilder: FormBuilder) {

        this.route.data.subscribe(data => {
            this.parameters = data.parameters;
            this.planItemParameters = this.parameters.planItemParameters;
            this.caseDefinitionControl.setValue(this.parameters.caseDefinition);
        });

        this.caseDefinitions = adminService.listCaseDefinitions();
        this.groepen = identityService.listGroepen();
        this.medewerkers = identityService.listMedewerkers();
        this.formulierDefinities = adminService.listFormulierDefinities();
    }

    ngOnInit(): void {
        this.utilService.setTitle('title.parameters.wijzigen');
        this.createForm();
    }

    readPlanItemParameters(event: MatSelectChange): void {
        this.planItemParameters = [];
        let caseDefinition = event.value;
        if (this.compareObject(caseDefinition, this.parameters.caseDefinition)) {
            this.planItemParameters = this.parameters.planItemParameters;
            this.updateForm();
        } else {
            this.adminService.readCaseDefinition(caseDefinition.key).subscribe(data => {
                data.planItemDefinitions.forEach(planItemDefinition => {
                    let planItemParameter: PlanItemParameter = new PlanItemParameter();
                    planItemParameter.planItemDefinition = planItemDefinition;
                    planItemParameter.defaultGroep = this.parameters.defaultGroep;
                    this.planItemParameters.push(planItemParameter);
                });
                this.updateForm();
            });
        }
    }

    getControl(planItemParameter: PlanItemParameter, field: string): FormControl {
        return <FormControl>this.planItemFormGroup.get(`${planItemParameter.planItemDefinition.id}__${field}`);
    }

    createForm() {
        this.algemeenFormGroup = this.formBuilder.group({
            caseDefinitionControl: this.caseDefinitionControl,
            groepControl: this.groepControl,
            behandelaarControl: this.behandelaarControl
        });
        this.updateForm();
    }

    updateForm() {
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
        return this.getControl(planItemParameter, 'defaultGroep').valid &&
            this.getControl(planItemParameter, 'formulierDefinitie').valid &&
            this.getControl(planItemParameter, 'doorlooptijd').valid;
    }

    isValid(): boolean {
        return this.algemeenFormGroup.valid && this.planItemFormGroup.valid;
    }

    opslaan(): void {
        this.parameters.caseDefinition = this.caseDefinitionControl.value;
        this.parameters.defaultGroep = this.groepControl.value;
        this.parameters.defaultBehandelaar = this.behandelaarControl.value;

        this.planItemParameters.forEach(param => {
            param.defaultGroep = this.getControl(param, 'defaultGroep').value;
            param.formulierDefinitie = this.getControl(param, 'formulierDefinitie').value;
            param.doorlooptijd = this.getControl(param, 'doorlooptijd').value;
        });
        this.parameters.planItemParameters = this.planItemParameters;
        this.adminService.updateZaakafhandelparameters(this.parameters).subscribe(() => {
            this.utilService.openSnackbar('msg.zaakafhandelparameters.opgeslagen');
        });
    }

    compareObject(object1: any, object2: any): boolean {
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
    }

}
