/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable, OnDestroy} from '@angular/core';
import {webSocket, WebSocketSubject} from 'rxjs/webSocket';
import {forkJoin, Observable, of, Subject} from 'rxjs';
import {delay, retryWhen, switchMap, takeUntil} from 'rxjs/operators';
import {SubscriptionMessage} from './model/subscription-message';
import {SubscriptionType} from './model/subscription-type';
import {Opcode} from './model/opcode';
import {ObjectType} from './model/object-type';
import {ScreenEvent} from './model/screen-event';
import {EventCallback} from './model/event-callback';
import {MatSnackBar} from '@angular/material/snack-bar';
import {TranslateService} from '@ngx-translate/core';
import {WebsocketListener} from './model/websocket-listener';
import {EventSuspension} from './model/event-suspension';

@Injectable({
    providedIn: 'root'
})
export class WebsocketService implements OnDestroy {

    public static test: boolean = false; // Als true dan wordt de mock gebruikt

    private static DEFAULT_SUSPENSION_TIMEOUT: number = 5; // seconds

    private readonly PROTOCOL: string = window.location.protocol.replace(/^http/, 'ws');

    private readonly HOST: string = window.location.host;

    private readonly URL: string = this.PROTOCOL + '//' + this.HOST + '/websocket';

    private connection$: WebSocketSubject<any>;

    private destroyed$ = new Subject();

    private listeners: EventCallback[][] = [];

    private suspended: EventSuspension[] = [];

    constructor(private snackbar: MatSnackBar,
                private translate: TranslateService) {
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
            switchMap(url => {
                if (!this.connection$) {
                    this.connection$ = webSocket(url);
                    console.log('Websocket geopend: ' + url);
                }
                return this.connection$;
            })
            , retryWhen((errors) =>
                errors.pipe(delay(7)))
        );
    }

    private receive(websocket: string) {
        this.open(websocket).pipe(
            takeUntil(this.destroyed$)
        ).subscribe(
            this.onMessage,
            this.onError);
    }

    private mock() {
        console.warn('Websocket is een mock');
        this.send = function (data: any) {
            // Simulates one (i.e. 1) incoming websocket message for the event, after a delay (in ms) which gets derived from the objectId.
            var delay: number = Number(data.event.objectId);
            var event: ScreenEvent = data.event;
            setTimeout(() => {
                this.onMessage(new ScreenEvent(event.opcode, event.objectType, event.objectId, 0));
            }, delay);
        };
    }

    private send(data: any) {
        if (this.connection$) {
            this.connection$.next(data);
        } else {
            console.error('Websocket is niet open');
        }
    }

    private close() {
        if (this.connection$) {
            this.connection$.complete();
            console.warn('Websocket gesloten');
            this.connection$ = null;
        }
    }

    private onMessage = (message: any) => {
        var event: ScreenEvent = new ScreenEvent(message.opcode, message.objectType, message.objectId, message.timestamp);
        var callbacks: EventCallback[] = this.getCallbacks(event);
        for (var listenerId in callbacks) {
            if (callbacks.hasOwnProperty(listenerId) && listenerId != 'length') {
                try {
                    if (!this.isSuspended(listenerId)) {
                        callbacks[listenerId](event);
                    }
                } catch (error) {
                    console.warn('Websocket callback error: ');
                    console.debug(error);
                }
            }
        }
    };

    private onError = (error: any) => {
        console.error('Websocket error:');
        console.debug(error);
    };

    public addListener(opcode: Opcode, objectType: ObjectType, objectId: string, callback: EventCallback): WebsocketListener {
        var event: ScreenEvent = new ScreenEvent(opcode, objectType, objectId);
        var listener: WebsocketListener = this.addCallback(event, callback);
        this.send(new SubscriptionMessage(SubscriptionType.CREATE, event));
        return listener;
    }

    public addListenerMetSnackbar(opcode: Opcode, objectType: ObjectType, objectId: string, callback: EventCallback): WebsocketListener {
        return this.addListener(opcode, objectType, objectId, (event) => {
            forkJoin({
                snackbar1: this.translate.get('msg.gewijzigd.objecttype.' + objectType),
                snackbar2: this.translate.get('msg.gewijzigd.2'),
                snackbar3: this.translate.get('msg.gewijzigd.operatie.' + opcode),
                snackbar4: this.translate.get('msg.gewijzigd.4'),
                actie: this.translate.get('actie.scherm.verversen')
            }).subscribe(result => {
                this.snackbar.open(result.snackbar1 + result.snackbar2 + result.snackbar3 + result.snackbar4, result.actie)
                    .onAction().subscribe(() => callback(event));
            });
        });
    }

    public suspendListener(listener: WebsocketListener, timeout: number = WebsocketService.DEFAULT_SUSPENSION_TIMEOUT): void {
        if (listener) {
            var suspension: EventSuspension = this.suspended[listener.id];
            if (suspension) {
                suspension.increment();
            } else {
                this.suspended[listener.id] = new EventSuspension(timeout);
            }
        }
    }

    public removeListener(listener: WebsocketListener): void {
        if (listener) {
            this.removeCallback(listener);
            this.send(new SubscriptionMessage(SubscriptionType.DELETE, listener.event));
        }
    }

    private addCallback(event: ScreenEvent, callback: EventCallback): WebsocketListener {
        var listener: WebsocketListener = new WebsocketListener(event, callback);
        var callbacks: EventCallback[] = this.getCallbacks(event);
        callbacks[listener.id] = callback;
        return listener;
    }

    private removeCallback(listener: WebsocketListener): void {
        var callbacks: EventCallback[] = this.getCallbacks(listener.event);
        delete callbacks[listener.id];
        delete this.suspended[listener.id];
    }

    private getCallbacks(event: ScreenEvent): EventCallback[] {
        if (!this.listeners[event.key]) {
            this.listeners[event.key] = [];
        }
        return this.listeners[event.key];
    }

    private isSuspended(listenerId: string): boolean {
        var suspension: EventSuspension = this.suspended[listenerId];
        if (suspension) {
            var expired: boolean = suspension.isExpired();
            var done = suspension.isDone(); // Do not short circuit calling this method (here be side effects)
            if (done || expired) {
                delete this.suspended[listenerId];
            }
            return !expired;
        }
        return false;
    }
}
