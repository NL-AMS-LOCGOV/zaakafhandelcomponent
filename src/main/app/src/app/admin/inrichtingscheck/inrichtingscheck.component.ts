/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { AfterViewInit, Component, OnInit, ViewChild } from "@angular/core";
import { UtilService } from "../../core/service/util.service";
import { MatSidenav, MatSidenavContainer } from "@angular/material/sidenav";
import { MatTableDataSource } from "@angular/material/table";
import { MatSort, Sort } from "@angular/material/sort";
import { AdminComponent } from "../admin/admin.component";
import { ToggleSwitchOptions } from "../../shared/table-zoek-filters/toggle-filter/toggle-switch-options";
import { HealthCheckService } from "../health-check.service";
import { ZaaktypeInrichtingscheck } from "../model/zaaktype-inrichtingscheck";
import {
  animate,
  state,
  style,
  transition,
  trigger,
} from "@angular/animations";
import { VersionLayout } from "../../shared/version/version.component";

@Component({
  templateUrl: "./inrichtingscheck.component.html",
  styleUrls: ["./inrichtingscheck.component.less"],
  animations: [
    trigger("detailExpand", [
      state("collapsed", style({ height: "0px", minHeight: "0" })),
      state("expanded", style({ height: "*" })),
      transition(
        "expanded <=> collapsed",
        animate("225ms cubic-bezier(0.4, 0.0, 0.2, 1)"),
      ),
    ]),
  ],
})
export class InrichtingscheckComponent
  extends AdminComponent
  implements OnInit, AfterViewInit
{
  @ViewChild("sideNavContainer") sideNavContainer: MatSidenavContainer;
  @ViewChild("menuSidenav") menuSidenav: MatSidenav;
  @ViewChild(MatSort) sort: MatSort;

  readonly versionLayout = VersionLayout;
  dataSource: MatTableDataSource<ZaaktypeInrichtingscheck> =
    new MatTableDataSource<ZaaktypeInrichtingscheck>();
  loadingZaaktypes = true;
  loadingCommunicatiekanaal = true;
  columnsToDisplay = [
    "valide",
    "expand",
    "zaaktypeOmschrijving",
    "zaaktypeDoel",
    "beginGeldigheid",
  ];
  zaaktypes: ZaaktypeInrichtingscheck[];
  expandedRow: ZaaktypeInrichtingscheck | null;
  valideFilter: ToggleSwitchOptions = ToggleSwitchOptions.UNCHECKED;
  filterValue = "";
  bestaatCommunicatiekanaalEformulier: boolean;
  ztcCacheTime: string;

  constructor(
    private healtCheckService: HealthCheckService,
    public utilService: UtilService,
  ) {
    super(utilService);
  }

  ngOnInit(): void {
    this.setupMenu("title.inrichtingscheck");
  }

  ngAfterViewInit(): void {
    super.ngAfterViewInit();
    this.dataSource.sortingDataAccessor = (item, property) => {
      switch (property) {
        case "zaaktypeOmschrijving":
          return item.zaaktype.omschrijving.toLowerCase();
        case "zaaktypeDoel":
          return item.zaaktype.doel;
        case "beginGeldigheid":
          return item.zaaktype.beginGeldigheid;
        default:
          return item[property];
      }
    };
    this.dataSource.sort = this.sort;
    this.dataSource.filterPredicate = (data, filter: string) => {
      if (this.valideFilter === ToggleSwitchOptions.CHECKED && !data.valide) {
        return false;
      }
      if (this.valideFilter === ToggleSwitchOptions.UNCHECKED && data.valide) {
        return false;
      }
      const dataString = (data.zaaktype.omschrijving + " " + data.zaaktype.doel)
        .trim()
        .toLowerCase();
      return dataString.indexOf(filter.trim().toLowerCase()) !== -1;
    };

    this.healtCheckService
      .readBestaatCommunicatiekanaalEformulier()
      .subscribe((value) => {
        this.loadingCommunicatiekanaal = false;
        this.bestaatCommunicatiekanaalEformulier = value;
      });

    this.checkZaaktypes();
    this.healtCheckService.readZTCCacheTime().subscribe((value) => {
      this.ztcCacheTime = value;
    });
  }

  applyFilter(event?: Event) {
    if (event) {
      const filterValue = (event.target as HTMLInputElement).value;
      this.filterValue = filterValue.trim().toLowerCase();
      this.dataSource.filter = filterValue;
    } else {
      // toggleSwitch
      this.dataSource.filter = " " + this.filterValue;
    }
  }

  clearZTCCache($event: MouseEvent) {
    $event.stopPropagation();
    this.healtCheckService.clearZTCCaches().subscribe((value) => {
      this.ztcCacheTime = value;
      this.checkZaaktypes();
    });
  }

  checkZaaktypes() {
    this.loadingZaaktypes = true;
    this.dataSource.data = [];
    this.healtCheckService
      .listZaaktypeInrichtingschecks()
      .subscribe((value) => {
        this.loadingZaaktypes = false;
        this.dataSource.data = value.sort((a, b) =>
          a.zaaktype.omschrijving.localeCompare(b.zaaktype.omschrijving),
        );
        this.applyFilter();
      });
  }

  sortData(sort: Sort) {
    if (!sort.active || sort.direction === "") {
      return;
    }

    this.dataSource.data = this.dataSource.data.slice().sort((a, b) => {
      const isAsc = sort.direction === "asc";
      switch (sort.active) {
        case "zaaktypeOmschrijving":
          return this.compare(
            a.zaaktype.omschrijving,
            b.zaaktype.omschrijving,
            isAsc,
          );
        case "doel":
          return this.compare(a.zaaktype.doel, b.zaaktype.doel, isAsc);
        case "beginGeldigheid":
          return this.compare(
            a.zaaktype.beginGeldigheid,
            b.zaaktype.beginGeldigheid,
            isAsc,
          );
        case "valide":
          return this.compare(a.valide, b.valide, isAsc);
        default:
          return 0;
      }
    });
  }

  compare(
    a: number | string | boolean,
    b: number | string | boolean,
    isAsc: boolean,
  ) {
    return (a < b ? -1 : 1) * (isAsc ? 1 : -1);
  }
}
