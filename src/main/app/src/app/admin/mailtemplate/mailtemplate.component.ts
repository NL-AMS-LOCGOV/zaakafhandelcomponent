/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {AdminComponent} from '../admin/admin.component';
import {MatSidenav, MatSidenavContainer} from '@angular/material/sidenav';
import {Mailtemplate} from '../model/mailtemplate';
import {IdentityService} from '../../identity/identity.service';
import {UtilService} from '../../core/service/util.service';
import {ActivatedRoute, Router} from '@angular/router';
import {InputFormFieldBuilder} from '../../shared/material-form-builder/form-components/input/input-form-field-builder';
import {Validators} from '@angular/forms';
import {Observable, of} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {MailtemplateBeheerService} from '../mailtemplate-beheer.service';
import {HtmlEditorFormFieldBuilder} from '../../shared/material-form-builder/form-components/html-editor/html-editor-form-field-builder';
import {SelectFormFieldBuilder} from '../../shared/material-form-builder/form-components/select/select-form-field-builder';
import {AbstractFormControlField} from '../../shared/material-form-builder/model/abstract-form-control-field';
import {MailtemplateEnum} from '../model/mailtemplate-enum';

@Component({
    templateUrl: './mailtemplate.component.html',
    styleUrls: ['./mailtemplate.component.less']
})
export class MailtemplateComponent extends AdminComponent implements OnInit, AfterViewInit {
    @ViewChild('sideNavContainer') sideNavContainer: MatSidenavContainer;
    @ViewChild('menuSidenav') menuSidenav: MatSidenav;

    fields = {
        NAAM: 'mailTemplateNaam',
        ENUM: 'mailTemplateEnum',
        ONDERWERP: 'onderwerp',
        BODY: 'body',
        PARENT: 'parent'
    };

    naamFormField: AbstractFormControlField;
    enumFormField: AbstractFormControlField;
    onderwerpFormField: AbstractFormControlField;
    bodyFormField: AbstractFormControlField;
    parentFormField: AbstractFormControlField;

    template: Mailtemplate;

    isLoadingResults: boolean = false;

    constructor(private identityService: IdentityService,
                private service: MailtemplateBeheerService,
                public utilService: UtilService,
                private route: ActivatedRoute,
                private router: Router) {
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
        const mailtemplateTypes = this.utilService.getEnumAsSelectList('mailtemplatetype', MailtemplateEnum);

        let mailtemplateType;
        if (this.template.mailTemplateEnum) {
            mailtemplateTypes.forEach(value => {
                if (this.template.mailTemplateEnum === value.value) {
                    mailtemplateType = value;
                }
            });
        }

        this.naamFormField = new InputFormFieldBuilder(this.template.mailTemplateNaam).id(this.fields.NAAM)
                                                                                      .label(this.fields.NAAM)
                                                                                      .validators(Validators.required)
                                                                                      .build();
        this.enumFormField = new SelectFormFieldBuilder(mailtemplateType).id(this.fields.ENUM)
                                                                         .label(this.fields.ENUM)
                                                                         .optionLabel('label')
                                                                         .options(mailtemplateTypes)
                                                                         .validators(Validators.required)
                                                                         .build();
        this.onderwerpFormField = new InputFormFieldBuilder(this.template.onderwerp).id(this.fields.ONDERWERP)
                                                                                    .label(this.fields.ONDERWERP)
                                                                                    .validators(Validators.required)
                                                                                    .maxlength(100)
                                                                                    .build();
        this.bodyFormField = new HtmlEditorFormFieldBuilder(this.template.body).id(this.fields.BODY)
                                                                               .label(this.fields.BODY)
                                                                               .validators(Validators.required)
                                                                               .maxlength(1000)
                                                                               .build();
        this.parentFormField = new InputFormFieldBuilder(this.template.parent).id(this.fields.PARENT)
                                                                              .label(this.fields.PARENT)
                                                                              .build();
    }

    saveMailtemplate(): void {
        this.template.mailTemplateNaam = this.naamFormField.formControl.value;
        this.template.mailTemplateEnum = this.enumFormField.formControl.value.value;
        this.template.parent = this.parentFormField.formControl.value;
        this.template.onderwerp = this.onderwerpFormField.formControl.value;
        this.template.body = this.bodyFormField.formControl.value;
        this.persistMailtemplate();
    }

    private persistMailtemplate(): void {
        const persistMailtemplate: Observable<Mailtemplate> = this.service.persistMailtemplate(this.template);
        persistMailtemplate.pipe(catchError(error => of(this.template)))
                           .subscribe(() => {
                               this.utilService.openSnackbar('msg.mailtemplate.opgeslagen');
                               this.router.navigate(['/admin/mailtemplates']);
                           });
    }

    cancel() {
        this.router.navigate(['/admin/mailtemplates']);
    }

    isInvalid() {
        return this.naamFormField.formControl.invalid || this.enumFormField.formControl.invalid ||
            this.onderwerpFormField.formControl.invalid || this.bodyFormField.formControl.invalid;
    }
}
