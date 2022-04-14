/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormConfig} from '../../shared/material-form-builder/model/form-config';
import {ActivatedRoute, Router} from '@angular/router';
import {PlanItemsService} from '../plan-items.service';
import {FormGroup} from '@angular/forms';
import {PlanItem} from '../model/plan-item';
import {PlanItemType} from '../model/plan-item-type.enum';
import {NavigationService} from '../../shared/navigation/navigation.service';
import {UtilService} from '../../core/service/util.service';
import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';
import {AbstractFormulier} from '../../formulieren/model/abstract-formulier';
import {TaakFormulierenService} from '../../formulieren/taak-formulieren.service';
import {FormConfigBuilder} from '../../shared/material-form-builder/model/form-config-builder';

@Component({
    selector: 'zac-plan-item-do',
    templateUrl: './plan-item-do.component.html',
    styleUrls: ['./plan-item-do.component.less']
})
export class PlanItemDoComponent implements OnInit {

    formItems: Array<AbstractFormField[]>;
    formConfig: FormConfig;
    private formulier: AbstractFormulier;
    @Input() planItem: PlanItem;
    @Output() done = new EventEmitter<void>();

    constructor(private route: ActivatedRoute, private planItemsService: PlanItemsService, private taakFormulierenService: TaakFormulierenService,
                private router: Router, private navigation: NavigationService, private utilService: UtilService) {
    }

    ngOnInit(): void {
        this.formConfig = new FormConfigBuilder().saveText('actie.starten').cancelText('actie.annuleren').build();
        if (this.planItem.type === PlanItemType.HumanTask) {
            this.formulier = this.taakFormulierenService.getFormulierBuilder(this.planItem.formulierDefinitie)
                                 .startForm(this.planItem).build();
            if (this.formulier.disablePartialSave) {
                this.formConfig.partialButtonText = null;
            }
            this.utilService.setTitle(this.formulier.getStartTitel());
            this.formItems = this.formulier.form;
        } else {
            this.utilService.setTitle(this.planItem.naam);
            this.formItems = [[]];
        }
    }

    onFormSubmit(formGroup: FormGroup): void {
        if (formGroup) {
            if (this.planItem.type === PlanItemType.HumanTask) {
                this.planItem = this.formulier.getPlanItem(formGroup);
            }
            this.planItemsService.doPlanItem(this.planItem).subscribe(() => {
                this.done.emit();
            });
        } else { // cancel button clicked
            this.done.emit();
        }

    }
}
