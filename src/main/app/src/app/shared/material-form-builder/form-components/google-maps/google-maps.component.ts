/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, OnInit, OnDestroy, ViewChild} from '@angular/core';
import {IFormComponent} from '../../model/iform-component';
import {GoogleMapsFormField} from './google-maps-form-field';
import {GoogleMap, MapInfoWindow} from '@angular/google-maps';
import {GoogleMapsService} from '../../service/google-maps.service';
import LatLng = google.maps.LatLng;
import {Subscription} from 'rxjs';

@Component({
    selector: 'mfb-google-maps',
    templateUrl: './google-maps.component.html',
    styleUrls: ['./google-maps.component.less']
})
export class GoogleMapsComponent implements OnInit, AfterViewInit, OnDestroy, IFormComponent {

    data: GoogleMapsFormField;
    _map: GoogleMap;

    @ViewChild(GoogleMap, {static: false}) set map(map: GoogleMap) {
        this._map = map;
    } ;

    @ViewChild(MapInfoWindow, {static: false}) infoWindow: MapInfoWindow;
    @ViewChild('mapAutoComplete', {static: false}) mapAutoComplete;

    mapCenter: google.maps.LatLngLiteral;
    options: google.maps.MapOptions;
    currentLocation: google.maps.LatLngLiteral;

    apiLoaded: boolean;

    private subscription$: Subscription;

    constructor(private googleMapsService: GoogleMapsService) {

    }

    ngOnInit(): void {
        this.googleMapsService.load();
    }

    ngAfterViewInit(): void {

        this.subscription$ = this.googleMapsService.loaded.subscribe(loaded => {
            if (loaded) {
                this.apiLoaded = true;
                //Timeout omdat de kaart anders undefined is...
                setTimeout(() => {
                    this.options = {
                        center: {lat: 52.13303, lng: 5.2905505},
                        zoom: 7
                    };

                    const locationButton = document.createElement('button');
                    locationButton.setAttribute('type', 'button');
                    locationButton.textContent = 'Gebruik huidige locatie';
                    locationButton.classList.add('locatie-button');

                    this._map.googleMap.controls[google.maps.ControlPosition.TOP_CENTER].push(locationButton);

                    locationButton.addEventListener('click', () => {
                        this.googleMapsService.loadCurrentPosition()
                            .then(latLng => this.addMarker(latLng))
                            .catch(reason => {
                                this.infoWindow.infoWindow.setContent(reason);
                                this.infoWindow.open();
                            });
                    });
                    const autocomplete = new google.maps.places.Autocomplete(this.mapAutoComplete.nativeElement);

                    autocomplete.setFields(['address_components', 'geometry', 'icon', 'name']);
                    autocomplete.bindTo('bounds', this._map.googleMap);

                    autocomplete.addListener('place_changed', () => {
                        let place: google.maps.places.PlaceResult = autocomplete.getPlace();
                        this.addMarker(place.geometry.location);
                    });

                }, 500);
            }
        });
    }

    ngOnDestroy(): void {
        if(this.subscription$){
            this.subscription$.unsubscribe();
        }
    }

    onPlaceMarker(event: google.maps.MouseEvent) {
        this.addMarker(event.latLng);
    }

    addMarker(latLng: LatLng): void {
        this.currentLocation = latLng.toJSON();
        this._map.center = latLng.toJSON();
        this._map.zoom = 14;
        this.getLocation();
    }

    getLocation() {
        new google.maps.Geocoder().geocode({location: this.currentLocation}, (results, status) => {
            if (status.toString() === 'OK' && results[0]) {
                this.data.formControl.setValue(`${results[0].geometry.location}|${results[0].formatted_address}`);
            } else {
                this.data.formControl.setValue(null);
            }
        });
    }
}
