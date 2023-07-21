/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { MatDrawerMode } from "@angular/material/sidenav";
import { MenuMode } from "./side-nav.component";
import { SessionStorageUtil } from "../storage/session-storage.util";

export class SideNavUtil {
  static getMode(): MatDrawerMode {
    const menuMode = this.load();
    return menuMode === MenuMode.CLOSED || menuMode === MenuMode.OPEN
      ? "side"
      : "over";
  }

  static store(menuMode: MenuMode): void {
    SessionStorageUtil.setItem("menuMode", menuMode);
  }

  static load(): MenuMode {
    return SessionStorageUtil.getItem("menuMode", MenuMode.OPEN) as MenuMode;
  }
}
