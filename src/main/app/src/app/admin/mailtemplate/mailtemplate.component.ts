/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {
  AfterViewInit,
  Component,
  OnDestroy,
  OnInit,
  ViewChild,
} from "@angular/core";
import { AdminComponent } from "../admin/admin.component";
import { MatSidenav, MatSidenavContainer } from "@angular/material/sidenav";
import { Mailtemplate } from "../model/mailtemplate";
import { IdentityService } from "../../identity/identity.service";
import { UtilService } from "../../core/service/util.service";
import { ActivatedRoute, Router } from "@angular/router";
import { InputFormFieldBuilder } from "../../shared/material-form-builder/form-components/input/input-form-field-builder";
import { Validators } from "@angular/forms";
import { Observable, of } from "rxjs";
import { catchError } from "rxjs/operators";
import { MailtemplateBeheerService } from "../mailtemplate-beheer.service";
import { HtmlEditorFormFieldBuilder } from "../../shared/material-form-builder/form-components/html-editor/html-editor-form-field-builder";
import { SelectFormFieldBuilder } from "../../shared/material-form-builder/form-components/select/select-form-field-builder";
import { AbstractFormControlField } from "../../shared/material-form-builder/model/abstract-form-control-field";
import { Mail } from "../model/mail";
import { ReadonlyFormFieldBuilder } from "../../shared/material-form-builder/form-components/readonly/readonly-form-field-builder";
import { TranslateService } from "@ngx-translate/core";
import { HtmlEditorFormField } from "../../shared/material-form-builder/form-components/html-editor/html-editor-form-field";

@Component({
  templateUrl: "./mailtemplate.component.html",
  styleUrls: ["./mailtemplate.component.less"],
})
export class MailtemplateComponent
  extends AdminComponent
  implements OnInit, OnDestroy, AfterViewInit
{
  @ViewChild("sideNavContainer") sideNavContainer: MatSidenavContainer;
  @ViewChild("menuSidenav") menuSidenav: MatSidenav;

  fields = {
    NAAM: "mailTemplateNaam",
    MAIL: "mail",
    ONDERWERP: "onderwerp",
    BODY: "body",
    DEFAULT_MAILTEMPLATE: "defaultMailtemplate",
  };

  naamFormField: AbstractFormControlField;
  mailFormField: AbstractFormControlField;
  onderwerpFormField: HtmlEditorFormField;
  bodyFormField: HtmlEditorFormField;
  defaultMailtemplateFormField: AbstractFormControlField;

  template: Mailtemplate;

  isLoadingResults = false;

  constructor(
    private identityService: IdentityService,
    private service: MailtemplateBeheerService,
    public utilService: UtilService,
    private route: ActivatedRoute,
    private router: Router,
    private translateService: TranslateService,
  ) {
    super(utilService);
  }

  ngOnInit(): void {
    this.route.data.subscribe((data) => {
      this.init(data?.template ? data.template : new Mailtemplate());
    });
  }

  init(mailtemplate: Mailtemplate): void {
    this.template = mailtemplate;
    this.setupMenu("title.mailtemplate");
    this.createForm();
  }

  createForm() {
    const mails = this.utilService.getEnumAsSelectList("mail", Mail);

    this.naamFormField = new InputFormFieldBuilder(
      this.template.mailTemplateNaam,
    )
      .id(this.fields.NAAM)
      .label(this.fields.NAAM)
      .validators(Validators.required)
      .build();
    if (this.template.mail) {
      this.mailFormField = new ReadonlyFormFieldBuilder(
        this.translateService.instant("mail." + this.template.mail),
      )
        .id(this.fields.MAIL)
        .label(this.fields.MAIL)
        .build();
    } else {
      this.mailFormField = new SelectFormFieldBuilder()
        .id(this.fields.MAIL)
        .label(this.fields.MAIL)
        .optionLabel("label")
        .options(mails)
        .validators(Validators.required)
        .build();
    }
    this.onderwerpFormField = new HtmlEditorFormFieldBuilder(
      this.template.onderwerp,
    )
      .id(this.fields.ONDERWERP)
      .label(this.fields.ONDERWERP)
      .variabelen(this.template.variabelen)
      .emptyToolbar()
      .validators(Validators.required)
      .maxlength(100)
      .build();
    this.bodyFormField = new HtmlEditorFormFieldBuilder(this.template.body)
      .id(this.fields.BODY)
      .label(this.fields.BODY)
      .variabelen(this.template.variabelen)
      .validators(Validators.required)
      .build();
    this.defaultMailtemplateFormField = new InputFormFieldBuilder(
      this.template.defaultMailtemplate,
    )
      .id(this.fields.DEFAULT_MAILTEMPLATE)
      .label(this.fields.DEFAULT_MAILTEMPLATE)
      .build();

    this.subscriptions$.push(
      this.mailFormField.formControl.valueChanges.subscribe((value) => {
        if (value) {
          this.service
            .ophalenVariabelenVoorMail(value.value)
            .subscribe((variabelen) => {
              this.onderwerpFormField.variabelen = variabelen;
              this.bodyFormField.variabelen = variabelen;
            });
        }
      }),
    );
  }

  ngOnDestroy(): void {
    for (const subscription of this.subscriptions$) {
      subscription.unsubscribe();
    }
  }

  saveMailtemplate(): void {
    this.template.defaultMailtemplate = this.defaultMailtemplateFormField
      .formControl.value
      ? this.defaultMailtemplateFormField.formControl.value
      : false;
    this.template.mailTemplateNaam = this.naamFormField.formControl.value;
    if (!this.template.mail) {
      this.template.mail = this.mailFormField.formControl.value.value;
    }
    this.template.mail = this.template.mail
      ? this.template.mail
      : this.mailFormField.formControl.value.value;
    this.template.onderwerp = this.onderwerpFormField.formControl.value;
    this.template.body = this.bodyFormField.formControl.value;
    this.persistMailtemplate();
  }

  private persistMailtemplate(): void {
    const persistMailtemplate: Observable<Mailtemplate> =
      this.service.persistMailtemplate(this.template);
    persistMailtemplate
      .pipe(catchError(() => of(this.template)))
      .subscribe(() => {
        this.utilService.openSnackbar("msg.mailtemplate.opgeslagen");
        this.router.navigate(["/admin/mailtemplates"]);
      });
  }

  cancel() {
    this.router.navigate(["/admin/mailtemplates"]);
  }

  isInvalid() {
    return (
      this.naamFormField.formControl.invalid ||
      this.mailFormField.formControl.invalid ||
      this.onderwerpFormField.formControl.invalid ||
      this.bodyFormField.formControl.invalid
    );
  }
}
