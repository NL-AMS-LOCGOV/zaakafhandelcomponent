import {Component, Input, OnInit} from '@angular/core';
import {combineLatest, distinctUntilChanged, mapTo, merge, Observable, timer} from 'rxjs';
import {startWith, takeUntil} from 'rxjs/operators';
import {NgxSkeletonLoaderConfigTheme} from 'ngx-skeleton-loader';
import {SkeletonLayout, SkeletonLoaderOptions} from './skeleton-loader-options';
import {loadingFadeIn} from '../animations/animations';

@Component({
    selector: 'zac-skeleton-loader',
    templateUrl: './skeleton-loader.component.html',
    styleUrls: ['./skeleton-loader.component.less'],
    animations: [loadingFadeIn]
})
export class SkeletonLoaderComponent implements OnInit {

    @Input() options: SkeletonLoaderOptions = {};
    @Input() loading$: Observable<any>;

    private delay: number = 0;
    private duration: number = 400;

    skeletonLayout = SkeletonLayout;
    showLoader$: Observable<boolean>;

    cardHeaderTheme: NgxSkeletonLoaderConfigTheme = {
        'height.px': 28,
        'width.%': 35,
        margin: '0 16px 12px'
    };

    labelTheme: NgxSkeletonLoaderConfigTheme = {
        'height.px': 13,
        'width.%': 25,
        margin: 0
    };

    valueTheme: NgxSkeletonLoaderConfigTheme = {
        'height.px': 16,
        'width.%': 95,
        margin: '5px 0 0 0'
    };

    valueFullwidthTheme: NgxSkeletonLoaderConfigTheme = {
        'height.px': 80,
        'width.%': 100,
        margin: '5px 0 0 0'
    };

    tableTheme: NgxSkeletonLoaderConfigTheme = {
        'height.px': 26,
        'width.%': 100,
        margin: '11px 0'
    };

    constructor() { }

    ngOnInit(): void {
        this.showLoader$ = this.loading$.pipe(
            this.showLoader(this.delay, this.duration)
        );
    }

    getFields(fields: number): any[] {
        return new Array(fields).fill(1).map((x, i) => i);
    }

    showLoader = <T>(loaderDelay: number, loaderDuration: number) => {
        return (source: Observable<T>): Observable<boolean> => {
            return new Observable<boolean>(subscriber => {

                // delay loader and set loading to true until the source observable emits a value
                const loader$ = timer(loaderDelay).pipe(
                    mapTo(true),
                    takeUntil(source)
                );

                // set loader to false when both observables finish
                const result$ = combineLatest([
                    source,
                    timer(loaderDelay + loaderDuration)
                ]).pipe(
                    mapTo(false)
                );

                // merge the 2 observables and only emit a value when the loading changes from true to false
                return merge(loader$, result$)
                .pipe(
                    startWith(true), distinctUntilChanged())
                .subscribe({
                    next: value => {
                        subscriber.next(value);
                    },
                    error: err => subscriber.error(err),
                    complete: () => subscriber.complete()
                });
            });
        };
    };
}
