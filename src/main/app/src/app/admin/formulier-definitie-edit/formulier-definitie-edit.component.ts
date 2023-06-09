/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit, ViewChild} from '@angular/core';
import {UtilService} from '../../core/service/util.service';
import {MatSidenav, MatSidenavContainer} from '@angular/material/sidenav';
import {IdentityService} from '../../identity/identity.service';
import {AdminComponent} from '../admin/admin.component';
import {MatDialog} from '@angular/material/dialog';
import {FormulierDefinitieService} from '../formulier-defintie.service';
import {FormulierDefinitie} from '../model/formulieren/formulier-definitie';
import {FormBuilder} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';

@Component({
    templateUrl: './formulier-definitie-edit.component.html',
    styleUrls: ['./formulier-definitie-edit.component.less']
})
export class FormulierDefinitieEditComponent extends AdminComponent implements OnInit {

    @ViewChild('sideNavContainer') sideNavContainer: MatSidenavContainer;
    @ViewChild('menuSidenav') menuSidenav: MatSidenav;

    definitie: FormulierDefinitie;

    constructor(private identityService: IdentityService,
                private service: FormulierDefinitieService,
                public dialog: MatDialog,
                private route: ActivatedRoute,
                private formBuilder: FormBuilder,
                public utilService: UtilService) {
        super(utilService);
    }

    ngOnInit(): void {
        this.route.data.subscribe(data => {
            this.definitie = data.definitie;
            if (this.definitie.id) {
                this.setupMenu('title.formulierdefinitie.edit');
            } else {
                this.setupMenu('title.formulierdefinitie.add');
            }
        });

    }

}
