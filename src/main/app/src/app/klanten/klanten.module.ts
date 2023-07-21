/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { NgModule } from "@angular/core";
import { SharedModule } from "../shared/shared.module";
import { PersoonZoekComponent } from "./zoek/personen/persoon-zoek.component";
import { BedrijfZoekComponent } from "./zoek/bedrijven/bedrijf-zoek.component";
import { PersoonsgegevensComponent } from "./persoonsgegevens/persoonsgegevens.component";
import { BedrijfsgegevensComponent } from "./bedrijfsgegevens/bedrijfsgegevens.component";
import { KlantZoekComponent } from "./zoek/klanten/klant-zoek.component";
import { KlantKoppelComponent } from "./koppel/klanten/klant-koppel.component";
import { RouterLink } from "@angular/router";
import { KlantenRoutingModule } from "./klanten-routing.module";
import { PersoonViewComponent } from "./persoon-view/persoon-view.component";
import { BedrijfViewComponent } from "./bedrijf-view/bedrijf-view.component";
import { KlantZakenTabelComponent } from "./klant-zaken-tabel/klant-zaken-tabel.component";
import { ContactmomentenModule } from "../contactmomenten/contactmomenten.module";

@NgModule({
  declarations: [
    BedrijfZoekComponent,
    BedrijfsgegevensComponent,
    PersoonZoekComponent,
    PersoonsgegevensComponent,
    KlantZakenTabelComponent,
    KlantZoekComponent,
    KlantKoppelComponent,
    PersoonViewComponent,
    BedrijfViewComponent,
  ],
  exports: [
    BedrijfZoekComponent,
    BedrijfsgegevensComponent,
    PersoonZoekComponent,
    PersoonsgegevensComponent,
    KlantZoekComponent,
    KlantKoppelComponent,
  ],
  imports: [
    SharedModule,
    RouterLink,
    KlantenRoutingModule,
    ContactmomentenModule,
  ],
})
export class KlantenModule {}
