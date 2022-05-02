import {Component, Input, OnInit} from '@angular/core';
import {combineLatest, distinctUntilChanged, mapTo, merge, Observable, timer} from 'rxjs';
import {startWith, takeUntil} from 'rxjs/operators';
import {NgxSkeletonLoaderConfigTheme} from 'ngx-skeleton-loader';
import {SkeletonLoaderOptions} from './skeleton-loader-options';
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
            this.showLoaderTime(this.delay, this.duration)
        );
    }

    getFields(fields: number): any[] {
        return new Array(fields).fill(1).map((x, i) => i);
    }

    showLoaderTime = <T>(loaderDelay: number, loaderDuration: number) => {
        return (source: Observable<T>): Observable<boolean> => {
            return new Observable<boolean>(subscriber => {
                const loader$ = timer(loaderDelay).pipe(
                    mapTo(true),
                    takeUntil(source)
                );

                const result$ = combineLatest([
                    source,
                    timer(loaderDelay + loaderDuration)
                ]).pipe(
                    mapTo(false));

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
