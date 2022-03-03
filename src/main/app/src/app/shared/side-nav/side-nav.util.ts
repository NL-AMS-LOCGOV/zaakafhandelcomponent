/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {MatDrawerMode} from '@angular/material/sidenav';
import {MenuMode} from './side-nav.component';

export class SideNavUtil {

    static getMode(): MatDrawerMode {
        const menuMode = this.load();
        return menuMode === MenuMode.CLOSED || menuMode === MenuMode.OPEN ? 'side' : 'over';
    }

    static store(menuMode): void {
        sessionStorage.setItem('menuMode', menuMode);
    }

    static load(): MenuMode {
        const menuMode = sessionStorage.getItem('menuMode');
        return menuMode ? menuMode.replace(/"/g, '') as MenuMode : MenuMode.OPEN;
    }
}
