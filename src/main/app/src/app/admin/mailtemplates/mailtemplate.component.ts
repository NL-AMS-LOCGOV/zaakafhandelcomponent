/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {AdminComponent} from '../admin/admin.component';
import {MatSidenav, MatSidenavContainer} from '@angular/material/sidenav';
import {Mailtemplate} from '../model/mailtemplate';
import {InputFormField} from '../../shared/material-form-builder/form-components/input/input-form-field';
import {IdentityService} from '../../identity/identity.service';
import {UtilService} from '../../core/service/util.service';
import {ActivatedRoute} from '@angular/router';
import {InputFormFieldBuilder} from '../../shared/material-form-builder/form-components/input/input-form-field-builder';
import {Validators} from '@angular/forms';
import {Observable, of} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {MailtemplateService} from '../mailtemplate.service';
import {ReadonlyFormField} from '../../shared/material-form-builder/form-components/readonly/readonly-form-field';
import {ReadonlyFormFieldBuilder} from '../../shared/material-form-builder/form-components/readonly/readonly-form-field-builder';

@Component({
    templateUrl: './mailtemplate.component.html',
    styleUrls: ['./mailtemplate.component.less']
})
export class MailtemplateComponent extends AdminComponent implements OnInit, AfterViewInit {
    @ViewChild('sideNavContainer') sideNavContainer: MatSidenavContainer;
    @ViewChild('menuSidenav') menuSidenav: MatSidenav;

    template: Mailtemplate;

    naamFormField: InputFormField;
    enumFormField: InputFormField;
    onderwerpFormField: InputFormField;
    bodyFormField: InputFormField;
    parentFormField: ReadonlyFormField;

    isLoadingResults: boolean = false;

    constructor(private identityService: IdentityService,
                private service: MailtemplateService,
                public utilService: UtilService,
                private route: ActivatedRoute) {
        super(utilService);
    }

    ngOnInit(): void {
        this.route.data.subscribe(data => {
            this.init(data.template);
        });
    }

    init(mailtemplate: Mailtemplate): void {
        this.template = mailtemplate;
        this.setupMenu('title.mailtemplate');
        this.createForm();
    }

    createForm() {
        this.naamFormField = new InputFormFieldBuilder(this.template.mailTemplateNaam).id('mailTemplateNaam')
                                                                                      .label('naam')
                                                                                      .validators(Validators.required)
                                                                                      .build();
        this.enumFormField = new InputFormFieldBuilder(this.template.mailTemplateEnum).id('mailTemplateEnum')
                                                                                      .label('type')
                                                                                      .validators(Validators.required)
                                                                                      .build();
        this.onderwerpFormField = new InputFormFieldBuilder(this.template.onderwerp).id('onderwerp')
                                                                                    .label('onderwerp')
                                                                                    .validators(Validators.required)
                                                                                    .build();
        this.bodyFormField = new InputFormFieldBuilder(this.template.body).id('body')
                                                                          .label('body')
                                                                          .validators(Validators.required)
                                                                          .build();
        this.parentFormField = new ReadonlyFormFieldBuilder(this.template.parent).id('parent')
                                                                                 .label('parent')
                                                                                 .build();
    }

    editMailtemplate(event: any, field: string): void {
        this.template[field] = event[field];
        this.persistMailtemplate();
    }

    private persistMailtemplate(): void {
        const persistMailtemplate: Observable<Mailtemplate> = this.template.id != null
            ? this.service.updateMailtemplate(this.template)
            : this.service.createMailtemplate(this.template);
        persistMailtemplate.pipe(catchError(error => of(this.template)))
                           .subscribe(persistedMailtemplate => {
                               this.init(persistedMailtemplate);
                           });
    }

}
