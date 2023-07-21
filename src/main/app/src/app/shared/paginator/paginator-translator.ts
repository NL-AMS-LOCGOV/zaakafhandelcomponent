/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { MatPaginatorIntl } from "@angular/material/paginator";
import { TranslateService } from "@ngx-translate/core";

export class PaginatorTranslator {
  constructor(private translateService: TranslateService) {}

  getTranslatedPaginator() {
    const paginator = new MatPaginatorIntl();

    paginator.itemsPerPageLabel =
      this.translateService.instant("msg.pagina.items");
    paginator.nextPageLabel = this.translateService.instant(
      "actie.pagina.volgende",
    );
    paginator.previousPageLabel = this.translateService.instant(
      "actie.pagina.vorige",
    );
    paginator.getRangeLabel = this.translatedRangeLabel;

    return paginator;
  }

  private translatedRangeLabel = (
    page: number,
    pageSize: number,
    length: number,
  ) => {
    if (length == 0 || pageSize == 0) {
      return `0 ${this.translateService.instant("msg.pagina")} ${length}`;
    }
    length = Math.max(length, 0);
    const startIndex = page * pageSize;
    const endIndex =
      startIndex < length
        ? Math.min(startIndex + pageSize, length)
        : startIndex + pageSize;
    return `${startIndex + 1} - ${endIndex} ${this.translateService.instant(
      "msg.pagina",
    )} ${length}`;
  };
}
