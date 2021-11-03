/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {FormItem} from '../../shared/material-form-builder/model/form-item';
import {FormConfig} from '../../shared/material-form-builder/model/form-config';
import {MaterialFormBuilderService} from '../../shared/material-form-builder/material-form-builder.service';
import {ActivatedRoute, Router} from '@angular/router';
import {PlanItemsService} from '../plan-items.service';
import {FormGroup, Validators} from '@angular/forms';
import {PlanItem} from '../model/plan-item';
import {IdentityService} from '../../identity/identity.service';
import {FormFieldConfig} from '../../shared/material-form-builder/model/form-field-config';
import {PlanItemType} from '../model/plan-item-type.enum';
import {NavigationService} from '../../shared/navigation/navigation.service';
import {UtilService} from '../../core/service/util.service';

@Component({
    templateUrl: './plan-item-do.component.html',
    styleUrls: ['./plan-item-do.component.less']
})
export class PlanItemDoComponent implements OnInit {

    formItems: Array<FormItem[]>;
    formConfig: FormConfig;
    private planItem: PlanItem;

    constructor(private route: ActivatedRoute, private planItemsService: PlanItemsService, private identityService: IdentityService,
                private mfbService: MaterialFormBuilderService, private router: Router, private navigation: NavigationService, private utilService: UtilService) {
    }

    ngOnInit(): void {
        this.planItem = this.route.snapshot.data['planItem'];
        this.utilService.setTitle('title.taak.aanmaken');
        this.formConfig = new FormConfig('actie.starten', 'actie.annuleren');
        const titel = this.mfbService.createHeadingFormItem('doPlanItem', 'actie.taak.aanmaken', '1');
        const naam = this.mfbService.createReadonlyFormItem('naam', 'naam', this.planItem.naam);
        if (this.planItem.type == PlanItemType.HumanTask) {
            this.identityService.getGroepen().subscribe(groepen => {
                const groep = this.mfbService.createSelectFormItem('groep', 'groep', this.planItem.groep, 'naam', groepen,
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
