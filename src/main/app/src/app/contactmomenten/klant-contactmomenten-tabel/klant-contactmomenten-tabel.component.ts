/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {
  AfterViewInit,
  Component,
  Input,
  OnChanges,
  OnInit,
  SimpleChanges,
  ViewChild,
} from "@angular/core";
import { MatPaginator } from "@angular/material/paginator";
import { MatTableDataSource } from "@angular/material/table";
import { Contactmoment } from "../model/contactmoment";
import { ContactmomentenService } from "../contactmomenten.service";
import { Observable } from "rxjs";
import { Resultaat } from "../../shared/model/resultaat";
import { ListContactmomentenParameters } from "../model/list-contactmomenten-parameters";
import { UtilService } from "../../core/service/util.service";
import { map, startWith, switchMap } from "rxjs/operators";

@Component({
  selector: "zac-klant-contactmomenten-tabel",
  templateUrl: "./klant-contactmomenten-tabel.component.html",
  styleUrls: ["./klant-contactmomenten-tabel.component.less"],
})
export class KlantContactmomentenTabelComponent
  implements OnInit, AfterViewInit, OnChanges
{
  @Input() bsn: string;
  @Input() vestigingsnummer: string;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  dataSource: MatTableDataSource<Contactmoment> =
    new MatTableDataSource<Contactmoment>();
  columns: string[] = [
    "registratiedatum",
    "kanaal",
    "initiatiefnemer",
    "medewerker",
    "tekst",
  ];
  listParameters = new ListContactmomentenParameters();
  resultaat: Resultaat<Contactmoment> = new Resultaat<Contactmoment>();
  init: boolean;
  isLoadingResults = true;

  constructor(
    private contactmomentenService: ContactmomentenService,
    private utilService: UtilService,
  ) {}

  ngOnInit(): void {
    this.listParameters.bsn = this.bsn;
    this.listParameters.vestigingsnummer = this.vestigingsnummer;
  }

  ngAfterViewInit(): void {
    this.init = true;
    this.paginator.page
      .pipe(
        startWith({}),
        switchMap(() => {
          this.isLoadingResults = true;
          this.utilService.setLoading(true);
          return this.loadContactmomenten();
        }),
        map((resultaat) => {
          this.isLoadingResults = false;
          this.utilService.setLoading(false);
          return resultaat;
        }),
      )
      .subscribe((resultaat) => {
        this.resultaat = resultaat;
        this.paginator.length = resultaat.totaal;
        this.dataSource.data = resultaat.resultaten;
      });
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.bsn = changes.bsn?.currentValue;
    this.vestigingsnummer = changes.vestigingsnummer?.currentValue;
    if (this.init) {
      this.paginator.pageIndex = 0;
      this.paginator.page.emit();
    }
  }

  private loadContactmomenten(): Observable<Resultaat<Contactmoment>> {
    this.listParameters.page = this.paginator.pageIndex;
    this.listParameters.pageSize = this.paginator.pageSize;
    return this.contactmomentenService.listContactmomenten(this.listParameters);
  }
}
