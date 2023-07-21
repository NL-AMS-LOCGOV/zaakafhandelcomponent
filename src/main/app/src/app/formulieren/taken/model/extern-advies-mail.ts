/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Validators } from "@angular/forms";
import { TextareaFormFieldBuilder } from "../../../shared/material-form-builder/form-components/textarea/textarea-form-field-builder";
import { TranslateService } from "@ngx-translate/core";
import { InformatieObjectenService } from "../../../informatie-objecten/informatie-objecten.service";
import { TakenService } from "../../../taken/taken.service";
import { ParagraphFormFieldBuilder } from "../../../shared/material-form-builder/form-components/paragraph/paragraph-form-field-builder";
import { ReadonlyFormFieldBuilder } from "../../../shared/material-form-builder/form-components/readonly/readonly-form-field-builder";
import { InputFormFieldBuilder } from "../../../shared/material-form-builder/form-components/input/input-form-field-builder";
import { CustomValidators } from "../../../shared/validators/customValidators";
import { InformatieobjectZoekParameters } from "../../../informatie-objecten/model/informatieobject-zoek-parameters";
import { Mail } from "../../../admin/model/mail";
import { HtmlEditorFormFieldBuilder } from "../../../shared/material-form-builder/form-components/html-editor/html-editor-form-field-builder";
import { MailtemplateService } from "../../../mailtemplate/mailtemplate.service";
import { AbstractTaakFormulier } from "../abstract-taak-formulier";
import { SelectFormFieldBuilder } from "../../../shared/material-form-builder/form-components/select/select-form-field-builder";
import { ZakenService } from "../../../zaken/zaken.service";
import { HiddenFormFieldBuilder } from "../../../shared/material-form-builder/form-components/hidden/hidden-form-field-builder";
import { ZaakAfzender } from "../../../admin/model/zaakafzender";
import { SelectFormField } from "../../../shared/material-form-builder/form-components/select/select-form-field";
import { DocumentenLijstFieldBuilder } from "../../../shared/material-form-builder/form-components/documenten-lijst/documenten-lijst-field-builder";

export class ExternAdviesMail extends AbstractTaakFormulier {
  fields = {
    ADVISEUR: "adviseur",
    VERZENDER: "verzender",
    REPLYTO: "replyTo",
    EMAILADRES: "emailadres",
    BODY: "body",
    EXTERNADVIES: "externAdvies",
    BIJLAGEN: "bijlagen",
  };

  taakinformatieMapping = {
    uitkomst: this.fields.EXTERNADVIES,
  };

  constructor(
    translate: TranslateService,
    public takenService: TakenService,
    public informatieObjectenService: InformatieObjectenService,
    public mailtemplateService: MailtemplateService,
    private zakenService: ZakenService,
  ) {
    super(translate, informatieObjectenService);
  }

  _initStartForm() {
    const fields = this.fields;
    this.humanTaskData.taakStuurGegevens.sendMail = true;
    this.humanTaskData.taakStuurGegevens.mail = Mail.TAAK_ADVIES_EXTERN;

    const zoekparameters = new InformatieobjectZoekParameters();
    zoekparameters.zaakUUID = this.zaak.uuid;
    const documenten =
      this.informatieObjectenService.listEnkelvoudigInformatieobjecten(
        zoekparameters,
      );
    const mailtemplate = this.mailtemplateService.findMailtemplate(
      Mail.TAAK_ADVIES_EXTERN,
      this.zaak.uuid,
    );

    this.form.push(
      [
        new InputFormFieldBuilder()
          .id(fields.ADVISEUR)
          .label(fields.ADVISEUR)
          .validators(Validators.required)
          .maxlength(1000)
          .build(),
      ],
      [
        new SelectFormFieldBuilder()
          .id(fields.VERZENDER)
          .label(fields.VERZENDER)
          .options(this.zakenService.listAfzendersVoorZaak(this.zaak.uuid))
          .optionLabel("mail")
          .optionSuffix("suffix")
          .optionValue("mail")
          .value$(this.zakenService.readDefaultAfzenderVoorZaak(this.zaak.uuid))
          .validators(Validators.required)
          .build(),
      ],
      [new HiddenFormFieldBuilder().id(fields.REPLYTO).build()],
      [
        new InputFormFieldBuilder()
          .id(fields.EMAILADRES)
          .label(fields.EMAILADRES)
          .validators(Validators.required, CustomValidators.emails)
          .build(),
      ],
      [
        new HtmlEditorFormFieldBuilder()
          .id(fields.BODY)
          .label(fields.BODY)
          .validators(Validators.required)
          .mailtemplateBody(mailtemplate)
          .build(),
      ],
      [
        new DocumentenLijstFieldBuilder()
          .id(fields.BIJLAGEN)
          .label(fields.BIJLAGEN)
          .openInNieuweTab()
          .documenten(documenten)
          .build(),
      ],
    );

    this.getFormField(fields.VERZENDER).formControl.valueChanges.subscribe(
      (afzender: ZaakAfzender) => {
        const verzender: SelectFormField = this.getFormField(
          fields.VERZENDER,
        ) as SelectFormField;
        this.getFormField(fields.REPLYTO).formControl.setValue(
          verzender.getOption(afzender)?.replyTo,
        );
      },
    );
  }

  _initBehandelForm() {
    const fields = this.fields;
    this.form.push(
      [
        new ParagraphFormFieldBuilder()
          .text(
            this.translate.instant("msg.extern.advies.vastleggen.behandelen"),
          )
          .build(),
      ],
      [
        new ReadonlyFormFieldBuilder(this.getDataElement(fields.ADVISEUR))
          .id(fields.ADVISEUR)
          .label(fields.ADVISEUR)
          .build(),
      ],
      [
        new ReadonlyFormFieldBuilder(this.getDataElement(fields.VERZENDER))
          .id(fields.VERZENDER)
          .label(fields.VERZENDER)
          .build(),
      ],
      [
        new ReadonlyFormFieldBuilder(this.getDataElement(fields.EMAILADRES))
          .id(fields.EMAILADRES)
          .label(fields.EMAILADRES)
          .build(),
      ],
      [
        new ReadonlyFormFieldBuilder(this.getDataElement(fields.BODY))
          .id(fields.BODY)
          .label(fields.BODY)
          .build(),
      ],
      [
        new TextareaFormFieldBuilder(this.getDataElement(fields.EXTERNADVIES))
          .id(fields.EXTERNADVIES)
          .label(fields.EXTERNADVIES)
          .validators(Validators.required)
          .readonly(this.readonly)
          .maxlength(1000)
          .build(),
      ],
    );
  }

  getBehandelTitel(): string {
    return this.translate.instant("title.taak.extern-advies.verwerken");
  }
}
