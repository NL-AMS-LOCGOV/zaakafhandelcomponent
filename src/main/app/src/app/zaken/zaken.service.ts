/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Injectable } from "@angular/core";
import { Zaak } from "./model/zaak";
import { Observable } from "rxjs";
import { HttpClient, HttpParams } from "@angular/common/http";
import { catchError } from "rxjs/operators";
import { TableRequest } from "../shared/dynamic-table/datasource/table-request";
import { Zaaktype } from "./model/zaaktype";
import { FoutAfhandelingService } from "../fout-afhandeling/fout-afhandeling.service";
import { ZaakOverzicht } from "./model/zaak-overzicht";
import { ZaakToekennenGegevens } from "./model/zaak-toekennen-gegevens";
import { User } from "../identity/model/user";
import { ZakenVerdeelGegevens } from "./model/zaken-verdeel-gegevens";
import { HistorieRegel } from "../shared/historie/model/historie-regel";
import { Group } from "../identity/model/group";
import { ZaakEditMetRedenGegevens } from "./model/zaak-edit-met-reden-gegevens";
import { ZaakBetrokkeneGegevens } from "./model/zaak-betrokkene-gegevens";
import { ZaakbeeindigReden } from "../admin/model/zaakbeeindig-reden";
import { ZaakAfbrekenGegevens } from "./model/zaak-afbreken-gegevens";
import { DocumentOntkoppelGegevens } from "./model/document-ontkoppel-gegevens";
import { ZaakOpschortGegevens } from "./model/zaak-opschort-gegevens";
import { ZaakOpschorting } from "./model/zaak-opschorting";
import { ZaakVerlengGegevens } from "./model/zaak-verleng-gegevens";
import { ZaakZoekObject } from "../zoeken/model/zaken/zaak-zoek-object";
import { ZaakHeropenenGegevens } from "./model/zaak-heropenen-gegevens";
import { ZaakAfsluitenGegevens } from "./model/zaak-afsluiten-gegevens";
import { ZaakKoppelGegevens } from "./model/zaak-koppel-gegevens";
import { ZaakOntkoppelGegevens } from "./model/zaak-ontkoppel-gegevens";
import { Roltype } from "../klanten/model/klanten/roltype";
import { ZaakBetrokkene } from "./model/zaak-betrokkene";
import { Klant } from "../klanten/model/klanten/klant";
import { BesluitVastleggenGegevens } from "./model/besluit-vastleggen-gegevens";
import { Besluit } from "./model/besluit";
import { Resultaattype } from "./model/resultaattype";
import { Besluittype } from "./model/besluittype";
import { EnkelvoudigInformatieobject } from "../informatie-objecten/model/enkelvoudig-informatieobject";
import { BesluitWijzigenGegevens } from "./model/besluit-wijzigen-gegevens";
import { ZaakAfzender } from "../admin/model/zaakafzender";
import { ZaakAanmaakGegevens } from "./model/zaak-aanmaak-gegevens";
import { BesluitIntrekkenGegevens } from "./model/besluit-intrekken-gegevens";
import { ZaakLocatieGegevens } from "./model/zaak-locatie-gegevens";
import { Geometry } from "./model/geometry";

@Injectable({
  providedIn: "root",
})
export class ZakenService {
  constructor(
    private http: HttpClient,
    private foutAfhandelingService: FoutAfhandelingService,
  ) {}

  private basepath = "/rest/zaken";

  private static getTableParams(request: TableRequest): HttpParams {
    return new HttpParams().set("tableRequest", JSON.stringify(request));
  }

  readZaak(uuid: string): Observable<Zaak> {
    return this.http
      .get<Zaak>(`${this.basepath}/zaak/${uuid}`)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  readZaakByID(id: string): Observable<Zaak> {
    return this.http
      .get<Zaak>(`${this.basepath}/zaak/id/${id}`)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  createZaak(zaakAanmaakgegevens: ZaakAanmaakGegevens): Observable<Zaak> {
    return this.http
      .post<Zaak>(`${this.basepath}/zaak`, zaakAanmaakgegevens)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  updateZaak(uuid: string, zaak: Zaak, reden?: string): Observable<Zaak> {
    return this.http
      .patch<Zaak>(
        `${this.basepath}/zaak/${uuid}`,
        new ZaakEditMetRedenGegevens(zaak, reden),
      )
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  readOpschortingZaak(uuid: string): Observable<ZaakOpschorting> {
    return this.http
      .get<ZaakOpschorting>(`${this.basepath}/zaak/${uuid}/opschorting`)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  opschortenZaak(
    uuid: string,
    zaakOpschortGegevens: ZaakOpschortGegevens,
  ): Observable<Zaak> {
    return this.http
      .patch<Zaak>(
        `${this.basepath}/zaak/${uuid}/opschorting`,
        zaakOpschortGegevens,
      )
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  verlengenZaak(
    zaakUUID: string,
    zaakVerlengGegevens: ZaakVerlengGegevens,
  ): Observable<Zaak> {
    return this.http
      .patch<Zaak>(
        `${this.basepath}/zaak/${zaakUUID}/verlenging`,
        zaakVerlengGegevens,
      )
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  listZaakWaarschuwingen(): Observable<ZaakOverzicht[]> {
    return this.http
      .get<ZaakOverzicht[]>(`${this.basepath}/waarschuwing`)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  listZaaktypes(): Observable<Zaaktype[]> {
    return this.http
      .get<Zaaktype[]>(`${this.basepath}/zaaktypes`)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  updateZaakdata(zaak: Zaak): Observable<Zaak> {
    return this.http
      .put<Zaak>(`${this.basepath}/zaakdata`, zaak)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  toekennen(zaak: Zaak, reden?: string): Observable<Zaak> {
    const toekennenGegevens: ZaakToekennenGegevens =
      new ZaakToekennenGegevens();
    toekennenGegevens.zaakUUID = zaak.uuid;
    toekennenGegevens.behandelaarGebruikersnaam = zaak.behandelaar?.id;
    toekennenGegevens.groepId = zaak.groep?.id;
    toekennenGegevens.reden = reden;

    return this.http
      .patch<Zaak>(`${this.basepath}/toekennen`, toekennenGegevens)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  verdelenVanuitLijst(
    uuids: string[],
    groep?: Group,
    medewerker?: User,
    reden?: string,
  ): Observable<void> {
    const verdeelGegevens: ZakenVerdeelGegevens = new ZakenVerdeelGegevens();
    verdeelGegevens.uuids = uuids;
    verdeelGegevens.groepId = groep?.id;
    verdeelGegevens.behandelaarGebruikersnaam = medewerker?.id;
    verdeelGegevens.reden = reden;

    return this.http
      .put<void>(`${this.basepath}/lijst/verdelen`, verdeelGegevens)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  vrijgevenVanuitLijst(uuids: string[], reden?: string): Observable<void> {
    const verdeelGegevens: ZakenVerdeelGegevens = new ZakenVerdeelGegevens();
    verdeelGegevens.uuids = uuids;
    verdeelGegevens.reden = reden;

    return this.http
      .put<void>(`${this.basepath}/lijst/vrijgeven`, verdeelGegevens)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  toekennenAanIngelogdeMedewerker(
    zaak: Zaak,
    reden?: string,
  ): Observable<Zaak> {
    const toekennenGegevens: ZaakToekennenGegevens =
      new ZaakToekennenGegevens();
    toekennenGegevens.zaakUUID = zaak.uuid;
    toekennenGegevens.reden = reden;

    return this.http
      .put<Zaak>(`${this.basepath}/toekennen/mij`, toekennenGegevens)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  updateInitiator(zaak: Zaak, initiator: Klant): Observable<Zaak> {
    const gegevens = new ZaakBetrokkeneGegevens();
    gegevens.zaakUUID = zaak.uuid;
    gegevens.betrokkeneIdentificatieType = initiator.identificatieType;
    gegevens.betrokkeneIdentificatie = initiator.identificatie;
    return this.http
      .put<Zaak>(`${this.basepath}/initiator`, gegevens)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  deleteInitiator(zaak: Zaak, reden: string): Observable<Zaak> {
    return this.http
      .delete<Zaak>(`${this.basepath}/${zaak.uuid}/initiator`, {
        body: { reden },
      })
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  createBetrokkene(
    zaak: Zaak,
    betrokkene: Klant,
    roltype: Roltype,
    roltoelichting: string,
  ): Observable<Zaak> {
    const gegevens = new ZaakBetrokkeneGegevens();
    gegevens.zaakUUID = zaak.uuid;
    gegevens.roltypeUUID = roltype.uuid;
    gegevens.roltoelichting = roltoelichting;
    gegevens.betrokkeneIdentificatieType = betrokkene.identificatieType;
    gegevens.betrokkeneIdentificatie = betrokkene.identificatie;
    return this.http
      .post<Zaak>(`${this.basepath}/betrokkene`, gegevens)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  deleteBetrokkene(rolUUID: string, reden: string): Observable<Zaak> {
    return this.http
      .delete<Zaak>(`${this.basepath}/betrokkene/${rolUUID}`, {
        body: { reden },
      })
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  updateZaakLocatie(
    uuid: string,
    locatie: Geometry,
    reden: string,
  ): Observable<Zaak> {
    return this.http
      .patch<Zaak>(
        `${this.basepath}/${uuid}/zaaklocatie`,
        new ZaakLocatieGegevens(locatie, reden),
      )
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  ontkoppelInformatieObject(
    zaakUUID: string,
    documentUUID: string,
    reden: string,
  ): Observable<void> {
    const gegevens = new DocumentOntkoppelGegevens(
      zaakUUID,
      documentUUID,
      reden,
    );
    return this.http
      .put<void>(`${this.basepath}/zaakinformatieobjecten/ontkoppel`, gegevens)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  toekennenAanIngelogdeMedewerkerVanuitLijst(
    zaak: ZaakZoekObject,
    reden?: string,
  ): Observable<ZaakOverzicht> {
    const toekennenGegevens: ZaakToekennenGegevens =
      new ZaakToekennenGegevens();
    toekennenGegevens.zaakUUID = zaak.id;
    toekennenGegevens.reden = reden;

    return this.http
      .put<ZaakOverzicht>(
        `${this.basepath}/lijst/toekennen/mij`,
        toekennenGegevens,
      )
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  listHistorieVoorZaak(uuid: string): Observable<HistorieRegel[]> {
    return this.http
      .get<HistorieRegel[]>(`${this.basepath}/zaak/${uuid}/historie`)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  listBetrokkenenVoorZaak(uuid: string): Observable<ZaakBetrokkene[]> {
    return this.http
      .get<ZaakBetrokkene[]>(`${this.basepath}/zaak/${uuid}/betrokkene`)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  listAfzendersVoorZaak(zaakUuid: string): Observable<ZaakAfzender[]> {
    return this.http
      .get<ZaakAfzender[]>(`${this.basepath}/zaak/${zaakUuid}/afzender`)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  readDefaultAfzenderVoorZaak(zaakUuid: string): Observable<ZaakAfzender> {
    return this.http
      .get<ZaakAfzender>(`${this.basepath}/zaak/${zaakUuid}/afzender/default`)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  afbreken(uuid: string, beeindigReden: ZaakbeeindigReden): Observable<void> {
    return this.http
      .patch<void>(
        `${this.basepath}/zaak/${uuid}/afbreken`,
        new ZaakAfbrekenGegevens(beeindigReden.id),
      )
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  heropenen(uuid: string, heropenReden: string): Observable<void> {
    return this.http
      .patch<void>(
        `${this.basepath}/zaak/${uuid}/heropenen`,
        new ZaakHeropenenGegevens(heropenReden),
      )
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  afsluiten(
    uuid: string,
    afsluitenReden: string,
    resultaattypeUuid: string,
  ): Observable<void> {
    return this.http
      .patch<void>(
        `${this.basepath}/zaak/${uuid}/afsluiten`,
        new ZaakAfsluitenGegevens(afsluitenReden, resultaattypeUuid),
      )
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  createBesluit(
    besluitVestleggenGegevens: BesluitVastleggenGegevens,
  ): Observable<Besluit> {
    return this.http
      .post<BesluitVastleggenGegevens>(
        `${this.basepath}/besluit`,
        besluitVestleggenGegevens,
      )
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  updateBesluit(
    besluitWijzigenGegevens: BesluitWijzigenGegevens,
  ): Observable<Besluit> {
    return this.http
      .put<BesluitWijzigenGegevens>(
        `${this.basepath}/besluit`,
        besluitWijzigenGegevens,
      )
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  intrekkenBesluit(
    besluitIntrekkenGegevens: BesluitIntrekkenGegevens,
  ): Observable<Besluit> {
    return this.http
      .put<BesluitIntrekkenGegevens>(
        `${this.basepath}/besluit/intrekken`,
        besluitIntrekkenGegevens,
      )
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  listBesluittypes(zaaktypeUuid: string): Observable<Besluittype[]> {
    return this.http
      .get<Besluittype[]>(`${this.basepath}/besluittypes/${zaaktypeUuid}`)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  listResultaattypes(zaaktypeUuid: string): Observable<Resultaattype[]> {
    return this.http
      .get<Resultaattype[]>(`${this.basepath}/resultaattypes/${zaaktypeUuid}`)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  listCommunicatiekanalen(
    inclusiefEFormulier?: boolean,
  ): Observable<{ naam: string; uuid: string }[]> {
    return this.http
      .get<string[]>(
        `${this.basepath}/communicatiekanalen/${inclusiefEFormulier}`,
      )
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  koppelZaak(zaakKoppelGegevens: ZaakKoppelGegevens): Observable<void> {
    return this.http
      .patch<void>(`${this.basepath}/zaak/koppel`, zaakKoppelGegevens)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  ontkoppelZaak(
    zaakOntkoppelGegevens: ZaakOntkoppelGegevens,
  ): Observable<void> {
    return this.http
      .patch<void>(`${this.basepath}/zaak/ontkoppel`, zaakOntkoppelGegevens)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  listBesluitInformatieobjecten(
    besluitUuid: string,
  ): Observable<EnkelvoudigInformatieobject[]> {
    return this.http
      .get<EnkelvoudigInformatieobject[]>(
        `${this.basepath}/listBesluitInformatieobjecten/${besluitUuid}`,
      )
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  listBesluitenForZaak(zaakUuid: string): Observable<Besluit[]> {
    return this.http
      .get<Besluit[]>(`${this.basepath}/besluit/zaakUuid/${zaakUuid}`)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  listBesluitHistorie(uuid: string): Observable<HistorieRegel[]> {
    return this.http
      .get<HistorieRegel[]>(`${this.basepath}/besluit/${uuid}/historie`)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  listProcesVariabelen(): Observable<string[]> {
    return this.http
      .get<string[]>(`${this.basepath}/procesvariabelen`)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }
}
