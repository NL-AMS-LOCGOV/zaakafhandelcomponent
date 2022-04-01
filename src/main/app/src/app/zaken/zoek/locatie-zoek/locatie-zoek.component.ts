/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, ElementRef, EventEmitter, OnDestroy, OnInit, Output, ViewChild} from '@angular/core';
import * as ol from 'ol/index.js';
import * as layer from 'ol/layer.js';
import * as proj from 'ol/proj.js';
import * as geom from 'ol/geom.js';
import * as source from 'ol/source.js';
import WMTSTileGrid from 'ol/tilegrid/WMTS.js';
import * as extent from 'ol/extent.js';
import * as control from 'ol/control.js';
import * as style from 'ol/style.js';
import {Coordinate} from 'ol/coordinate.js';
import * as interaction from 'ol/interaction.js';
import {LocationService} from '../../../shared/location/location.service';
import {Subject} from 'rxjs';
import {takeUntil} from 'rxjs/operators';
import {FormControl} from '@angular/forms';
import {MatAutocompleteSelectedEvent} from '@angular/material/autocomplete';

@Component({
    selector: 'zac-locatie-zoek',
    templateUrl: './locatie-zoek.component.html',
    styleUrls: ['./locatie-zoek.component.less']
})
export class LocatieZoekComponent implements OnInit, AfterViewInit, OnDestroy {

    @Output() locatie = new EventEmitter<{ naam: string, coordinates: Coordinate }>();
    @ViewChild('openLayersMap', {static: true}) openLayersMapRef: ElementRef;
    selectedAddress: any;
    results: any[];
    searchControl: FormControl = new FormControl();

    private unsubscribe$: Subject<void> = new Subject<void>();

    private map: ol.Map;
    private view: ol.View;
    private locationSource: source.Vector;
    private WGS84: string = 'WGS84';
    private EPSG3857 = 'EPSG:3857';

    private layers: any[];

    private defaultStyle: style.Style = new style.Style({
        fill: new style.Fill({
            color: 'rgba(255, 255, 255, 0.5)'
        }),
        stroke: new style.Stroke({
            color: '#ff0000',
            width: 2
        })
    });

    private pointStyle: style.Style = new style.Style({
        text: new style.Text({
            text: 'place',
            font: '900 30px "Material Icons"',
            fill: new style.Fill({
                color: '#ff0000'
            }),
            offsetY: -15
        })
    });

    constructor(private locationService: LocationService) {

    }

    ngOnInit(): void {
        const projection = proj.get(this.EPSG3857);
        const projectionExtent = projection.getExtent();
        const size = extent.getWidth(projectionExtent) / 256;
        const resolutions = new Array(20);
        const matrixIds = new Array(20);
        for (let z = 0; z < 20; ++z) {
            resolutions[z] = size / Math.pow(2, z);
            matrixIds[z] = ('0' + z).slice(-2);
        }

        const brtsource = new source.WMTS({
            projection: projection,
            layer: 'standaard',
            format: 'image/png',
            url: 'https://service.pdok.nl/brt/achtergrondkaart/wmts/v2_0',
            matrixSet: this.EPSG3857,
            style: '',
            tileGrid: new WMTSTileGrid({
                origin: extent.getTopLeft(projectionExtent),
                resolutions: resolutions,
                matrixIds: matrixIds
            }),
            attributions: ['© OpenLayers en PDOK']
        });

        const brtLayer = new layer.Tile({
            source: brtsource
        });

        this.locationSource = new source.Vector();
        const locationLayer = new layer.Vector({
            source: this.locationSource,
            style: this.defaultStyle
        });

        this.layers = [brtLayer];
        this.layers.push(locationLayer);

        this.view = new ol.View({
            projection: proj.get(this.EPSG3857),
            center: [631711.827985, 6856275.890632],
            constrainResolution: true,
            zoom: 8
        });

        const interactions = interaction.defaults({
            onFocusOnly: true
        });
        const controls = control.defaults();

        this.map = new ol.Map({
            interactions: interactions,
            controls: controls,
            view: this.view,
            layers: this.layers
        });

        const modify = new interaction.Modify({source: this.locationSource});
        this.map.addInteraction(modify);

        this.searchControl.valueChanges.pipe(
            takeUntil(this.unsubscribe$)
        ).subscribe((value) => {
            this.searchAddress(value);
        });
    }

    ngAfterViewInit(): void {
        setTimeout(() => {
            this.map.setTarget(this.openLayersMapRef.nativeElement);
        }, 0);

        this.map.on('click', (event) => {
            const locationCoordinates: Array<number> = proj.transform(event.coordinate, 'EPSG:3857', 'EPSG:4326');
            this.locationService.geolocationToAddress(locationCoordinates).subscribe(data => {
                this.addressLookup(data.response.docs[0].id);
            });

        });

        this.map.on('click', () => {
            this.openLayersMapRef.nativeElement.focus();
        });

        this.map.on('pointerdrag', () => {
            this.openLayersMapRef.nativeElement.focus();
        });
    }

    ngOnDestroy(): void {
        this.unsubscribe$.next();
        this.unsubscribe$.complete();
    }

    private searchAddress(query): void {
        if (query) {
            this.locationService.addressSuggest(query).subscribe(data => {
                this.results = data.response.docs;
            });
        }
    }

    save(): void {
        if (this.selectedAddress) {
            const locationCoordinates = this.selectedAddress.centroide_ll.replace('POINT(', '').replace(')', '').split(' ');
            this.locatie.next({naam: this.selectedAddress.weergavenaam, coordinates: locationCoordinates});
        }
    }

    selectionChanged($event: MatAutocompleteSelectedEvent): void {
        this.addressLookup($event.option.value.id);
    }

    addressLookup(id: string): void {

        this.locationService.addressLookup(id).subscribe(objectData => {
            this.selectedAddress = objectData.response.docs[0];
            const locationCoordinates = objectData.response.docs[0].centroide_ll.replace('POINT(', '').replace(')', '').split(' ');
            const mapCenter: Array<number> = proj.transform(locationCoordinates, 'EPSG:4326', 'EPSG:3857');

            this.map.getView().setCenter(mapCenter);
            this.addMarker(locationCoordinates);

            this.zoomToLocation(this.locationSource);
        });
    }

    private addMarker(locationCoordinates: Coordinate) {
        const marker = new ol.Feature({
            geometry: new geom.Point(proj.fromLonLat(locationCoordinates))
        });

        marker.setStyle(this.pointStyle);

        this.clearFeatures();
        this.locationSource.refresh();
        this.locationSource.addFeature(marker);

    }

    private zoomToLocation(sourceLayer: source.Vector): void {
        const locationExtent = sourceLayer.getExtent();
        this.map.getView().fit(locationExtent, {
            size: this.map.getSize(),
            maxZoom: 14
        });
    }

    private clearFeatures(): void {
        const features = this.locationSource.getFeatures();
        features.forEach((feature) => this.locationSource.removeFeature(feature));
    }

    resultDisplay = (result: any): string => {
        return result.weergavenaam;
    };
}
