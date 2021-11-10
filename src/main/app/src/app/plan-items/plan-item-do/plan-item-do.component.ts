/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {FormConfig} from '../../shared/material-form-builder/model/form-config';
import {ActivatedRoute, Router} from '@angular/router';
import {PlanItemsService} from '../plan-items.service';
import {FormGroup} from '@angular/forms';
import {PlanItem} from '../model/plan-item';
import {PlanItemType} from '../model/plan-item-type.enum';
import {NavigationService} from '../../shared/navigation/navigation.service';
import {UtilService} from '../../core/service/util.service';
import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';
import {AanvullendeInformatie} from '../../formulieren/model/aanvullende-informatie';
import {AbstractFormulier} from '../../formulieren/model/abstract-formulier';
import {FormulierModus} from '../../formulieren/model/formulier-modus';

@Component({
    templateUrl: './plan-item-do.component.html',
    styleUrls: ['./plan-item-do.component.less']
})
export class PlanItemDoComponent implements OnInit {

    formItems: Array<AbstractFormField[]>;
    formConfig: FormConfig;
    private planItem: PlanItem;
    private formulier: AbstractFormulier;

    constructor(private route: ActivatedRoute, private planItemsService: PlanItemsService,
                private router: Router, private navigation: NavigationService, private utilService: UtilService) {
    }

    ngOnInit(): void {
        this.planItem = this.route.snapshot.data['planItem'];
        this.utilService.setTitle('title.taak.aanmaken');
        this.formConfig = new FormConfig('actie.starten', 'actie.annuleren');
        this.formulier = new AanvullendeInformatie(FormulierModus.START, this.planItem);

        if (this.planItem.type == PlanItemType.HumanTask) {
            this.formItems = this.formulier.formulier;
        } else {
            this.formItems = [[]];
        }
    }

    onFormSubmit(formGroup: FormGroup): void {
        if (formGroup) {
            this.planItemsService.doPlanItem(this.formulier.getPlanItem(formGroup)).subscribe(() => {
                this.navigation.back();
            });
        } else {
            this.navigation.back();
        }
    }
}
