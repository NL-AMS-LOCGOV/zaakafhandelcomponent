/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {
  AfterViewInit,
  Component,
  TemplateRef,
  ViewChild,
} from "@angular/core";
import { UtilService } from "../service/util.service";
import { Observable } from "rxjs";
import { ActionBarAction } from "./model/action-bar-action";
import {
  MatBottomSheet,
  MatBottomSheetRef,
} from "@angular/material/bottom-sheet";
import { Router } from "@angular/router";

@Component({
  selector: "zac-action-bar",
  templateUrl: "./action-bar.component.html",
  styleUrls: ["./action-bar.component.less"],
})
export class ActionBarComponent implements AfterViewInit {
  @ViewChild(TemplateRef) template: TemplateRef<any>;
  addAction$: Observable<ActionBarAction>;
  disableActionBar$: Observable<boolean>;
  actions: ActionBarAction[] = [];
  actionBarDisabled = false;
  matBottomSheetRef: MatBottomSheetRef;

  constructor(
    readonly bottomSheet: MatBottomSheet,
    public utilService: UtilService,
    private router: Router,
  ) {}

  actionTextClicked(action: ActionBarAction): void {
    action.action.iconClicked.next(this.router.url);
    this.dismissAction(action);
  }

  dismissAction(action: ActionBarAction) {
    const index: number = this.actions.indexOf(action);
    if (index > -1) {
      this.actions.splice(index, 1);
      action.dissmis.next();
    }
    if (this.actions.length === 0) {
      this.matBottomSheetRef.dismiss();
    }
  }

  ngAfterViewInit(): void {
    this.addAction$ = this.utilService.addAction$.asObservable();
    this.addAction$.subscribe((action) => {
      this.actions.push(action);
      if (this.actions.length === 1) {
        this.matBottomSheetRef = this.bottomSheet.open(this.template, {
          closeOnNavigation: false,
          hasBackdrop: false,
        });
      }
    });
    this.disableActionBar$ = this.utilService.disableActionBar$.asObservable();
    this.disableActionBar$.subscribe((state) => {
      this.actionBarDisabled = state;
    });
  }
}
