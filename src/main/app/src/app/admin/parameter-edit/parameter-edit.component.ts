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

@Component({
    templateUrl: './parameter-edit.component.html',
    styleUrls: ['./parameter-edit.component.less']
})
export class ParameterEditComponent implements OnInit {

    parameters: ZaakafhandelParameters;
    algemeenFormGroup: FormGroup;
    planitemFormGroup: FormGroup;

    caseDefinitions: Observable<CaseDefinition[]>;
    caseDefinitionControl = new FormControl(null, [Validators.required]);

    groepen: Observable<Groep[]>;
    groepControl = new FormControl(null, [Validators.required]);

    medewerkers: Observable<Medewerker[]>;
    behandelaarControl = new FormControl();

    constructor(private utilService: UtilService, private adminService: AdminService, private identityService: IdentityService, private route: ActivatedRoute, private _formBuilder: FormBuilder) {
        this.route.data.subscribe(data => {
            this.parameters = data.parameters;
            this.caseDefinitionControl.setValue(this.parameters.caseDefinition);
        });
        this.caseDefinitions = adminService.listCaseDefinitions();
        this.groepen = identityService.listGroepen();
        this.medewerkers = identityService.listMedewerkers();
    }

    ngOnInit(): void {
        this.utilService.setTitle('title.parameters.edit');
        this.createForm();
    }

    createForm() {
        this.algemeenFormGroup = this._formBuilder.group({
            caseDefinitionControl: this.caseDefinitionControl,
            groepControl: this.groepControl,
            behandelaarControl: this.behandelaarControl
        });
        this.planitemFormGroup = this._formBuilder.group({
            //moet nog
        });
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
