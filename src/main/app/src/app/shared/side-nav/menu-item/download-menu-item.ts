/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {MenuItem, MenuItemType} from './menu-item';

export class DownloadMenuItem extends MenuItem {

    readonly type: MenuItemType = MenuItemType.DOWNLOAD;

    constructor(readonly title: string,
                readonly url: string,
                readonly filename: string,
                readonly icon?: string) {
        super();
    }
}
