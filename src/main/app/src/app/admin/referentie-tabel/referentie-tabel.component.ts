/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit, ViewChild} from '@angular/core';
import {UtilService} from '../../core/service/util.service';
import {MatSidenav, MatSidenavContainer} from '@angular/material/sidenav';
import {IdentityService} from '../../identity/identity.service';
import {AdminComponent} from '../admin/admin.component';
import {ReferentieTabelBeheerService} from '../referentie-tabel-beheer.service';
import {ReferentieTabel} from '../model/referentie-tabel';
import {Validators} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {InputFormField} from '../../shared/material-form-builder/form-components/input/input-form-field';
import {InputFormFieldBuilder} from '../../shared/material-form-builder/form-components/input/input-form-field-builder';

@Component({
    templateUrl: './referentie-tabel.component.html',
    styleUrls: ['./referentie-tabel.component.less']
})
export class ReferentieTabelComponent extends AdminComponent implements OnInit {

    @ViewChild('sideNavContainer') sideNavContainer: MatSidenavContainer;
    @ViewChild('menuSidenav') menuSidenav: MatSidenav;

    tabel: ReferentieTabel;

    codeFormField: InputFormField;
    naamFormField: InputFormField;

    constructor(private identityService: IdentityService,
                private service: ReferentieTabelBeheerService,
                public utilService: UtilService,
                private route: ActivatedRoute) {
        super(utilService);
    }

    ngOnInit(): void {
        this.route.data.subscribe(data => {
            // tslint:disable-next-line:no-console
            console.debug(data.tabel);
            this.init(data.tabel);
        });
    }

    init(tabel: ReferentieTabel): void {
        this.tabel = tabel;
        this.setupMenu('title.referentietabel', {tabel: this.tabel.code});
        this.createForm();
    }

    createForm() {
        this.codeFormField = new InputFormFieldBuilder().id('code')
                                                        .label('tabel')
                                                        .value(this.tabel.code)
                                                        .validators(Validators.required)
                                                        .build();
        this.naamFormField = new InputFormFieldBuilder().id('naam')
                                                        .label('naam')
                                                        .value(this.tabel.naam)
                                                        .validators(Validators.required)
                                                        .build();
    }

    editTabel(event: any, field: string): void {
        this.tabel[field] = event[field].value ? event[field].value : event[field];
        if (this.tabel.id != null) {
            this.service.updateReferentieTabel(this.tabel).subscribe(updatedTabel => {
                this.init(updatedTabel);
            });
        } else {
            this.service.createReferentieTabel(this.tabel).subscribe(createdTabel => {
                this.init(createdTabel);
            });
        }
    }
}
