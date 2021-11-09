/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {FormConfig} from '../../shared/material-form-builder/model/form-config';
import {ActivatedRoute, Router} from '@angular/router';
import {PlanItemsService} from '../plan-items.service';
import {FormGroup, Validators} from '@angular/forms';
import {PlanItem} from '../model/plan-item';
import {IdentityService} from '../../identity/identity.service';
import {FormFieldConfig} from '../../shared/material-form-builder/model/form-field-config';
import {PlanItemType} from '../model/plan-item-type.enum';
import {NavigationService} from '../../shared/navigation/navigation.service';
import {UtilService} from '../../core/service/util.service';
import {HeadingFormField} from '../../shared/material-form-builder/form-components/heading/heading-form-field';
import {ReadonlyFormField} from '../../shared/material-form-builder/form-components/readonly/readonly-form-field';
import {SelectFormField} from '../../shared/material-form-builder/form-components/select/select-form-field';
import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';

@Component({
    templateUrl: './plan-item-do.component.html',
    styleUrls: ['./plan-item-do.component.less']
})
export class PlanItemDoComponent implements OnInit {

    formItems: Array<AbstractFormField[]>;
    formConfig: FormConfig;
    private planItem: PlanItem;

    constructor(private route: ActivatedRoute, private planItemsService: PlanItemsService, private identityService: IdentityService,
                private router: Router, private navigation: NavigationService, private utilService: UtilService) {
    }

    ngOnInit(): void {
        this.planItem = this.route.snapshot.data['planItem'];
        this.utilService.setTitle('title.taak.aanmaken');
        this.formConfig = new FormConfig('actie.starten', 'actie.annuleren');
        const titel = new HeadingFormField('doPlanItem', 'actie.taak.aanmaken', '1');
        const naam = new ReadonlyFormField('naam', 'naam', this.planItem.naam);
        if (this.planItem.type == PlanItemType.HumanTask) {
            this.identityService.getGroepen().subscribe(groepen => {
                const groep = new SelectFormField('groep', 'groep', this.planItem.groep, 'naam', groepen,
                    new FormFieldConfig([Validators.required]));
                this.formItems = [[titel], [naam], [groep]];
            });
        } else {
            this.formItems = [[titel], [naam]];
        }
    }

    onFormSubmit(formGroup: FormGroup): void {
        if (formGroup) {
            const planItem = new PlanItem();
            planItem.id = this.planItem.id;
            planItem.groep = formGroup.controls['groep']?.value;
            this.planItemsService.doPlanItem(planItem).subscribe(planItem => {
                this.navigation.back();
            });
        } else {
            this.navigation.back();
        }
    }
}
