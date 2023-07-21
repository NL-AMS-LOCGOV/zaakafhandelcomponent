/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {
  AfterViewInit,
  Component,
  OnDestroy,
  OnInit,
  ViewChild,
} from "@angular/core";
import { FormComponent } from "../../model/form-component";
import { GoogleMapsFormField } from "./google-maps-form-field";
import { GoogleMap, MapInfoWindow } from "@angular/google-maps";
import { GoogleMapsService } from "../../service/google-maps.service";
import { Subscription } from "rxjs";
import { TranslateService } from "@ngx-translate/core";
import LatLng = google.maps.LatLng;

@Component({
  templateUrl: "./google-maps.component.html",
  styleUrls: ["./google-maps.component.less"],
})
export class GoogleMapsComponent
  extends FormComponent
  implements OnInit, AfterViewInit, OnDestroy
{
  data: GoogleMapsFormField;
  _map: GoogleMap;

  @ViewChild(GoogleMap, { static: false }) set map(map: GoogleMap) {
    this._map = map;
  }

  @ViewChild(MapInfoWindow, { static: false }) infoWindow: MapInfoWindow;
  @ViewChild("mapAutoComplete", { static: false }) mapAutoComplete;

  mapCenter: google.maps.LatLngLiteral;
  options: google.maps.MapOptions;
  currentLocation: google.maps.LatLngLiteral;

  apiLoaded: boolean;

  private subscription$: Subscription;

  constructor(
    public translate: TranslateService,
    private googleMapsService: GoogleMapsService,
  ) {
    super();
  }

  ngOnInit(): void {
    this.googleMapsService.load();
  }

  ngAfterViewInit(): void {
    this.subscription$ = this.googleMapsService.loaded.subscribe((loaded) => {
      if (loaded) {
        this.apiLoaded = true;
        this.options = {
          center: { lat: 52.13303, lng: 5.2905505 },
          zoom: 7,
        };

        const locationButton = document.createElement("button");
        locationButton.setAttribute("type", "button");
        locationButton.textContent = "Gebruik huidige locatie";
        locationButton.classList.add("locatie-button");

        this._map.googleMap.controls[
          google.maps.ControlPosition.TOP_CENTER
        ].push(locationButton);

        locationButton.addEventListener("click", () => {
          this.googleMapsService
            .loadCurrentPosition()
            .then((latLng) => this.addMarker(latLng))
            .catch((reason) => {
              this.infoWindow.infoWindow.setContent(reason);
              this.infoWindow.open();
            });
        });
        const autocomplete = new google.maps.places.Autocomplete(
          this.mapAutoComplete.nativeElement,
        );

        autocomplete.setFields([
          "address_components",
          "geometry",
          "icon",
          "name",
        ]);
        autocomplete.bindTo("bounds", this._map.googleMap);

        autocomplete.addListener("place_changed", () => {
          const place: google.maps.places.PlaceResult = autocomplete.getPlace();
          this.addMarker(place.geometry.location);
        });
      }
    });
  }

  ngOnDestroy(): void {
    if (this.subscription$) {
      this.subscription$.unsubscribe();
    }
  }

  onPlaceMarker(event: google.maps.MapMouseEvent) {
    this.addMarker(event.latLng);
  }

  addMarker(latLng: LatLng): void {
    this.currentLocation = latLng.toJSON();
    this._map.center = latLng.toJSON();
    this._map.zoom = 14;
    this.getLocation();
  }

  getLocation() {
    new google.maps.Geocoder().geocode(
      { location: this.currentLocation },
      (results, status) => {
        if (status.toString() === "OK" && results[0]) {
          this.data.formControl.setValue(
            `${results[0].geometry.location}|${results[0].formatted_address}`,
          );
        } else {
          this.data.formControl.setValue(null);
        }
      },
    );
  }
}
