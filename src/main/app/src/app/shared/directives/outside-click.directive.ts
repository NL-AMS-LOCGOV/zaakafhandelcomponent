import {Directive, ElementRef, EventEmitter, Inject, OnDestroy, OnInit, Optional, Output, PLATFORM_ID} from '@angular/core';
import {fromEvent, Subscription} from 'rxjs';
import {DOCUMENT, isPlatformBrowser} from '@angular/common';
import {filter} from 'rxjs/operators';

@Directive({
    selector: '[zacOutsideClick]'
})
export class OutsideClickDirective implements OnInit, OnDestroy {

    @Output('zacOutsideClick') outsideClick = new EventEmitter<MouseEvent>();

    private subscription: Subscription;

    constructor(
        private element: ElementRef,
        @Optional() @Inject(DOCUMENT) private document: any,
        @Inject(PLATFORM_ID) private platformId: Object) { }

    ngOnInit() {
        if (!isPlatformBrowser(this.platformId)) {
            return;
        }

        setTimeout(() => {
            this.subscription = fromEvent<MouseEvent>(this.document, 'click')
            .pipe(
                filter(event => {
                    //block the outside click when an overlay is on the screen e.g. datepicker/select list
                    let overlayElements: HTMLCollection[] = this.document.getElementsByClassName('cdk-overlay-pane');
                    if (overlayElements.length > 0) {
                        return false;
                    }

                    const clickTarget = event.target as HTMLElement;
                    return !OutsideClickDirective.isOrContainsClickTarget(this.element.nativeElement, clickTarget);
                })
            )
            .subscribe(event => this.outsideClick.emit(event));
        }, 0);
    }

    ngOnDestroy() {
        if (this.subscription) {
            this.subscription.unsubscribe();
        }
    }

    private static isOrContainsClickTarget(element: HTMLElement, clickTarget: HTMLElement) {
        return element == clickTarget || element.contains(clickTarget);
    }
}
