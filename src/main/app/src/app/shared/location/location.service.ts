/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { FoutAfhandelingService } from "../../fout-afhandeling/fout-afhandeling.service";
import { catchError, mergeMap } from "rxjs/operators";
import { Observable } from "rxjs";

@Injectable({
  providedIn: "root",
})
export class LocationService {
  private readonly flSuggest: string = "type,weergavenaam,id";

  private readonly flLookup: string = "id,weergavenaam,centroide_ll,type";

  private readonly typeSuggest: string = "type:adres";

  constructor(
    private http: HttpClient,
    private foutAfhandelingService: FoutAfhandelingService,
  ) {}

  addressSuggest(
    zoekOpdracht: string,
  ): Observable<GeoDataResponse<SuggestResult>> {
    const url = `https://geodata.nationaalgeoregister.nl/locatieserver/v3/suggest?wt=json&q=${zoekOpdracht}&fl=${this.flSuggest}&fq=${this.typeSuggest}&rows=5`;
    return this.http
      .get<GeoDataResponse<SuggestResult>>(url, {
        headers: new HttpHeaders({ "Content-Type": "application/json" }),
      })
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  geolocationAddressSuggest(
    coordinates: number[],
  ): Observable<GeoDataResponse<SuggestResult>> {
    const url = `https://geodata.nationaalgeoregister.nl/locatieserver/revgeo?lon=${coordinates[0]}&lat=${coordinates[1]}&type=adres&rows=1&fl=${this.flSuggest}`;
    return this.http
      .get<GeoDataResponse<SuggestResult>>(url, {
        headers: new HttpHeaders({ "Content-Type": "application/json" }),
      })
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  addressLookup(objectId: string): Observable<GeoDataResponse<AddressResult>> {
    const url = `https://geodata.nationaalgeoregister.nl/locatieserver/v3/lookup?wt=json&id=${objectId}&fl=${this.flLookup}`;
    return this.http
      .get<GeoDataResponse<AddressResult>>(url, {
        headers: new HttpHeaders({ "Content-Type": "application/json" }),
      })
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  coordinateToAddress(
    coordinates: number[],
  ): Observable<GeoDataResponse<AddressResult>> {
    return this.geolocationAddressSuggest(coordinates).pipe(
      mergeMap((data) => this.addressLookup(data.response.docs[0].id)),
    );
  }
}

export interface SuggestResult {
  id: string;
  weergavenaam: string;
  type: string;
}

export interface AddressResult {
  id: string;
  weergavenaam: string;
  centroide_ll: string;
  type: string;
}

interface GeoDataResponse<TYPE> {
  response: {
    numFound: number;
    start: number;
    docs: [TYPE];
  };
}
