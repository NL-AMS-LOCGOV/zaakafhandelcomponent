/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, Inject, OnInit } from "@angular/core";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { ZakenService } from "../zaken.service";
import { MaterialFormBuilderService } from "../../shared/material-form-builder/material-form-builder.service";
import { Zaak } from "../model/zaak";
import { ZaakKoppelGegevens } from "../model/zaak-koppel-gegevens";
import { SelectFormFieldBuilder } from "../../shared/material-form-builder/form-components/select/select-form-field-builder";
import { Validators } from "@angular/forms";
import { UtilService } from "../../core/service/util.service";
import { ZaakKoppelenService } from "./zaak-koppelen.service";
import { forkJoin } from "rxjs";
import { RadioFormField } from "../../shared/material-form-builder/form-components/radio/radio-form-field";
import { SelectFormField } from "../../shared/material-form-builder/form-components/select/select-form-field";
import { RadioFormFieldBuilder } from "../../shared/material-form-builder/form-components/radio/radio-form-field-builder";
import { TranslateService } from "@ngx-translate/core";
import { ZaakRelatietype } from "../model/zaak-relatietype";
import { ZaakKoppelDialogGegevens } from "../model/zaak-koppel-dialog-gegevens";
import { ReadonlyFormField } from "../../shared/material-form-builder/form-components/readonly/readonly-form-field";
import { ReadonlyFormFieldBuilder } from "../../shared/material-form-builder/form-components/readonly/readonly-form-field-builder";

@Component({
  templateUrl: "zaak-koppelen-dialog.component.html",
})
export class ZaakKoppelenDialogComponent implements OnInit {
  loading: boolean;
  bronZaak: Zaak;
  doelZaak: Zaak;
  soortRadioFormField: RadioFormField;
  hoofddeelZaakSelectFormField: SelectFormField;
  relevanteZaakReadonlyFormField: ReadonlyFormField;
  relevanteZaakSelectFormField: SelectFormField;
  readonly soortOptions: string[] = [];
  readonly HOOFDDEEL: string = "relatieSoort.hoofddeelZaak";
  readonly RELEVANTE: string = "relatieSoort.relevanteZaak";
  hoofddeelZaakKeuzes: { label: string; value: ZaakRelatietype }[] = [];
  relevanteZaakBronKeuzes: { label: string; value: ZaakRelatietype }[] = [];
  relevanteZaakDoelKeuzes: { label: string; value: ZaakRelatietype }[] = [];

  constructor(
    public dialogRef: MatDialogRef<ZaakKoppelenDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ZaakKoppelDialogGegevens,
    private mfbService: MaterialFormBuilderService,
    private zakenService: ZakenService,
    private utilService: UtilService,
    private zaakKoppelenService: ZaakKoppelenService,
    private translate: TranslateService,
  ) {}

  ngOnInit(): void {
    this.loading = true;

    forkJoin([
      this.zakenService.readZaak(this.data.bronZaakUuid),
      this.zakenService.readZaakByID(this.data.doelZaakIdentificatie),
    ]).subscribe(([bronZaak, doelZaak]) => {
      this.bronZaak = bronZaak;
      this.doelZaak = doelZaak;

      if (
        !doelZaak.isDeelzaak &&
        !doelZaak.isHoofdzaak &&
        !bronZaak.isDeelzaak
      ) {
        this.optie(
          this.hoofddeelZaakKeuzes,
          ZaakRelatietype.HOOFDZAAK,
          bronZaak,
          doelZaak,
        );
      }
      if (
        !doelZaak.isDeelzaak &&
        !bronZaak.isHoofdzaak &&
        !bronZaak.isDeelzaak
      ) {
        this.optie(
          this.hoofddeelZaakKeuzes,
          ZaakRelatietype.DEELZAAK,
          bronZaak,
          doelZaak,
        );
      }
      this.optie(
        this.relevanteZaakBronKeuzes,
        ZaakRelatietype.VERVOLG,
        bronZaak,
        doelZaak,
      );
      this.optie(
        this.relevanteZaakDoelKeuzes,
        ZaakRelatietype.VERVOLG,
        doelZaak,
        bronZaak,
      );
      this.optie(
        this.relevanteZaakBronKeuzes,
        ZaakRelatietype.BIJDRAGE,
        bronZaak,
        doelZaak,
      );
      this.optie(
        this.relevanteZaakDoelKeuzes,
        ZaakRelatietype.BIJDRAGE,
        doelZaak,
        bronZaak,
      );
      this.optie(
        this.relevanteZaakBronKeuzes,
        ZaakRelatietype.ONDERWERP,
        bronZaak,
        doelZaak,
      );
      this.optie(
        this.relevanteZaakDoelKeuzes,
        ZaakRelatietype.ONDERWERP,
        doelZaak,
        bronZaak,
      );

      if (0 < this.hoofddeelZaakKeuzes.length) {
        this.hoofddeelZaakSelectFormField = new SelectFormFieldBuilder(
          this.hoofddeelZaakKeuzes.length === 1
            ? this.hoofddeelZaakKeuzes[0]
            : null,
        )
          .id("hoofddeelkeuze")
          .label("relatieType.koppelen")
          .optionLabel("label")
          .validators(Validators.required)
          .options(this.hoofddeelZaakKeuzes)
          .build();

        this.soortOptions.push(this.HOOFDDEEL);
      }

      if (0 < this.relevanteZaakBronKeuzes.length) {
        this.relevanteZaakReadonlyFormField = new ReadonlyFormFieldBuilder(
          this.relevanteZaakBronKeuzes[0].label,
        )
          .id("relevantebronkeuze")
          .label("relatieType.koppelen")
          .build();

        this.relevanteZaakSelectFormField = new SelectFormFieldBuilder(
          this.relevanteZaakDoelKeuzes.length == 1
            ? this.relevanteZaakDoelKeuzes[0]
            : null,
        )
          .id("relevantedoelkeuze")
          .label("relatieType.koppelen.terug")
          .optionLabel("label")
          .options(this.relevanteZaakDoelKeuzes)
          .build();

        this.soortOptions.push(this.RELEVANTE);
      }

      this.soortRadioFormField = new RadioFormFieldBuilder(
        this.soortOptions.length == 1 ? this.soortOptions[0] : null,
      )
        .id("relatiesoort")
        .label("relatieSoort")
        .options(this.soortOptions)
        .validators(Validators.required)
        .build();

      this.loading = false;
    });
  }

  private optie(
    opties: { label: string; value: ZaakRelatietype }[],
    type: ZaakRelatietype,
    bron: Zaak,
    doel: Zaak,
  ): void {
    if (this.koppelbaar(type, bron, doel)) {
      opties.push({
        label: this.label(type, bron, doel),
        value: type,
      });
    }
  }

  private koppelbaar(
    type: ZaakRelatietype,
    andere: Zaak,
    onderhanden: Zaak,
  ): boolean {
    if (type == ZaakRelatietype.HOOFDZAAK) {
      return this.koppelbaar(ZaakRelatietype.DEELZAAK, onderhanden, andere);
    }
    for (const zaaktypeRelatie of onderhanden.zaaktype.zaaktypeRelaties) {
      if (
        zaaktypeRelatie.zaaktypeUuid == andere.zaaktype.uuid &&
        zaaktypeRelatie.relatieType == type
      ) {
        return true;
      }
    }
    return false;
  }

  private label(
    type: ZaakRelatietype,
    andere: Zaak,
    onderhanden: Zaak,
  ): string {
    return (
      type +
      ": " +
      this.translate.instant("relatieType.koppelen." + type, {
        andereZaak: andere.identificatie,
        onderhandenZaak: onderhanden.identificatie,
      })
    );
  }

  isKoppelenToegestaan(): boolean {
    return 0 < this.soortOptions.length;
  }

  isSoortKiesbaar(): boolean {
    return 1 < this.soortOptions.length;
  }

  isSoortHoofdDeelZaak(): boolean {
    return this.soortRadioFormField?.formControl.value == this.HOOFDDEEL;
  }

  isSoortRelevanteZaak(): boolean {
    return this.soortRadioFormField?.formControl.value == this.RELEVANTE;
  }

  isValid(): boolean {
    if (
      this.isKoppelenToegestaan() &&
      this.soortRadioFormField.formControl.valid
    ) {
      if (this.isSoortHoofdDeelZaak()) {
        return this.hoofddeelZaakSelectFormField.formControl.valid;
      }
      if (this.isSoortRelevanteZaak()) {
        return this.relevanteZaakSelectFormField.formControl.valid;
      }
    }
    return false;
  }

  close(): void {
    this.loading = true;
    this.zaakKoppelenService.addTeKoppelenZaak(this.bronZaak);
    this.dialogRef.close();
  }

  koppel(): void {
    this.dialogRef.disableClose = true;
    this.loading = true;
    this.koppelZaak(
      this.bronZaak,
      this.doelZaak,
      this.getRelatieType(),
      this.getRelatieTypeReverse(),
    );
  }

  private getRelatieType(): ZaakRelatietype {
    if (this.isSoortHoofdDeelZaak()) {
      return this.hoofddeelZaakSelectFormField.formControl.value.value;
    }
    if (this.isSoortRelevanteZaak()) {
      return this.relevanteZaakBronKeuzes[0].value;
    }
    return null;
  }

  private getRelatieTypeReverse(): ZaakRelatietype {
    if (this.isSoortRelevanteZaak()) {
      return this.relevanteZaakSelectFormField.formControl.value?.value;
    }
    return null;
  }

  private koppelZaak(
    bronZaak: Zaak,
    doelZaak: Zaak,
    relatieType: ZaakRelatietype,
    relatieTypeReverse: ZaakRelatietype,
  ) {
    const zaakKoppelGegevens = new ZaakKoppelGegevens();
    zaakKoppelGegevens.zaakUuid = doelZaak.uuid;
    zaakKoppelGegevens.teKoppelenZaakUuid = bronZaak.uuid;
    zaakKoppelGegevens.relatieType = relatieType;
    zaakKoppelGegevens.reverseRelatieType = relatieTypeReverse;
    this.zakenService.koppelZaak(zaakKoppelGegevens).subscribe({
      next: () => this.dialogRef.close(true),
      error: () => this.dialogRef.close(false),
    });
  }
}
