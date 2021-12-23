/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {UtilService} from '../../core/service/util.service';
import {ActivatedRoute} from '@angular/router';
import {ZaakafhandelParameters} from '../model/zaakafhandel-parameters';
import {FormBuilder, FormGroup} from '@angular/forms';
import {BreakpointObserver} from '@angular/cdk/layout';

@Component({
    templateUrl: './parameter-edit.component.html',
    styleUrls: ['./parameter-edit.component.less']
})
export class ParameterEditComponent implements OnInit {

    public parameters: ZaakafhandelParameters;
    algemeenFormGroup: FormGroup;
    planitemFormGroup: FormGroup;

    constructor(private utilService: UtilService, breakpointObserver: BreakpointObserver, private route: ActivatedRoute, private _formBuilder: FormBuilder) {
        this.route.data.subscribe(data => {
            this.parameters = data.parameters;
            console.log(data);
        });
    }

    ngOnInit(): void {
        this.utilService.setTitle('title.parameters.edit');
        this.createForm();
    }

    createForm() {
        this.algemeenFormGroup = this._formBuilder.group({
            //moet nog
        });
        this.planitemFormGroup = this._formBuilder.group({
            //moet nog
        });
    }

    opslaan(): void {

    }
}
