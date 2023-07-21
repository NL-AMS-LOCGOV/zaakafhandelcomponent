/*
 * SPDX-FileCopyrightText: 2021-2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, EventEmitter, Input, OnInit, Output } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { KlantZoekDialog } from "./klant-zoek-dialog.component";
import { ZoekParameters } from "../../../model/zoek-parameters";
import { FormControl } from "@angular/forms";
import { ZoekVeld } from "../../../model/zoek-veld";

@Component({
  selector: "zac-zaak-betrokkene-filter",
  templateUrl: "./zaak-betrokkene-filter.component.html",
  styleUrls: ["./zaak-betrokkene-filter.component.less"],
})
export class ZaakBetrokkeneFilterComponent implements OnInit {
  @Input() zoekparameters: ZoekParameters;
  @Output() changed = new EventEmitter<string>();
  dialogOpen: boolean;
  betrokkeneSelectControl = new FormControl<ZoekVeld>(ZoekVeld.ZAAK_INITIATOR);
  klantIdControl = new FormControl("");
  huidigeRoltype: ZoekVeld;
  ZoekVeld = ZoekVeld;

  constructor(public dialog: MatDialog) {}

  ngOnInit(): void {
    this.bepaalHuidigRoltype();
    this.klantIdControl.setValue(
      this.zoekparameters.zoeken[this.huidigeRoltype],
    );
  }

  openDialog(): void {
    this.dialogOpen = true;
    const dialogRef = this.dialog.open(KlantZoekDialog, {
      minWidth: "750px",
      backdropClass: "noColor",
    });
    dialogRef.afterClosed().subscribe((result) => {
      this.dialogOpen = false;
      if (result) {
        this.klantIdControl.setValue(result.identificatie);
        this.zoekparameters.zoeken[this.huidigeRoltype] = result.identificatie;
      }
      this.changed.emit();
    });
  }

  idChanged(): void {
    const huidigId = this.zoekparameters.zoeken[this.huidigeRoltype];
    const nieuwId = this.klantIdControl.value;
    if (huidigId !== nieuwId) {
      this.zoekparameters.zoeken[this.huidigeRoltype] =
        this.klantIdControl.value;
      this.changed.emit();
    }
  }

  roltypeChanged(): void {
    const id = this.zoekparameters.zoeken[this.huidigeRoltype];
    delete this.zoekparameters.zoeken[this.huidigeRoltype];
    this.huidigeRoltype = this.betrokkeneSelectControl.value;
    this.zoekparameters.zoeken[this.huidigeRoltype] = id;
    if (id) {
      this.changed.emit();
    }
  }

  bepaalHuidigRoltype() {
    this.huidigeRoltype = ZoekVeld.ZAAK_INITIATOR;
    if (this.zoekparameters.zoeken.ZAAK_BETROKKENEN) {
      this.huidigeRoltype = ZoekVeld.ZAAK_BETROKKENEN;
    } else if (this.zoekparameters.zoeken.ZAAK_INITIATOR) {
      this.huidigeRoltype = ZoekVeld.ZAAK_INITIATOR;
    } else if (this.zoekparameters.zoeken.ZAAK_BETROKKENE_BELANGHEBBENDE) {
      this.huidigeRoltype = ZoekVeld.ZAAK_BETROKKENE_BELANGHEBBENDE;
    } else if (this.zoekparameters.zoeken.ZAAK_BETROKKENE_ADVISEUR) {
      this.huidigeRoltype = ZoekVeld.ZAAK_BETROKKENE_ADVISEUR;
    } else if (this.zoekparameters.zoeken.ZAAK_BETROKKENE_BESLISSER) {
      this.huidigeRoltype = ZoekVeld.ZAAK_BETROKKENE_BESLISSER;
    } else if (this.zoekparameters.zoeken.ZAAK_BETROKKENE_ZAAKCOORDINATOR) {
      this.huidigeRoltype = ZoekVeld.ZAAK_BETROKKENE_ZAAKCOORDINATOR;
    } else if (this.zoekparameters.zoeken.ZAAK_BETROKKENE_MEDE_INITIATOR) {
      this.huidigeRoltype = ZoekVeld.ZAAK_BETROKKENE_MEDE_INITIATOR;
    }
  }
}
