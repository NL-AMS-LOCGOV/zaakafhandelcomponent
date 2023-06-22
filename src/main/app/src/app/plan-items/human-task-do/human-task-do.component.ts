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
import {FormulierDefinitieService} from '../../admin/formulier-defintie.service';
import {FormulierDefinitie} from '../../admin/model/formulieren/formulier-definitie';
import {HumanTaskData} from '../model/human-task-data';

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
                private formulierDefinitieService: FormulierDefinitieService,
                private planItemsService: PlanItemsService) {
    }

    ngOnInit(): void {
        this.formulierDefinitieService.run(this.planItem.startformulierDefinitie).subscribe(fd => {
            this.formulierDefinitie = fd;
        });
    }

    onFormSubmit(formState: {}): void {

        this.done.emit();

        // if (formState) {
        //     this.planItemsService.doHumanTaskPlanItem(this.getHumanTaskData(formState)).subscribe(() => {
        //         this.done.emit();
        //     });
        // } else { // cancel button clicked
        //     this.done.emit();
        // }
    }


    getHumanTaskData(formState: {}): HumanTaskData {

        const humanTaskData = new HumanTaskData();


        return humanTaskData;

    }


}
