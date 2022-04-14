/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Pipe, PipeTransform} from '@angular/core';
import {LocationService} from '../location/location.service';
import {LocationUtil} from '../location/location-util';
import {Observable, of} from 'rxjs';
import {map} from 'rxjs/operators';

@Pipe({
    name: 'location'
})
export class LocationPipe implements PipeTransform {
    constructor(private locationService: LocationService) {}

    transform(value: string): Observable<string> {
        if (value) {
            return this.locationService.coordinatesToAddress(LocationUtil.centroide_llToArray(value)).pipe(
                map(address => address.response.docs[0].weergavenaam)
            );
        } else {
            return of(value);
        }
    }
}
