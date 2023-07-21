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
import { Persoon } from "../model/personen/persoon";

@Component({
  selector: "zac-persoongegevens",
  styleUrls: ["./persoonsgegevens.component.less"],
  templateUrl: "./persoonsgegevens.component.html",
})
export class PersoonsgegevensComponent implements OnChanges {
  @Input() isVerwijderbaar: boolean;
  @Input() isWijzigbaar: boolean;
  @Output() delete = new EventEmitter<Persoon>();
  @Output() edit = new EventEmitter<Persoon>();
  @Input() bsn: string;

  persoon: Persoon;
  klantExpanded: boolean;

  constructor(private klantenService: KlantenService) {}

  ngOnChanges(): void {
    this.persoon = null;
    this.klantExpanded = false;
    if (this.bsn) {
      this.klantenService.readPersoon(this.bsn).subscribe((persoon) => {
        this.persoon = persoon;
        this.klantExpanded = true;
      });
    }
  }
}
