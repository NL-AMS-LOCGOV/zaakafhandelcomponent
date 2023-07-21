/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Injectable } from "@angular/core";
import {
  NavigationCancel,
  NavigationEnd,
  NavigationError,
  NavigationStart,
  Router,
} from "@angular/router";
import { Location } from "@angular/common";
import { BehaviorSubject, Observable } from "rxjs";
import { UtilService } from "../../core/service/util.service";
import { filter } from "rxjs/operators";
import { SessionStorageUtil } from "../storage/session-storage.util";

@Injectable({
  providedIn: "root",
})
export class NavigationService {
  private static NAVIGATION_HISTORY = "navigationHistory";
  private backDisabled: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(
    false,
  );
  public backDisabled$: Observable<boolean> = this.backDisabled.asObservable();

  constructor(
    private router: Router,
    private location: Location,
    private utilService: UtilService,
  ) {
    router.events
      .pipe(
        filter(
          (e) =>
            e instanceof NavigationStart ||
            e instanceof NavigationEnd ||
            e instanceof NavigationCancel ||
            e instanceof NavigationError,
        ),
      )
      .subscribe((e) => this.handleRouterEvents(e));
  }

  private handleRouterEvents(e): void {
    if (e instanceof NavigationStart) {
      this.utilService.setLoading(true);
    } else {
      if (e instanceof NavigationEnd) {
        const history = SessionStorageUtil.getItem(
          NavigationService.NAVIGATION_HISTORY,
          [],
        );
        if (
          history.length === 0 ||
          history[history.length - 1] !== e.urlAfterRedirects
        ) {
          history.push(e.urlAfterRedirects);
        }
        SessionStorageUtil.setItem(
          NavigationService.NAVIGATION_HISTORY,
          history,
        );
        this.backDisabled.next(history.length <= 1);
      }

      this.utilService.setLoading(false);
    }
  }

  back(): void {
    const history = SessionStorageUtil.getItem(
      NavigationService.NAVIGATION_HISTORY,
      [],
    );
    history.pop();
    if (history.length > 0) {
      this.location.back();
    } else {
      this.router.navigate([".."]);
    }
    SessionStorageUtil.setItem(NavigationService.NAVIGATION_HISTORY, history);
    this.backDisabled.next(history.length <= 1);
  }
}
