/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
} from "@angular/core";
import { KlantenService } from "../klanten.service";
import { Bedrijf } from "../model/bedrijven/bedrijf";
import { Vestigingsprofiel } from "../model/bedrijven/vestigingsprofiel";

@Component({
  selector: "zac-bedrijfsgegevens",
  templateUrl: "./bedrijfsgegevens.component.html",
  styleUrls: ["./bedrijfsgegevens.component.less"],
})
export class BedrijfsgegevensComponent implements OnChanges {
  @Input() isVerwijderbaar: boolean;
  @Input() isWijzigbaar: boolean;
  @Input() rsinOfVestigingsnummer: string;
  @Output() delete = new EventEmitter<Bedrijf>();
  @Output() edit = new EventEmitter<Bedrijf>();

  vestigingsprofielOphalenMogelijk = true;
  vestigingsprofiel: Vestigingsprofiel = null;
  bedrijf: Bedrijf;
  klantExpanded: boolean;

  constructor(private klantenService: KlantenService) {}

  ngOnChanges(): void {
    this.bedrijf = null;
    this.vestigingsprofiel = null;
    if (this.rsinOfVestigingsnummer) {
      this.klantenService
        .readBedrijf(this.rsinOfVestigingsnummer)
        .subscribe((bedrijf) => {
          this.bedrijf = bedrijf;
          this.klantExpanded = true;
          this.vestigingsprofielOphalenMogelijk =
            !!this.bedrijf.vestigingsnummer;
        });
    }
  }

  ophalenVestigingsprofiel() {
    this.vestigingsprofielOphalenMogelijk = false;
    this.klantenService
      .readVestigingsprofiel(this.bedrijf.vestigingsnummer)
      .subscribe((value) => {
        this.vestigingsprofiel = value;
      });
  }
}
