/*
 * SPDX-FileCopyrightText: 2021-2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, Input, OnChanges, SimpleChanges } from "@angular/core";
import { EnkelvoudigInformatieobject } from "../../../informatie-objecten/model/enkelvoudig-informatieobject";
import { DocumentZoekObject } from "../../../zoeken/model/documenten/document-zoek-object";
import { TranslateModule, TranslateService } from "@ngx-translate/core";
import { DatumPipe } from "../../pipes/datum.pipe";
import { Indicatie } from "../../model/indicatie";
import { IndicatiesComponent } from "../indicaties.component";
import { MaterialModule } from "../../material/material.module";
import { PipesModule } from "../../pipes/pipes.module";
import { CommonModule } from "@angular/common";

export enum InformatieobjectIndicatie {
  VERGRENDELD = "VERGRENDELD",
  ONDERTEKEND = "ONDERTEKEND",
  BESLUIT = "BESLUIT",
  GEBRUIKSRECHT = "GEBRUIKSRECHT",
  VERZONDEN = "VERZONDEN",
}

@Component({
  standalone: true,
  selector: "zac-informatie-object-indicaties",
  imports: [MaterialModule, TranslateModule, PipesModule, CommonModule],
  templateUrl: "../indicaties.component.html",
  styleUrls: ["../indicaties.component.less"],
})
export class InformatieObjectIndicatiesComponent
  extends IndicatiesComponent
  implements OnChanges
{
  datumPipe = new DatumPipe("nl");

  @Input() document: EnkelvoudigInformatieobject;
  @Input() documentZoekObject: DocumentZoekObject;

  constructor(private translateService: TranslateService) {
    super();
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.document = changes.document?.currentValue;
    this.documentZoekObject = changes.documentZoekObject?.currentValue;
    this.loadIndicaties();
  }

  private loadIndicaties(): void {
    this.indicaties = [];
    const indicaties = this.documentZoekObject
      ? this.documentZoekObject.indicaties
      : this.document.indicaties;
    indicaties.forEach((indicatie) => {
      switch (indicatie) {
        case InformatieobjectIndicatie.VERGRENDELD:
          this.indicaties.push(
            new Indicatie(
              indicatie,
              "lock",
              this.getVergrendeldToelichting(),
            ).temporary(),
          );
          break;
        case InformatieobjectIndicatie.ONDERTEKEND:
          this.indicaties.push(
            new Indicatie(
              indicatie,
              "fact_check",
              this.getOndertekeningToelichting(),
            ),
          );
          break;
        case InformatieobjectIndicatie.BESLUIT:
          this.indicaties.push(
            new Indicatie(
              indicatie,
              "gavel",
              this.translateService.instant("msg.document.besluit"),
            ),
          );
          break;
        case InformatieobjectIndicatie.GEBRUIKSRECHT:
          this.indicaties.push(
            new Indicatie(indicatie, "privacy_tip", "").temporary(),
          );
          break;
        case InformatieobjectIndicatie.VERZONDEN:
          this.indicaties.push(
            new Indicatie(
              indicatie,
              "local_post_office",
              this.getVerzondenToelichting(),
            ),
          );
          break;
      }
    });
  }

  private getOndertekeningToelichting(): string {
    if (this.documentZoekObject) {
      return (
        this.documentZoekObject.ondertekeningSoort +
        "-" +
        this.datumPipe.transform(this.documentZoekObject.ondertekeningDatum)
      );
    } else {
      return (
        this.document.ondertekening.soort +
        "-" +
        this.datumPipe.transform(this.document.ondertekening.datum)
      );
    }
  }

  private getVerzondenToelichting(): string {
    if (this.documentZoekObject) {
      return this.datumPipe.transform(this.documentZoekObject.verzenddatum);
    } else {
      return this.datumPipe.transform(this.document.verzenddatum);
    }
  }

  private getVergrendeldToelichting(): string {
    if (this.documentZoekObject) {
      return this.translateService.instant("msg.document.vergrendeld", {
        gebruiker: this.documentZoekObject.vergrendeldDoor,
      });
    } else {
      return this.translateService.instant("msg.document.vergrendeld", {
        gebruiker: this.document.gelockedDoor.naam,
      });
    }
  }
}
