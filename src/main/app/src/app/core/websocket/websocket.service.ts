/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Injectable, OnDestroy } from "@angular/core";
import { webSocket, WebSocketSubject } from "rxjs/webSocket";
import { forkJoin, Observable, of, Subject } from "rxjs";
import { delay, retryWhen, switchMap, takeUntil } from "rxjs/operators";
import { SubscriptionMessage } from "./model/subscription-message";
import { SubscriptionType } from "./model/subscription-type";
import { Opcode } from "./model/opcode";
import { ObjectType } from "./model/object-type";
import { ScreenEvent } from "./model/screen-event";
import { EventCallback } from "./model/event-callback";
import { TranslateService } from "@ngx-translate/core";
import { WebsocketListener } from "./model/websocket-listener";
import { EventSuspension } from "./model/event-suspension";
import { UtilService } from "../service/util.service";
import { ScreenEventId } from "./model/screen-event-id";

@Injectable({
  providedIn: "root",
})
export class WebsocketService implements OnDestroy {
  public static test = false; // Als true dan wordt de mock gebruikt

  // This must be bigger then the SECONDS_TO_DELAY defined in ScreenEventObserver.java
  private static DEFAULT_SUSPENSION_TIMEOUT = 5; // seconds

  private readonly PROTOCOL: string = window.location.protocol.replace(
    /^http/,
    "ws",
  );

  private readonly HOST: string = window.location.host.replace(
    "localhost:4200",
    "localhost:8080",
  );

  private readonly URL: string =
    this.PROTOCOL + "//" + this.HOST + "/websocket";

  private connection$: WebSocketSubject<any>;

  private destroyed$ = new Subject<void>();

  private listeners: EventCallback[][] = [];

  private suspended: EventSuspension[] = [];

  constructor(
    private translate: TranslateService,
    private utilService: UtilService,
  ) {
    if (WebsocketService.test) {
      this.mock();
    } else {
      this.receive(this.URL);
    }
  }

  ngOnDestroy(): void {
    this.destroyed$.next();
    this.destroyed$.complete();
    this.close();
  }

  private open(url: string): Observable<any> {
    return of(url).pipe(
      switchMap((openUrl) => {
        if (!this.connection$) {
          this.connection$ = webSocket(openUrl);
          console.log("Websocket geopend: " + openUrl);
        }
        return this.connection$;
      }),
      retryWhen((errors) => errors.pipe(delay(7))),
    );
  }

  private receive(websocket: string) {
    this.open(websocket).pipe(takeUntil(this.destroyed$)).subscribe({
      next: this.onMessage,
      error: this.onError,
    });
  }

  private mock() {
    console.warn("Websocket is een mock");
    this.send = (data: any) => {
      // Simulates one (i.e. 1) incoming websocket message for the event, after a delay (in ms) which gets derived from the objectId.
      const wsDelay = Number(data.event.objectId);
      const event: ScreenEvent = data.event;
      setTimeout(() => {
        this.onMessage(
          new ScreenEvent(event.opcode, event.objectType, event.objectId, 0),
        );
      }, wsDelay);
    };
  }

  private send(data: any) {
    if (this.connection$) {
      this.connection$.next(data);
    } else {
      console.error("Websocket is niet open");
    }
  }

  private close() {
    if (this.connection$) {
      this.connection$.complete();
      console.warn("Websocket gesloten");
      this.connection$ = null;
    }
  }

  private onMessage = (message: any) => {
    // message is a JSON representation of ScreenEvent.java
    const event: ScreenEvent = new ScreenEvent(
      message.opcode,
      message.objectType,
      message.objectId,
      message.timestamp,
    );
    this.dispatch(event, event.key);
    this.dispatch(event, event.keyAnyOpcode);
    this.dispatch(event, event.keyAnyObjectType);
    this.dispatch(event, event.keyAnyOpcodeAndObjectType);
  };

  private dispatch(event: ScreenEvent, key: string) {
    const callbacks: EventCallback[] = this.getCallbacks(key);
    for (const listenerId in callbacks) {
      if (callbacks.hasOwnProperty(listenerId) && listenerId !== "length") {
        try {
          if (!this.isSuspended(listenerId)) {
            console.debug("listener call: " + key);
            callbacks[listenerId](event);
          }
        } catch (error) {
          console.warn("Websocket callback error: ");
          console.error(error);
        }
      }
    }
  }

  private onError = (error: any) => {
    console.error("Websocket error:");
    console.error(error);
  };

  public addListener(
    opcode: Opcode,
    objectType: ObjectType,
    objectId: string,
    callback: EventCallback,
  ): WebsocketListener {
    const event: ScreenEvent = new ScreenEvent(
      opcode,
      objectType,
      new ScreenEventId(objectId),
    );
    const listener: WebsocketListener = this.addCallback(event, callback);
    this.send(new SubscriptionMessage(SubscriptionType.CREATE, event));
    console.debug("listener added: " + listener.key);
    return listener;
  }

  public addListenerWithSnackbar(
    opcode: Opcode,
    objectType: ObjectType,
    objectId: string,
    callback: EventCallback,
  ): WebsocketListener {
    return this.addListener(opcode, objectType, objectId, (event) => {
      forkJoin({
        msgPart1: this.translate.get(
          "msg.gewijzigd.objecttype." + event.objectType,
        ),
        msgPart2: this.translate.get(
          event.objectType.indexOf("_") < 0
            ? "msg.gewijzigd.2"
            : "msg.gewijzigd.2.details",
        ),
        msgPart3: this.translate.get("msg.gewijzigd.operatie." + event.opcode),
        msgPart4: this.translate.get("msg.gewijzigd.4"),
      }).subscribe((result) => {
        callback(event);
        this.utilService.openSnackbar(
          result.msgPart1 + result.msgPart2 + result.msgPart3 + result.msgPart4,
        );
      });
    });
  }

  public suspendListener(
    listener: WebsocketListener,
    timeout: number = WebsocketService.DEFAULT_SUSPENSION_TIMEOUT,
  ): void {
    if (listener) {
      const suspension: EventSuspension = this.suspended[listener.id];
      if (suspension) {
        suspension.increment();
      } else {
        this.suspended[listener.id] = new EventSuspension(timeout);
      }
      console.debug("listener suspended: " + listener.key);
    }
  }

  public doubleSuspendListener(listener: WebsocketListener) {
    this.suspendListener(listener);
    this.suspendListener(listener);
  }

  public removeListener(listener: WebsocketListener): void {
    if (listener) {
      this.removeCallback(listener);
      this.send(
        new SubscriptionMessage(SubscriptionType.DELETE, listener.event),
      );
      console.debug("listener removed: " + listener.key);
    }
  }

  public removeListeners(listeners: WebsocketListener[]): void {
    listeners.forEach((listener) => {
      this.removeListener(listener);
    });
  }

  private addCallback(
    event: ScreenEvent,
    callback: EventCallback,
  ): WebsocketListener {
    const listener: WebsocketListener = new WebsocketListener(event, callback);
    const callbacks: EventCallback[] = this.getCallbacks(event.key);
    callbacks[listener.id] = callback;
    return listener;
  }

  private removeCallback(listener: WebsocketListener): void {
    const callbacks: EventCallback[] = this.getCallbacks(listener.event.key);
    delete callbacks[listener.id];
    delete this.suspended[listener.id];
  }

  private getCallbacks(key: string): EventCallback[] {
    if (!this.listeners[key]) {
      this.listeners[key] = [];
    }
    return this.listeners[key];
  }

  private isSuspended(listenerId: string): boolean {
    const suspension: EventSuspension = this.suspended[listenerId];
    if (suspension) {
      const expired: boolean = suspension.isExpired();
      const done = suspension.isDone(); // Do not short circuit calling this method (here be side effects)
      if (done || expired) {
        delete this.suspended[listenerId];
      }
      return !expired;
    }
    return false;
  }
}
