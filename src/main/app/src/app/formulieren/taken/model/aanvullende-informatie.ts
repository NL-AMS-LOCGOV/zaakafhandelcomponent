/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Validators } from "@angular/forms";
import { ReadonlyFormFieldBuilder } from "../../../shared/material-form-builder/form-components/readonly/readonly-form-field-builder";
import { DateFormFieldBuilder } from "../../../shared/material-form-builder/form-components/date/date-form-field-builder";
import { TranslateService } from "@ngx-translate/core";
import { InputFormFieldBuilder } from "../../../shared/material-form-builder/form-components/input/input-form-field-builder";
import { CustomValidators } from "../../../shared/validators/customValidators";
import { Observable, of, Subject } from "rxjs";
import { InformatieObjectenService } from "../../../informatie-objecten/informatie-objecten.service";
import { TakenService } from "../../../taken/taken.service";
import * as moment from "moment/moment";
import { RadioFormFieldBuilder } from "../../../shared/material-form-builder/form-components/radio/radio-form-field-builder";
import { HiddenFormFieldBuilder } from "../../../shared/material-form-builder/form-components/hidden/hidden-form-field-builder";
import { InformatieobjectZoekParameters } from "../../../informatie-objecten/model/informatieobject-zoek-parameters";
import { HtmlEditorFormFieldBuilder } from "../../../shared/material-form-builder/form-components/html-editor/html-editor-form-field-builder";
import { MailtemplateService } from "../../../mailtemplate/mailtemplate.service";
import { Mailtemplate } from "../../../admin/model/mailtemplate";
import { Mail } from "../../../admin/model/mail";
import { AbstractTaakFormulier } from "../abstract-taak-formulier";
import { CheckboxFormFieldBuilder } from "../../../shared/material-form-builder/form-components/checkbox/checkbox-form-field-builder";
import { ZaakIndicatie } from "../../../shared/indicaties/zaak-indicaties/zaak-indicaties.component";
import { KlantenService } from "../../../klanten/klanten.service";
import { ActionIcon } from "../../../shared/edit/action-icon";
import { InputFormField } from "../../../shared/material-form-builder/form-components/input/input-form-field";
import { ZakenService } from "../../../zaken/zaken.service";
import { SelectFormFieldBuilder } from "../../../shared/material-form-builder/form-components/select/select-form-field-builder";
import { ZaakAfzender } from "../../../admin/model/zaakafzender";
import { SelectFormField } from "../../../shared/material-form-builder/form-components/select/select-form-field";
import { DocumentenLijstFieldBuilder } from "../../../shared/material-form-builder/form-components/documenten-lijst/documenten-lijst-field-builder";
import { DividerFormFieldBuilder } from "../../../shared/material-form-builder/form-components/divider/divider-form-field-builder";

export class AanvullendeInformatie extends AbstractTaakFormulier {
  fields = {
    VERZENDER: "verzender",
    REPLYTO: "replyTo",
    EMAILADRES: "emailadres",
    BODY: "body",
    DATUMGEVRAAGD: "datumGevraagd",
    DATUMGELEVERD: "datumGeleverd",
    AANVULLENDE_INFORMATIE: "aanvullendeInformatie",
    BIJLAGEN: "bijlagen",
    ZAAK_OPSCHORTEN: "zaakOpschorten",
    ZAAK_HERVATTEN: "zaakHervatten",
  };

  taakinformatieMapping = {
    uitkomst: this.fields.AANVULLENDE_INFORMATIE,
    opmerking: AbstractTaakFormulier.TOELICHTING_FIELD,
  };

  mailtemplate$: Observable<Mailtemplate>;

  constructor(
    translate: TranslateService,
    public takenService: TakenService,
    public informatieObjectenService: InformatieObjectenService,
    private mailtemplateService: MailtemplateService,
    private klantenService: KlantenService,
    private zakenService: ZakenService,
  ) {
    super(translate, informatieObjectenService);
  }

  private opschortenMogelijk(): boolean {
    return (
      this.zaak.zaaktype.opschortingMogelijk &&
      !this.zaak.redenOpschorting &&
      !this.zaak.isHeropend &&
      this.zaak.rechten.behandelen
    );
  }

  _initStartForm() {
    this.humanTaskData.taakStuurGegevens.sendMail = true;
    this.mailtemplate$ = this.mailtemplateService.findMailtemplate(
      Mail.TAAK_AANVULLENDE_INFORMATIE,
      this.zaak.uuid,
    );
    this.humanTaskData.taakStuurGegevens.mail =
      Mail.TAAK_AANVULLENDE_INFORMATIE;
    const zoekparameters = new InformatieobjectZoekParameters();
    zoekparameters.zaakUUID = this.zaak.uuid;
    const documenten =
      this.informatieObjectenService.listEnkelvoudigInformatieobjecten(
        zoekparameters,
      );
    const morgen = new Date();
    morgen.setDate(morgen.getDate() + 1);
    const fields = this.fields;
    this.form.push(
      [
        new SelectFormFieldBuilder()
          .id(fields.VERZENDER)
          .label(fields.VERZENDER)
          .options(this.zakenService.listAfzendersVoorZaak(this.zaak.uuid))
          .optionLabel("mail")
          .optionSuffix("suffix")
          .optionValue("mail")
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
          .mailtemplateBody(this.mailtemplate$)
          .build(),
      ],
      [new HiddenFormFieldBuilder(moment()).id(fields.DATUMGEVRAAGD).build()],
      [
        new DocumentenLijstFieldBuilder()
          .id(fields.BIJLAGEN)
          .label(fields.BIJLAGEN)
          .documenten(documenten)
          .openInNieuweTab()
          .build(),
      ],
      [
        new DateFormFieldBuilder(this.humanTaskData.fataledatum)
          .id(AbstractTaakFormulier.TAAK_FATALEDATUM)
          .minDate(morgen)
          .label("fataledatum")
          .showDays()
          .build(),
      ],
    );

    this.zakenService
      .readDefaultAfzenderVoorZaak(this.zaak.uuid)
      .subscribe((defaultMail) => {
        this.getFormField(fields.VERZENDER).formControl.setValue(
          defaultMail.mail,
        );
      });

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

    if (this.opschortenMogelijk()) {
      this.form.push([
        new CheckboxFormFieldBuilder()
          .id(fields.ZAAK_OPSCHORTEN)
          .label(fields.ZAAK_OPSCHORTEN)
          .build(),
      ]);
      this.getFormField(
        fields.ZAAK_OPSCHORTEN,
      ).formControl.valueChanges.subscribe((opschorten) => {
        this.getFormField(AbstractTaakFormulier.TAAK_FATALEDATUM).required =
          opschorten;
      });
    }

    if (
      this.zaak.initiatorIdentificatieType &&
      this.zaak.initiatorIdentificatie
    ) {
      this.klantenService
        .ophalenContactGegevens(
          this.zaak.initiatorIdentificatieType,
          this.zaak.initiatorIdentificatie,
        )
        .subscribe((gegevens) => {
          if (gegevens.emailadres) {
            const initiatorToevoegenIcon = new ActionIcon(
              "person",
              "actie.initiator.email.toevoegen",
              new Subject<void>(),
            );
            const emailInput = this.getFormField(
              this.fields.EMAILADRES,
            ) as InputFormField;
            emailInput.icons
              ? emailInput.icons.push(initiatorToevoegenIcon)
              : (emailInput.icons = [initiatorToevoegenIcon]);
            initiatorToevoegenIcon.iconClicked.subscribe(() => {
              emailInput.value(gegevens.emailadres);
            });
          }
        });
    }
  }

  _initBehandelForm() {
    const fields = this.fields;
    const aanvullendeInformatieDataElement = this.getDataElement(
      fields.AANVULLENDE_INFORMATIE,
    );
    this.form.push(
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
      [new DividerFormFieldBuilder().build()],
      [
        new DateFormFieldBuilder(this.getDataElement(fields.DATUMGEVRAAGD))
          .id(fields.DATUMGEVRAAGD)
          .label(fields.DATUMGEVRAAGD)
          .readonly(true)
          .build(),
        new DateFormFieldBuilder(this.getDataElement(fields.DATUMGELEVERD))
          .id(fields.DATUMGELEVERD)
          .label(fields.DATUMGELEVERD)
          .readonly(this.readonly)
          .build(),
      ],
      [
        new RadioFormFieldBuilder(
          this.readonly && aanvullendeInformatieDataElement
            ? this.translate.instant(aanvullendeInformatieDataElement)
            : aanvullendeInformatieDataElement,
        )
          .id(fields.AANVULLENDE_INFORMATIE)
          .label(fields.AANVULLENDE_INFORMATIE)
          .options(this.getAanvullendeInformatieOpties())
          .validators(Validators.required)
          .readonly(this.readonly)
          .build(),
      ],
    );

    if (this.toonHervatten()) {
      if (this.readonly) {
        this.form.push([
          new ReadonlyFormFieldBuilder(
            this.translate.instant(
              this.getDataElement(fields.ZAAK_HERVATTEN) === "true"
                ? "zaak.hervatten.ja"
                : "zaak.hervatten.nee",
            ),
          )
            .id(fields.ZAAK_HERVATTEN)
            .label("actie.zaak.hervatten")
            .build(),
        ]);
      } else {
        this.form.push([
          new RadioFormFieldBuilder(this.getDataElement(fields.ZAAK_HERVATTEN))
            .id(fields.ZAAK_HERVATTEN)
            .label("actie.zaak.hervatten")
            .options([
              { value: "true", label: "zaak.hervatten.ja" },
              { value: "false", label: "zaak.hervatten.nee" },
            ])
            .validators(Validators.required)
            .optionLabel("label")
            .optionValue("value")
            .readonly(this.readonly)
            .build(),
        ]);
      }
    }
  }

  getStartTitel(): string {
    return this.translate.instant("title.taak.aanvullende-informatie.starten");
  }

  getBehandelTitel(): string {
    if (this.readonly) {
      return this.translate.instant(
        "title.taak.aanvullende-informatie.raadplegen",
      );
    } else {
      return this.translate.instant(
        "title.taak.aanvullende-informatie.behandelen",
      );
    }
  }

  getAanvullendeInformatieOpties(): Observable<string[]> {
    return of([
      "aanvullende-informatie.geleverd-akkoord",
      "aanvullende-informatie.geleverd-niet-akkoord",
      "aanvullende-informatie.niet-geleverd",
    ]);
  }

  private toonHervatten(): boolean {
    if (this.readonly) {
      return this.getDataElement(this.fields.ZAAK_HERVATTEN);
    }
    return (
      this.zaak.indicaties.find(
        (indicatie) => indicatie === ZaakIndicatie.OPSCHORTING,
      ) && this.zaak.rechten.behandelen
    );
  }
}
