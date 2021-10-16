/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {MenuItem} from './menu-item/menu-item';
import {catchError, finalize} from 'rxjs/operators';
import {HttpClient} from '@angular/common/http';
import {FoutAfhandelingService} from '../../fout-afhandeling/fout-afhandeling.service';
import {ButtonMenuItem} from './menu-item/button-menu-item';
import {DownloadMenuItem} from './menu-item/download-menu-item';
import {LinkMenuTitem} from './menu-item/link-menu-titem';
import {rotate180, sideNavToggle} from '../animations/animations';
import {Store} from '@ngrx/store';
import {State} from '../../state/app.state';
import {isFixedMenu} from '../state/side-nav.reducer';
import {toggleFixedSideNav} from '../state/side-nav.actions';
import {UtilService} from '../../core/service/util.service';

@Component({
    selector: 'zac-side-nav',
    templateUrl: './side-nav.component.html',
    styleUrls: ['./side-nav.component.less'],
    animations: [
        rotate180, sideNavToggle
    ]
})
export class SideNavComponent implements OnInit, OnChanges {

    @Input() menu: MenuItem[];
    @Input() collapsed: boolean;

    private fixedMenu: boolean;
    public buttonState: string = 'default';

    constructor(private store: Store<State>, private http: HttpClient, private foutAfhandelingService: FoutAfhandelingService, public utilService: UtilService) {
    }

    ngOnInit(): void {
        this.store.select(isFixedMenu).subscribe(fixedMenu => {
            this.fixedMenu = fixedMenu;
            this.buttonState = fixedMenu ? 'default' : 'rotated';
            this.collapsed = !fixedMenu;
        });
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes && changes.collapsed) {
            this.collapsed = changes.collapsed.currentValue;
        }
    }

    toggleMenu(): void {
        this.store.dispatch(toggleFixedSideNav());
    }

    download(downloadMenuItem: DownloadMenuItem): void {
        if (downloadMenuItem.disabled) {
            return;
        }
        downloadMenuItem.disabled = true;
        this.http.get(downloadMenuItem.url, {responseType: 'blob' as 'json'}).pipe(
            catchError(this.foutAfhandelingService.log('Er is een fout opgetreden bij het downloaden van het bestand')),
            finalize(() => {
                downloadMenuItem.disabled = false;
            })
        ).subscribe(
            (response: any) => {
                let dataType = response.type;
                let binaryData = [];
                binaryData.push(response);
                let downloadLink = document.createElement('a');
                downloadLink.href = window.URL.createObjectURL(new Blob(binaryData, {type: dataType}));
                if (downloadMenuItem.filename) {
                    downloadLink.setAttribute('download', downloadMenuItem.filename);
                }
                document.body.appendChild(downloadLink);
                downloadLink.click();
                downloadLink.parentNode.removeChild(downloadLink);
            }
        );
    }

    onClick(buttonMenuItem: ButtonMenuItem): void {
        buttonMenuItem.fn();
    }

    asButtonMenuItem(menuItem: MenuItem): ButtonMenuItem {
        return menuItem as ButtonMenuItem;
    }

    asDownloadMenuItem(menuItem: MenuItem): DownloadMenuItem {
        return menuItem as DownloadMenuItem;
    }

    asLinkMenuItem(menuItem: MenuItem): LinkMenuTitem {
        return menuItem as LinkMenuTitem;
    }
}
