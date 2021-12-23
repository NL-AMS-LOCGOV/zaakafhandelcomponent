/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {map} from 'rxjs/operators';
import {Zaaktype} from '../../zaken/model/zaaktype';
import {UtilService} from '../../core/service/util.service';
import {ActivatedRoute} from '@angular/router';
import {AutocompleteFormFieldBuilder} from '../../shared/material-form-builder/form-components/autocomplete/autocomplete-form-field-builder';
import {ZaakafhandelParameters} from '../model/zaakafhandel-parameters';

@Component({
    templateUrl: './param-view.component.html',
    styleUrls: ['./param-view.component.less']
})
export class ParamViewComponent implements OnInit {

    public parameters: ZaakafhandelParameters;

    constructor(private utilService: UtilService, private route: ActivatedRoute) {
        this.route.data.subscribe(data => {
            this.parameters = data.parameters;
            console.log(data);
        });
    }

    ngOnInit(): void {
        this.utilService.setTitle('param.view');
    }
}
