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
import {Operatie} from './model/operatie';
import {ObjectType} from './model/object-type';
import {ScreenUpdateEvent} from './model/screen-update-event';
import {EventCallback} from './model/event-callback';
import {MatSnackBar} from '@angular/material/snack-bar';
import {TranslateService} from '@ngx-translate/core';

@Injectable({
    providedIn: 'root'
})
export class WebsocketService implements OnDestroy {

    public static test: boolean = false; // Als true dan wordt de mock gebruikt

    private readonly PROTOCOL: string = window.location.protocol.replace(/^http/, 'ws');

    private readonly HOST: string = window.location.host;

    private readonly CONTEXT: string = 'zac';

    private readonly URL: string = this.PROTOCOL + '//' + this.HOST + '/' + this.CONTEXT + '/websocket';

    private connection$: WebSocketSubject<any>;

    private destroyed$ = new Subject();

    private listeners: EventCallback[][] = [];

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
            // Simuleert een inkomend websocketbericht na een vertraging (in ms) die uit het objectId gehaald wordt
            var delay: number = Number(data.event.objectId);
            var event: ScreenUpdateEvent = data.event;
            event.timestamp = 0;
            setInterval(() => {
                this.onMessage(event);
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
        var event: ScreenUpdateEvent = new ScreenUpdateEvent(message.operatie, message.objectType, message.objectId, message.timestamp);
        var callbacks: EventCallback[] = this.getCallbacks(event);
        for (var i = 0; i < callbacks.length; i++) {
            try {
                callbacks[i](event);
            } catch (error) {
                console.warn('Websocket callback error: ');
                console.debug(error);
            }
        }
    };

    private onError = (error: any) => {
        console.error('Websocket error:');
        console.debug(error);
    };

    public addListener(operatie: Operatie, objectType: ObjectType, objectId: string, callback: EventCallback): void {
        var event: ScreenUpdateEvent = new ScreenUpdateEvent(operatie, objectType, objectId);
        this.addCallback(event, callback);
        this.send(new SubscriptionMessage(SubscriptionType.TOEVOEGEN, event));
    }

    public addListenerMetSnackbar(operatie: Operatie, objectType: ObjectType, objectId: string, callback: EventCallback): void {
        this.addListener(operatie, objectType, objectId, (event) => {
            forkJoin({
                snackbar1: this.translate.get('objecttype.' + objectType),
                snackbar2: this.translate.get('snackbar.2'),
                snackbar3: this.translate.get('operatie.' + operatie),
                snackbar4: this.translate.get('snackbar.4'),
                actie: this.translate.get('snackbar.actie')
            }).subscribe(result => {
                this.snackbar.open(result.snackbar1 + result.snackbar2 + result.snackbar3 + result.snackbar4, result.actie)
                    .onAction().subscribe(() => callback(event));
            });
        });
    }

    public removeListeners(operatie: Operatie, objectType: ObjectType, objectId: string): void {
        var event: ScreenUpdateEvent = new ScreenUpdateEvent(operatie, objectType, objectId);
        this.removeCallbacks(event);
        this.send(new SubscriptionMessage(SubscriptionType.VERWIJDEREN, event));
    }

    private getCallbacks(event: ScreenUpdateEvent): EventCallback[] {
        if (!this.listeners[event.key]) {
            this.listeners[event.key] = [];
        }
        return this.listeners[event.key];
    }

    private addCallback(event: ScreenUpdateEvent, callback: EventCallback): void {
        var callbacks: EventCallback[] = this.getCallbacks(event);
        callbacks.push(callback);
    }

    private removeCallbacks(event: ScreenUpdateEvent): void {
        var callbacks: EventCallback[] = this.getCallbacks(event);
        callbacks.length = 0;
    }
}
