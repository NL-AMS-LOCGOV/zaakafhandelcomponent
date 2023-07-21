/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Injectable } from "@angular/core";
import { EnkelvoudigInformatieobject } from "./model/enkelvoudig-informatieobject";
import { DocumentVerplaatsGegevens } from "./model/document-verplaats-gegevens";
import { SessionStorageUtil } from "../shared/storage/session-storage.util";
import { Subject } from "rxjs";
import {
  ActionBarAction,
  ActionEntityType,
} from "../core/actionbar/model/action-bar-action";
import { ActionIcon } from "../shared/edit/action-icon";
import { UtilService } from "../core/service/util.service";
import { Router } from "@angular/router";
import { InformatieObjectenService } from "./informatie-objecten.service";
import { ViewResourceUtil } from "../locatie/view-resource.util";

@Injectable({
  providedIn: "root",
})
export class InformatieObjectVerplaatsService {
  constructor(
    private utilService: UtilService,
    private router: Router,
    private informatieObjectService: InformatieObjectenService,
  ) {}

  addTeVerplaatsenDocument(
    informatieobject: EnkelvoudigInformatieobject,
    bron: string,
  ): void {
    if (!this.isReedsTeVerplaatsen(informatieobject.uuid)) {
      this._verplaatsenDocument(
        new DocumentVerplaatsGegevens(
          informatieobject.uuid,
          informatieobject.titel,
          informatieobject.informatieobjectTypeUUID,
          bron,
        ),
      );
    }
  }

  isReedsTeVerplaatsen(informatieobjectUUID: string): boolean {
    const teVerplaatsenDocumenten = SessionStorageUtil.getItem(
      "teVerplaatsenDocumenten",
      [],
    ) as DocumentVerplaatsGegevens[];
    return (
      teVerplaatsenDocumenten.find(
        (dvg) => dvg.documentUUID === informatieobjectUUID,
      ) !== undefined
    );
  }

  appInit() {
    const documenten = SessionStorageUtil.getItem(
      "teVerplaatsenDocumenten",
      [],
    ) as DocumentVerplaatsGegevens[];
    documenten.forEach((document) => {
      this._verplaatsenDocument(document, true);
    });
  }

  private _verplaatsenDocument(
    document: DocumentVerplaatsGegevens,
    onInit?: boolean,
  ) {
    const dismiss: Subject<void> = new Subject<void>();
    dismiss.asObservable().subscribe(() => {
      this.deleteTeVerplaatsenDocument(document);
    });
    const verplaatsAction = new Subject<string>();
    verplaatsAction.asObservable().subscribe((url) => {
      const nieuweZaakID = url.split("/").pop();
      this.informatieObjectService
        .postVerplaatsDocument(document, nieuweZaakID)
        .subscribe(() =>
          this.utilService.openSnackbar("msg.document.verplaatsen.uitgevoerd"),
        );
      this.deleteTeVerplaatsenDocument(document);
    });
    const teVerplaatsenDocumenten = SessionStorageUtil.getItem(
      "teVerplaatsenDocumenten",
      [],
    ) as DocumentVerplaatsGegevens[];
    teVerplaatsenDocumenten.push(document);
    if (!onInit) {
      SessionStorageUtil.setItem(
        "teVerplaatsenDocumenten",
        teVerplaatsenDocumenten,
      );
    }
    const action: ActionBarAction = new ActionBarAction(
      document.documentTitel,
      ActionEntityType.DOCUMENT,
      document.bron,
      new ActionIcon(
        "content_paste_go",
        "actie.document.verplaatsen",
        verplaatsAction,
      ),
      dismiss,
      () => this.isDisabled(document),
    );
    this.utilService.addAction(action);
  }

  private deleteTeVerplaatsenDocument(
    documentVerplaatsGegevens: DocumentVerplaatsGegevens,
  ) {
    const documenten = SessionStorageUtil.getItem(
      "teVerplaatsenDocumenten",
      [],
    ) as DocumentVerplaatsGegevens[];
    SessionStorageUtil.setItem(
      "teVerplaatsenDocumenten",
      documenten.filter(
        (document) =>
          document.documentUUID !== documentVerplaatsGegevens.documentUUID,
      ),
    );
  }

  /**
   * @return null als toegestaan, string met reden indien disabled;
   */
  private isDisabled(
    verplaatsGegevens: DocumentVerplaatsGegevens,
  ): string | null {
    const actieveZaak = ViewResourceUtil.actieveZaak;
    if (actieveZaak && actieveZaak.identificatie !== verplaatsGegevens.bron) {
      if (
        !actieveZaak.zaaktype.informatieobjecttypes.includes(
          verplaatsGegevens.documentTypeUUID,
        )
      ) {
        return "actie.document.verplaatsen.disabled.documenttype";
      }
    } else {
      return "actie.document.verplaatsen.disabled.zaak";
    }
    return null;
  }
}
