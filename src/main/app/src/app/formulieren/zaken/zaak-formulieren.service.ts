import { Injectable } from "@angular/core";
import { ZaakFormulierBuilder } from "./zaak-formulier-builder";
import { MeldingKleinEvenement } from "./model/melding-klein-evenement";
import { TranslateService } from "@ngx-translate/core";

@Injectable({
  providedIn: "root",
})
export class ZaakFormulierenService {
  constructor(private translate: TranslateService) {}

  public getFormulierBuilder(zaaktype: string): ZaakFormulierBuilder {
    switch (zaaktype) {
      case "melding-klein-evenement":
        return new ZaakFormulierBuilder(
          new MeldingKleinEvenement(this.translate),
        );
      default:
        throw new Error(`Onbekend zaakformulier: ${zaaktype}`);
    }
  }
}
