/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {PlanItemsService} from '../plan-items.service';
import {PlanItem} from '../model/plan-item';
import {Zaak} from '../../zaken/model/zaak';
import {MatDrawer} from '@angular/material/sidenav';
import {FormulierDefinitie} from '../../admin/model/formulieren/formulier-definitie';
import {HumanTaskData} from '../model/human-task-data';
import {FormulierRuntimeService} from '../../admin/formulier-runtime.service';
import {FormulierRuntimeContext} from '../../admin/model/formulieren/formulier-runtime-context';

@Component({
    selector: 'zac-human-task-do',
    templateUrl: './human-task-do.component.html',
    styleUrls: ['./human-task-do.component.less']
})
export class HumanTaskDoComponent implements OnInit {

    @Input() planItem: PlanItem;
    @Input() sideNav: MatDrawer;
    @Input() zaak: Zaak;
    @Output() done = new EventEmitter<void>();

    formulierDefinitie: FormulierDefinitie;

    constructor(private route: ActivatedRoute,
                private formulierRuntimeService: FormulierRuntimeService,
                private planItemsService: PlanItemsService) {
    }

    ngOnInit(): void {
        const context = new FormulierRuntimeContext();
        context.formulierSysteemnaam = this.planItem.startformulierDefinitie;
        context.zaak = this.zaak;
        this.formulierRuntimeService.run(context).subscribe(fd => {
            this.formulierDefinitie = fd;
        });
    }

    onFormSubmit(formState: {}): void {
        if (formState) {
            this.planItemsService.doHumanTaskPlanItem(this.getHumanTaskData(formState)).subscribe(() => {
                this.done.emit();
            });
        } else { // cancel button clicked
            this.done.emit();
        }
    }

    getHumanTaskData(formState: {}): HumanTaskData {
        const humanTaskData = new HumanTaskData();
        humanTaskData.planItemInstanceId = this.planItem.id;
        humanTaskData.formulierDefinitie = this.planItem.startformulierDefinitie;
        Object.entries(formState).map(([key, value]) => {
            if (typeof formState[key] === 'boolean') {
                formState[key] = `${formState[key]}`; // convert to string, boolean not allowed in string map (yasson/jsonb exception)
            }
        });
        humanTaskData.data = formState;
        return humanTaskData;
    }
}
