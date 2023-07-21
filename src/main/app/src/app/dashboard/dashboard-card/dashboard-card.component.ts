/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {
  AfterViewInit,
  Component,
  Input,
  OnDestroy,
  OnInit,
  ViewChild,
} from "@angular/core";
import { DashboardCard } from "../model/dashboard-card";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatTableDataSource } from "@angular/material/table";
import { interval, Observable, Subject, Subscription } from "rxjs";
import { Opcode } from "../../core/websocket/model/opcode";
import { ObjectType } from "../../core/websocket/model/object-type";
import { ScreenEvent } from "../../core/websocket/model/screen-event";
import { IdentityService } from "../../identity/identity.service";
import { WebsocketService } from "../../core/websocket/websocket.service";
import { SignaleringType } from "../../shared/signaleringen/signalering-type";

@Component({
  template: "",
  styleUrls: ["./dashboard-card.component.less"],
})
export abstract class DashboardCardComponent<T>
  implements OnInit, AfterViewInit, OnDestroy
{
  private readonly RELOAD_INTERVAL: number = 60; // 1 min.

  @Input() data: DashboardCard;

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;
  dataSource: MatTableDataSource<T> = new MatTableDataSource<T>();

  protected reload: Observable<any>;
  private reloader: Subscription;

  abstract columns: string[];

  constructor(
    protected identityService: IdentityService,
    protected websocketService: WebsocketService,
  ) {}

  ngOnInit(): void {
    this.onLoad(this.afterLoad);
  }

  ngAfterViewInit(): void {
    if (this.reload == null) {
      if (this.data.signaleringType != null) {
        this.reload = this.refreshOnSignalering(this.data.signaleringType);
      } else {
        this.reload = this.refreshTimed(this.RELOAD_INTERVAL);
      }
    }
    this.reloader = this.reload.subscribe((next) => {
      this.onLoad(this.afterLoad);
    });
  }

  ngOnDestroy(): void {
    this.reloader.unsubscribe();
  }

  protected abstract onLoad(afterLoad: () => void): void;

  private readonly afterLoad = () => {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  };

  protected refreshTimed(seconds: number): Observable<number> {
    return interval(seconds * 1000);
  }

  protected refreshOnSignalering(
    signaleringType: SignaleringType,
  ): Observable<void> {
    const reload$: Subject<void> = new Subject<void>();
    this.identityService.readLoggedInUser().subscribe((medewerker) => {
      this.websocketService.addListener(
        Opcode.UPDATED,
        ObjectType.SIGNALERINGEN,
        medewerker.id,
        (event: ScreenEvent) => {
          if (event.objectId.detail === signaleringType) {
            reload$.next();
          }
        },
      );
    });
    return reload$;
  }
}
