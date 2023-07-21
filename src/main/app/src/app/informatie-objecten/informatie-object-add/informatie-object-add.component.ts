/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {
  AfterViewInit,
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
  ViewChild,
} from "@angular/core";
import { ZakenService } from "../../zaken/zaken.service";
import { InformatieObjectenService } from "../informatie-objecten.service";
import { UtilService } from "../../core/service/util.service";
import { Zaak } from "../../zaken/model/zaak";
import { FormConfig } from "../../shared/material-form-builder/model/form-config";
import { FormGroup, Validators } from "@angular/forms";
import { EnkelvoudigInformatieobject } from "../model/enkelvoudig-informatieobject";
import * as moment from "moment/moment";
import { Vertrouwelijkheidaanduiding } from "../model/vertrouwelijkheidaanduiding.enum";
import { InformatieobjectStatus } from "../model/informatieobject-status.enum";
import { NavigationService } from "../../shared/navigation/navigation.service";
import { AbstractFormField } from "../../shared/material-form-builder/model/abstract-form-field";
import { InputFormFieldBuilder } from "../../shared/material-form-builder/form-components/input/input-form-field-builder";
import { FileFormFieldBuilder } from "../../shared/material-form-builder/form-components/file/file-form-field-builder";
import { DateFormFieldBuilder } from "../../shared/material-form-builder/form-components/date/date-form-field-builder";
import { SelectFormFieldBuilder } from "../../shared/material-form-builder/form-components/select/select-form-field-builder";
import { FormConfigBuilder } from "../../shared/material-form-builder/model/form-config-builder";
import { ConfiguratieService } from "../../configuratie/configuratie.service";
import { TranslateService } from "@ngx-translate/core";
import { User } from "../../identity/model/user";
import { IdentityService } from "../../identity/identity.service";
import { CheckboxFormFieldBuilder } from "../../shared/material-form-builder/form-components/checkbox/checkbox-form-field-builder";
import { FormComponent } from "../../shared/material-form-builder/form/form/form.component";
import { MatDrawer } from "@angular/material/sidenav";
import { Taak } from "../../taken/model/taak";
import { Subscription } from "rxjs";
import { OrderUtil } from "../../shared/order/order-util";
import { SelectFormField } from "../../shared/material-form-builder/form-components/select/select-form-field";

@Component({
  selector: "zac-informatie-object-add",
  templateUrl: "./informatie-object-add.component.html",
  styleUrls: ["./informatie-object-add.component.less"],
})
export class InformatieObjectAddComponent
  implements OnInit, AfterViewInit, OnDestroy
{
  @Input() zaak: Zaak;
  @Input() taak: Taak;
  @Input() sideNav: MatDrawer;
  @Output() document = new EventEmitter<EnkelvoudigInformatieobject>();

  @ViewChild(FormComponent) form: FormComponent;

  fields: Array<AbstractFormField[]>;
  formConfig: FormConfig;
  ingelogdeMedewerker: User;

  private informatieobjectStatussen: { label: string; value: string }[];
  private status: SelectFormField;
  private subscriptions$: Subscription[] = [];

  constructor(
    private zakenService: ZakenService,
    private informatieObjectenService: InformatieObjectenService,
    private navigation: NavigationService,
    public utilService: UtilService,
    private configuratieService: ConfiguratieService,
    private translateService: TranslateService,
    private identityService: IdentityService,
  ) {}

  ngOnInit(): void {
    this.formConfig = new FormConfigBuilder()
      .saveText("actie.toevoegen")
      .cancelText("actie.annuleren")
      .build();
    this.getIngelogdeMedewerker();

    const vertrouwelijkheidsAanduidingen = this.utilService.getEnumAsSelectList(
      "vertrouwelijkheidaanduiding",
      Vertrouwelijkheidaanduiding,
    );
    this.informatieobjectStatussen =
      this.utilService.getEnumAsSelectListExceptFor(
        "informatieobject.status",
        InformatieobjectStatus,
        [InformatieobjectStatus.GEARCHIVEERD],
      );

    const titel = new InputFormFieldBuilder()
      .id("titel")
      .label("titel")
      .validators(Validators.required)
      .maxlength(100)
      .build();

    const beschrijving = new InputFormFieldBuilder()
      .id("beschrijving")
      .label("beschrijving")
      .maxlength(100)
      .build();

    const inhoudField = new FileFormFieldBuilder()
      .id("bestandsnaam")
      .label("bestandsnaam")
      .uploadURL(
        this.zaak
          ? this.informatieObjectenService.getUploadURL(this.zaak.uuid)
          : this.informatieObjectenService.getUploadURL(this.taak.id),
      )
      .validators(Validators.required)
      .maxFileSizeMB(this.configuratieService.readMaxFileSizeMB())
      .additionalAllowedFileTypes(
        this.configuratieService.readAdditionalAllowedFileTypes(),
      )
      .build();

    const beginRegistratie = new DateFormFieldBuilder(moment())
      .id("creatiedatum")
      .label("creatiedatum")
      .validators(Validators.required)
      .build();

    const taal = new SelectFormFieldBuilder(
      this.configuratieService.readDefaultTaal(),
    )
      .id("taal")
      .label("taal")
      .optionLabel("naam")
      .options(this.configuratieService.listTalen())
      .value$(this.configuratieService.readDefaultTaal())
      .validators(Validators.required)
      .build();

    this.status = new SelectFormFieldBuilder(
      this.isAfgehandeld() ? this.getStatusDefinitief() : null,
    )
      .id("status")
      .label("status")
      .validators(Validators.required)
      .optionLabel("label")
      .options(this.informatieobjectStatussen)
      .build();

    const informatieobjectType = new SelectFormFieldBuilder()
      .id("informatieobjectTypeUUID")
      .label("informatieobjectType")
      .options(
        this.zaak
          ? this.informatieObjectenService.listInformatieobjecttypesForZaak(
              this.zaak.uuid,
            )
          : this.informatieObjectenService.listInformatieobjecttypesForZaak(
              this.taak.zaakUuid,
            ),
      )
      .optionLabel("omschrijving")
      .validators(Validators.required)
      .build();

    const auteur = new InputFormFieldBuilder(this.ingelogdeMedewerker.naam)
      .id("auteur")
      .label("auteur")
      .validators(Validators.required, Validators.pattern("\\S.*"))
      .maxlength(50)
      .build();

    const vertrouwelijk = new SelectFormFieldBuilder()
      .id("vertrouwelijkheidaanduiding")
      .label("vertrouwelijkheidaanduiding")
      .optionLabel("label")
      .options(vertrouwelijkheidsAanduidingen)
      .optionsOrder(OrderUtil.orderAsIs())
      .validators(Validators.required)
      .build();

    const ontvangstDatum = new DateFormFieldBuilder()
      .id("ontvangstdatum")
      .label("ontvangstdatum")
      .hint("msg.document.ontvangstdatum.hint", "start")
      .build();

    const verzendDatum = new DateFormFieldBuilder()
      .id("verzenddatum")
      .label("verzenddatum")
      .build();

    const nogmaals = new CheckboxFormFieldBuilder()
      .id("nogmaals")
      .label(this.translateService.instant("actie.document.toevoegen.nogmaals"))
      .build();

    if (this.zaak) {
      this.fields = [
        [inhoudField],
        [titel],
        [beschrijving],
        [informatieobjectType, vertrouwelijk],
        [this.status, beginRegistratie],
        [auteur, taal],
        [ontvangstDatum, verzendDatum],
        [nogmaals],
      ];
    } else if (this.taak) {
      this.fields = [
        [inhoudField],
        [titel],
        [informatieobjectType],
        [ontvangstDatum, verzendDatum],
        [nogmaals],
      ];
    }

    let vorigeBestandsnaam = null;
    this.subscriptions$.push(
      inhoudField.fileUploaded.subscribe((bestandsnaam) => {
        const titelCtrl = titel.formControl;
        if (!titelCtrl.value || titelCtrl.value === vorigeBestandsnaam) {
          titelCtrl.setValue(bestandsnaam.replace(/\.[^/.]+$/, ""));
          vorigeBestandsnaam = "" + titelCtrl.value;
        }
      }),
    );

    this.subscriptions$.push(
      informatieobjectType.formControl.valueChanges.subscribe((value) => {
        if (value) {
          vertrouwelijk.formControl.setValue(
            vertrouwelijkheidsAanduidingen.find(
              (option) => option.value === value.vertrouwelijkheidaanduiding,
            ),
          );
        }
      }),
    );

    this.subscriptions$.push(
      ontvangstDatum.formControl.valueChanges.subscribe((value) => {
        if (value && verzendDatum.formControl.enabled) {
          this.status.formControl.setValue(this.getStatusDefinitief());
          this.status.formControl.disable();
          verzendDatum.formControl.disable();
        } else if (!value && verzendDatum.formControl.disabled) {
          if (!this.isAfgehandeld()) {
            this.status.formControl.enable();
          }
          verzendDatum.formControl.enable();
        }
      }),
    );

    this.subscriptions$.push(
      verzendDatum.formControl.valueChanges.subscribe((value) => {
        if (value && ontvangstDatum.formControl.enabled) {
          ontvangstDatum.formControl.disable();
        } else if (!value && ontvangstDatum.formControl.disabled) {
          ontvangstDatum.formControl.enable();
        }
      }),
    );
  }

  private isAfgehandeld(): boolean {
    return this.zaak && !this.zaak.isOpen;
  }

  private getStatusDefinitief(): { label: string; value: string } {
    return this.informatieobjectStatussen.find(
      (option) =>
        option.value ===
        this.utilService.getEnumKeyByValue(
          InformatieobjectStatus,
          InformatieobjectStatus.DEFINITIEF,
        ),
    );
  }

  ngAfterViewInit(): void {
    if (this.isAfgehandeld()) {
      this.status.formControl.disable();
    }
  }

  ngOnDestroy(): void {
    for (const subscription of this.subscriptions$) {
      subscription.unsubscribe();
    }
  }

  onFormSubmit(formGroup: FormGroup): void {
    if (formGroup) {
      const infoObject = new EnkelvoudigInformatieobject();
      Object.keys(formGroup.controls).forEach((key) => {
        const control = formGroup.controls[key];
        const value = control.value;
        if (value instanceof moment) {
          infoObject[key] = value; // conversie niet nodig, ISO-8601 in UTC gaat goed met java ZonedDateTime.parse
        } else if (key === "informatieobjectTypeUUID") {
          infoObject[key] = value.uuid;
        } else if (key === "taal") {
          infoObject[key] = value.code;
        } else if (key === "status") {
          infoObject[key] = InformatieobjectStatus[value.value];
        } else if (key === "vertrouwelijkheidaanduiding") {
          infoObject[key] = value.value;
        } else {
          infoObject[key] = value;
        }
      });

      this.informatieObjectenService
        .createEnkelvoudigInformatieobject(
          this.zaak ? this.zaak.uuid : this.taak.zaakUuid,
          this.zaak ? this.zaak.uuid : this.taak.id,
          infoObject,
          !!this.taak,
        )
        .subscribe((document) => {
          this.document.emit(document);
          if (formGroup.get("nogmaals").value) {
            this.resetForm(formGroup);
          } else {
            this.clearForm(formGroup);
            this.sideNav.close();
          }
        });
    } else {
      this.resetAndClose();
    }
  }

  private resetAndClose() {
    this.fields.forEach((row) =>
      row.forEach((field) => {
        // Alles leeg maken behalve de volgende 3 velden
        if (
          field.id !== "auteur" &&
          field.id !== "creatiedatum" &&
          field.id !== "taal"
        ) {
          field.formControl.reset();
        }
      }),
    );
    this.sideNav.close();
  }

  resetForm(formGroup: FormGroup) {
    if (this.zaak) {
      formGroup.get("beschrijving").reset();
    }
    formGroup.get("bestandsnaam").reset();
    formGroup.get("bestandsnaam").setErrors(null);
    formGroup.get("titel").reset();
    formGroup.get("titel").setErrors(null);
    formGroup.get("nogmaals").setValue(false);
    formGroup.get("ontvangstdatum").reset();
    formGroup.get("verzenddatum").reset();
    this.form.reset();
    formGroup.setErrors({ invalid: true });
  }

  clearForm(formGroup: FormGroup) {
    if (this.zaak) {
      formGroup.get("status").reset();
      formGroup.get("status").setErrors(null);
      formGroup.get("vertrouwelijkheidaanduiding").reset();
      formGroup.get("vertrouwelijkheidaanduiding").setErrors(null);
    }
    formGroup.get("informatieobjectTypeUUID").reset();
    formGroup.get("informatieobjectTypeUUID").setErrors(null);
    formGroup.get("ontvangstdatum").reset();
    formGroup.get("ontvangstdatum").setErrors(null);
    formGroup.get("verzenddatum").reset();
    formGroup.get("verzenddatum").setErrors(null);
    this.resetForm(formGroup);
  }

  private getIngelogdeMedewerker() {
    this.identityService.readLoggedInUser().subscribe((ingelogdeMedewerker) => {
      this.ingelogdeMedewerker = ingelogdeMedewerker;
    });
  }
}
