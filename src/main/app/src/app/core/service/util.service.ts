/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Inject, Injectable, Optional } from "@angular/core";
import { BehaviorSubject, iif, Observable, of, Subject } from "rxjs";
import { delay, map, shareReplay, switchMap } from "rxjs/operators";
import { BreakpointObserver, Breakpoints } from "@angular/cdk/layout";
import { TranslateService } from "@ngx-translate/core";
import { Title } from "@angular/platform-browser";
import { MatSnackBar } from "@angular/material/snack-bar";
import { DOCUMENT } from "@angular/common";
import { Router } from "@angular/router";
import { ActionBarAction } from "../actionbar/model/action-bar-action";
import { OrderUtil } from "../../shared/order/order-util";

@Injectable({
  providedIn: "root",
})
export class UtilService {
  private headerTitle: BehaviorSubject<string> = new BehaviorSubject<string>(
    "zaakafhandelcomponent",
  );
  private loading: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(
    false,
  );

  public headerTitle$: Observable<string> = this.headerTitle.asObservable();
  public addAction$ = new Subject<ActionBarAction>();
  public disableActionBar$ = new Subject<boolean>();

  public loading$: Observable<boolean> = this.loading.pipe(
    switchMap((loading) =>
      iif(() => loading, of(loading).pipe(delay(700)), of(loading)),
    ),
  );

  public isHandset$: Observable<boolean> = this.breakpointObserver
    .observe(Breakpoints.Handset)
    .pipe(
      map(({ matches }) => matches),
      shareReplay(),
    );

  public isTablet$: Observable<boolean> = this.breakpointObserver
    .observe([Breakpoints.Handset, Breakpoints.TabletPortrait])
    .pipe(
      map(({ matches }) => matches),
      shareReplay(),
    );

  constructor(
    @Optional() @Inject(DOCUMENT) private document: any,
    private breakpointObserver: BreakpointObserver,
    private translate: TranslateService,
    private titleService: Title,
    private router: Router,
    private snackbar: MatSnackBar,
  ) {}

  /**
   * Check whether there is an active edit overlay on the screen eg. autocomplete or datepicker
   */
  hasEditOverlay(): boolean {
    const overlayElements: any[] = this.getOverlayElements(
      "mat-autocomplete-panel",
      "mat-select-panel",
      "mat-datepicker-popup",
      "zac-is-invalid",
    );
    return overlayElements.length > 0;
  }

  private getOverlayElements(...classList): any[] {
    const overlayElements: any[] = [];
    for (const styleClass of classList) {
      overlayElements.push(...this.document.getElementsByClassName(styleClass));
    }
    return overlayElements;
  }

  setTitle(title: string, params?: {}): void {
    const _title = this.translate.instant(title, params);
    this.titleService.setTitle(
      this.translate.instant("title", { title: _title }),
    );
    this.headerTitle.next(_title);
  }

  setLoading(loading: boolean): void {
    this.loading.next(loading);
  }

  getEnumAsSelectList(
    prefix: string,
    enumValue: any,
  ): { label: string; value: string }[] {
    const list: { label: string; value: string }[] = [];
    Object.keys(enumValue).forEach((value) => {
      this.translate
        .get(`${prefix}.${enumValue[value]}`)
        .subscribe((result) => {
          list.push({ label: result, value: value });
        });
    });
    return list;
  }

  getEnumAsSelectListExceptFor(
    prefix: string,
    enum_: any,
    exceptEnumValues: [string],
  ): { label: string; value: string }[] {
    const list: { label: string; value: string }[] = [];
    Object.keys(enum_)
      .filter(
        (key) =>
          !exceptEnumValues.some(
            (acceptEnumValue) => acceptEnumValue === enum_[key],
          ),
      )
      .forEach((key) => {
        this.translate.get(`${prefix}.${enum_[key]}`).subscribe((result) => {
          list.push({ label: result, value: key });
        });
      });
    return list;
  }

  getEnumKeyByValue(enum_: any, value: any) {
    return Object.keys(enum_)[Object.values(enum_).indexOf(value)];
  }

  openSnackbarError(message: string, params?: {}) {
    this.openSnackbar(message, params, null);
  }

  openSnackbar(message: string, params?: {}, duration = 3) {
    this.openSnackbarAction(message, "actie.sluiten", params, 3);
  }

  openSnackbarAction(
    message: string,
    action: string,
    params?: {},
    durationSeconden?: number,
  ): Observable<void> {
    return this.snackbar
      .open(
        this.translate.instant(message, params),
        this.translate.instant(action, params),
        {
          panelClass: ["mat-snackbar"],
          duration: durationSeconden != null ? durationSeconden * 1000 : null,
        },
      )
      .onAction();
  }

  addAction(action: ActionBarAction) {
    this.addAction$.next(action);
  }

  disableActionBar(state: boolean) {
    this.disableActionBar$.next(state);
  }

  reloadRoute(): void {
    const currentUrl = this.router.url;
    this.router.navigateByUrl("/", { skipLocationChange: true }).then(() => {
      this.router.navigate([currentUrl]);
    });
  }

  getUniqueItemsList(
    source: any[],
    item: string,
    key: string,
    sortKey?: string,
  ): any[] {
    return source
      .map((value) => value[item])
      .filter((value, index, self) => {
        return value && self.findIndex((v) => v[key] === value[key]) === index;
      })
      .sort(OrderUtil.orderBy(sortKey));
  }

  downloadBlobResponse(response: Blob, fileName: string) {
    const link = document.createElement("a");
    link.href = window.URL.createObjectURL(response);
    link.download = fileName;
    document.body.appendChild(link);
    link.dispatchEvent(
      new MouseEvent("click", {
        bubbles: true,
        cancelable: true,
        view: window,
      }),
    );
    link.remove();
    window.URL.revokeObjectURL(link.href);
  }
}
