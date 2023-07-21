/*
 * SPDX-FileCopyrightText: 2021-2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component } from "@angular/core";
import { MatDialogRef } from "@angular/material/dialog";

@Component({
  selector: "zac-klant-zoek-dialog",
  templateUrl: "klant-zoek-dialog.component.html",
  styleUrls: ["./klant-zoek-dialog.component.less"],
})
export class KlantZoekDialog {
  constructor(public dialogRef: MatDialogRef<KlantZoekDialog>) {}
}
