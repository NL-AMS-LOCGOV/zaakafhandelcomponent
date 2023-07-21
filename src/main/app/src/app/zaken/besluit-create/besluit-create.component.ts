/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
} from "@angular/core";
import { FormGroup, Validators } from "@angular/forms";
import { FormConfigBuilder } from "../../shared/material-form-builder/model/form-config-builder";
import { FormConfig } from "../../shared/material-form-builder/model/form-config";
import { UtilService } from "../../core/service/util.service";
import { AbstractFormField } from "../../shared/material-form-builder/model/abstract-form-field";
import { TextareaFormFieldBuilder } from "../../shared/material-form-builder/form-components/textarea/textarea-form-field-builder";
import { BesluitVastleggenGegevens } from "../model/besluit-vastleggen-gegevens";
import { SelectFormFieldBuilder } from "../../shared/material-form-builder/form-components/select/select-form-field-builder";
import { ZakenService } from "../zaken.service";
import { Zaak } from "../model/zaak";
import { DateFormFieldBuilder } from "../../shared/material-form-builder/form-components/date/date-form-field-builder";
import * as moment from "moment/moment";
import { Resultaattype } from "../model/resultaattype";
import { takeUntil } from "rxjs/operators";
import { Subject } from "rxjs";
import { DateFormField } from "../../shared/material-form-builder/form-components/date/date-form-field";
import { Besluittype } from "../model/besluittype";
import { InformatieObjectenService } from "../../informatie-objecten/informatie-objecten.service";
import { InformatieobjectZoekParameters } from "../../informatie-objecten/model/informatieobject-zoek-parameters";
import { DocumentenLijstFieldBuilder } from "../../shared/material-form-builder/form-components/documenten-lijst/documenten-lijst-field-builder";
import { MatDrawer } from "@angular/material/sidenav";

@Component({
  selector: "zac-besluit-create",
  templateUrl: "./besluit-create.component.html",
  styleUrls: ["./besluit-create.component.less"],
})
export class BesluitCreateComponent implements OnInit, OnDestroy {
  formConfig: FormConfig;
  @Input() zaak: Zaak;
  @Input() sideNav: MatDrawer;
  @Output() besluitVastgelegd = new EventEmitter<boolean>();
  fields: Array<AbstractFormField[]>;
  private ngDestroy = new Subject<void>();

  constructor(
    private zakenService: ZakenService,
    public utilService: UtilService,
    private informatieObjectenService: InformatieObjectenService,
  ) {}

  ngOnInit(): void {
    this.formConfig = new FormConfigBuilder()
      .saveText("actie.aanmaken")
      .cancelText("actie.annuleren")
      .build();

    const resultaattypeField = new SelectFormFieldBuilder(
      this.zaak.resultaat?.resultaattype,
    )
      .id("resultaattype")
      .label("resultaat")
      .validators(Validators.required)
      .optionLabel("naam")
      .options(this.zakenService.listResultaattypes(this.zaak.zaaktype.uuid))
      .build();
    const besluittypeField = new SelectFormFieldBuilder()
      .id("besluittype")
      .label("besluit")
      .validators(Validators.required)
      .optionLabel("naam")
      .options(this.zakenService.listBesluittypes(this.zaak.zaaktype.uuid))
      .build();
    const toelichtingField = new TextareaFormFieldBuilder()
      .id("toelichting")
      .label("toelichting")
      .maxlength(1000)
      .build();
    const ingangsdatumField = new DateFormFieldBuilder(moment())
      .id("ingangsdatum")
      .label("ingangsdatum")
      .validators(Validators.required)
      .build();
    const vervaldatumField = new DateFormFieldBuilder()
      .id("vervaldatum")
      .label("vervaldatum")
      .minDate(ingangsdatumField.formControl.value)
      .build();
    const documentenField = new DocumentenLijstFieldBuilder()
      .id("documenten")
      .label("documenten")
      .build();
    this.fields = [
      [resultaattypeField],
      [besluittypeField],
      [ingangsdatumField],
      [vervaldatumField],
      [toelichtingField],
      [documentenField],
    ];

    resultaattypeField.formControl.valueChanges
      .pipe(takeUntil(this.ngDestroy))
      .subscribe((value) => {
        if (value) {
          vervaldatumField.required = (
            value as Resultaattype
          ).vervaldatumBesluitVerplicht;
        }
      });
    ingangsdatumField.formControl.valueChanges
      .pipe(takeUntil(this.ngDestroy))
      .subscribe((value) => {
        (vervaldatumField as DateFormField).minDate = value;
      });
    besluittypeField.formControl.valueChanges
      .pipe(takeUntil(this.ngDestroy))
      .subscribe((value) => {
        if (value) {
          const zoekparameters = new InformatieobjectZoekParameters();
          zoekparameters.zaakUUID = this.zaak.uuid;
          zoekparameters.besluittypeUUID = value.id;
          documentenField.updateDocumenten(
            this.informatieObjectenService.listEnkelvoudigInformatieobjecten(
              zoekparameters,
            ),
          );
        }
      });
  }

  onFormSubmit(formGroup: FormGroup): void {
    if (formGroup) {
      const gegevens = new BesluitVastleggenGegevens();
      gegevens.zaakUuid = this.zaak.uuid;
      gegevens.resultaattypeUuid = (
        formGroup.controls["resultaattype"].value as Resultaattype
      ).id;
      gegevens.besluittypeUuid = (
        formGroup.controls["besluittype"].value as Besluittype
      ).id;
      gegevens.toelichting = formGroup.controls["toelichting"].value;
      gegevens.ingangsdatum = formGroup.controls["ingangsdatum"].value;
      gegevens.vervaldatum = formGroup.controls["vervaldatum"].value;
      gegevens.informatieobjecten = formGroup.controls["documenten"].value
        ? formGroup.controls["documenten"].value.split(";")
        : [];
      this.zakenService.createBesluit(gegevens).subscribe(() => {
        this.utilService.openSnackbar("msg.besluit.vastgelegd");
        this.besluitVastgelegd.emit(true);
      });
    } else {
      this.besluitVastgelegd.emit(false);
    }
  }

  ngOnDestroy(): void {
    this.ngDestroy.next();
    this.ngDestroy.complete();
  }
}
