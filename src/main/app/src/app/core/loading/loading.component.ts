/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, OnDestroy, OnInit } from "@angular/core";
import { UtilService } from "../service/util.service";
import { Subscription } from "rxjs";

@Component({
  selector: "zac-loading",
  templateUrl: "./loading.component.html",
  styleUrls: ["./loading.component.less"],
})
export class LoadingComponent implements OnInit, OnDestroy {
  loading: boolean;

  private subscription$: Subscription;

  constructor(private utilService: UtilService) {}

  ngOnInit(): void {
    this.subscription$ = this.utilService.loading$.subscribe(
      (value) => (this.loading = value),
    );
  }

  ngOnDestroy(): void {
    this.subscription$.unsubscribe();
  }
}
