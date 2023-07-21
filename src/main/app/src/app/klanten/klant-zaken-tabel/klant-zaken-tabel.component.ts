/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { UtilService } from "../../core/service/util.service";
import {
  AfterViewInit,
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  SimpleChanges,
  ViewChild,
} from "@angular/core";
import { merge, Observable } from "rxjs";
import { ZoekenService } from "../../zoeken/zoeken.service";
import { MatTableDataSource } from "@angular/material/table";
import { ZaakZoekObject } from "../../zoeken/model/zaken/zaak-zoek-object";
import { ZoekParameters } from "../../zoeken/model/zoek-parameters";
import { ZoekObjectType } from "../../zoeken/model/zoek-object-type";
import { ZoekResultaat } from "../../zoeken/model/zoek-resultaat";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { map, startWith, switchMap } from "rxjs/operators";
import { SorteerVeld } from "../../zoeken/model/sorteer-veld";
import { ZoekVeld } from "../../zoeken/model/zoek-veld";
import { FormControl } from "@angular/forms";

@Component({
  selector: "zac-klant-zaken-tabel",
  templateUrl: "./klant-zaken-tabel.component.html",
  styleUrls: ["./klant-zaken-tabel.component.less"],
})
export class KlantZakenTabelComponent
  implements OnInit, AfterViewInit, OnChanges
{
  @Input() klantIdentificatie: string;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;
  dataSource: MatTableDataSource<ZaakZoekObject> =
    new MatTableDataSource<ZaakZoekObject>();
  columns: string[] = [
    "identificatie",
    "betrokkene",
    "status",
    "groep",
    "behandelaar",
    "startdatum",
    "zaaktype",
    "omschrijving",
    "url",
  ];
  filterColumns: string[] = this.columns.map((n) => n + "_filter");
  isLoadingResults = true;
  sorteerVeld = SorteerVeld;
  filterChange: EventEmitter<void> = new EventEmitter<void>();
  zoekParameters = new ZoekParameters();
  actieveFilters = false;
  zoekResultaat: ZoekResultaat<ZaakZoekObject> =
    new ZoekResultaat<ZaakZoekObject>();
  init: boolean;
  inclusiefAfgerondeZaken = false;
  ZoekVeld = ZoekVeld;
  betrokkeneSelectControl = new FormControl<ZoekVeld>(null);
  private laatsteBetrokkenheid: string;

  constructor(
    private utilService: UtilService,
    private zoekenService: ZoekenService,
  ) {}

  ngOnInit(): void {
    this.zoekParameters.type = ZoekObjectType.ZAAK;
  }

  private loadZaken(): Observable<ZoekResultaat<ZaakZoekObject>> {
    if (this.laatsteBetrokkenheid) {
      delete this.zoekParameters.zoeken[this.laatsteBetrokkenheid];
    }
    if (this.betrokkeneSelectControl.value) {
      this.setZoekParameterBetrokkenheid(this.betrokkeneSelectControl.value);
    }
    this.actieveFilters = ZoekParameters.heeftActieveFilters(
      this.zoekParameters,
    ); // before default values
    if (!this.betrokkeneSelectControl.value) {
      this.setZoekParameterBetrokkenheid(ZoekVeld.ZAAK_BETROKKENEN);
    }
    this.zoekParameters.page = this.paginator.pageIndex;
    this.zoekParameters.sorteerRichting = this.sort.direction;
    this.zoekParameters.sorteerVeld = SorteerVeld[this.sort.active];
    this.zoekParameters.rows = this.paginator.pageSize;
    this.zoekParameters.alleenOpenstaandeZaken = !this.inclusiefAfgerondeZaken;
    return this.zoekenService.list(this.zoekParameters) as Observable<
      ZoekResultaat<ZaakZoekObject>
    >;
  }

  private setZoekParameterBetrokkenheid(betrokkenheid: ZoekVeld) {
    this.zoekParameters.zoeken[(this.laatsteBetrokkenheid = betrokkenheid)] =
      this.klantIdentificatie;
  }

  ngAfterViewInit(): void {
    this.init = true;
    this.filtersChanged();
    this.sort.sortChange.subscribe(() => (this.paginator.pageIndex = 0));
    merge(this.sort.sortChange, this.paginator.page, this.filterChange)
      .pipe(
        startWith({}),
        switchMap(() => {
          this.isLoadingResults = true;
          this.utilService.setLoading(true);
          return this.loadZaken();
        }),
        map((zoekResultaat) => {
          this.isLoadingResults = false;
          this.utilService.setLoading(false);
          return zoekResultaat;
        }),
      )
      .subscribe((zoekResultaat) => {
        this.zoekResultaat = zoekResultaat;
        this.paginator.length = zoekResultaat.totaal;
        this.dataSource.data = zoekResultaat.resultaten;
      });
  }

  getBetrokkenheid(zaak: ZaakZoekObject): string[] {
    const rollen = [];
    Object.entries(zaak.betrokkenen).forEach((value: [string, string[]]) => {
      const rol = value[0];
      const ids = value[1];
      if (ids.includes(this.klantIdentificatie)) {
        rollen.push(rol);
      }
    });
    return rollen;
  }

  filtersChanged(): void {
    this.paginator.pageIndex = 0;
    this.filterChange.emit();
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.klantIdentificatie = changes.klantIdentificatie.currentValue;
    if (this.init) {
      this.filtersChanged();
    }
  }

  clearFilters() {
    this.sort.sort({ id: null, start: "asc", disableClear: false });
    this.zoekParameters.zoeken = {};
    this.zoekParameters.filters = {};
    this.zoekParameters.datums = {};
    this.betrokkeneSelectControl.setValue(null);
    this.filtersChanged();
  }
}
