/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, ElementRef, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output, SimpleChanges, ViewChild} from '@angular/core';
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
import {AddressResult, LocationService, SuggestResult} from '../../../shared/location/location.service';
import {Subject} from 'rxjs';
import {takeUntil} from 'rxjs/operators';
import {GeometryCoordinate} from '../../model/geometryCoordinate';
import {FormControl} from '@angular/forms';
import {MatAutocompleteSelectedEvent} from '@angular/material/autocomplete';
import {LocationUtil} from '../../../shared/location/location-util';

@Component({
    selector: 'zac-locatie-zoek',
    templateUrl: './locatie-zoek.component.html',
    styleUrls: ['./locatie-zoek.component.less']
})
export class LocatieZoekComponent implements OnInit, AfterViewInit, OnChanges, OnDestroy {

    @Input() huidigeLocatie: GeometryCoordinate;
    @Output() locatie = new EventEmitter<AddressResult>();
    @ViewChild('openLayersMap', {static: true}) openLayersMapRef: ElementRef;
    selectedAddress: AddressResult;
    results: SuggestResult[];
    searchControl: FormControl = new FormControl();

    private unsubscribe$: Subject<void> = new Subject<void>();

    private map: ol.Map;
    private view: ol.View;
    private locationSource: source.Vector;
    private readonly WGS84: string = 'WGS84';
    private readonly EPSG3857: string = 'EPSG:3857';
    private readonly EXTENT_MATRIX: number = 20;

    private readonly DEFAULT_ZOOM: number = 8;
    private readonly MAX_ZOOM: number = 14;

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
        const resolutions = new Array(this.EXTENT_MATRIX);
        const matrixIds = new Array(this.EXTENT_MATRIX);
        for (let z = 0; z < this.EXTENT_MATRIX; ++z) {
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
            zoom: this.DEFAULT_ZOOM
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
            this.locationService.coordinatesToAddress(locationCoordinates).subscribe(objectData => {
                this.setAddress(objectData.response.docs[0]);
            });
        });

        this.map.on('click', () => {
            this.openLayersMapRef.nativeElement.focus();
        });

        this.map.on('pointerdrag', () => {
            this.openLayersMapRef.nativeElement.focus();
        });

        if (this.huidigeLocatie) {
            this.setAddress(this.huidigeLocatie);
        }
    }

    ngOnChanges(changes: SimpleChanges): void {
        for (const propName in changes) {
            if (changes.hasOwnProperty(propName)) {
                switch (propName) {
                    case 'huidigeLocatie':
                        if (!changes.huidigeLocatie.firstChange && changes.huidigeLocatie.currentValue) {
                            console.log(changes.huidigeLocatie);
                            this.setAddress(changes.huidigeLocatie.currentValue);
                        }
                        break;
                }
            }
        }
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

    clear(): void {
        if (this.selectedAddress) {
            this.locatie.next(null);
        }
    }

    save(): void {
        if (this.selectedAddress) {
            this.locatie.next(this.selectedAddress);
        }
    }

    selectionChanged($event: MatAutocompleteSelectedEvent): void {
        this.locationService.addressLookup($event.option.value.id).subscribe(objectData => {
            this.setAddress(objectData.response.docs[0]);
        });
    }

    setAddress(address) {
        this.selectedAddress = address;
        const locationCoordinates: Array<number> = LocationUtil.centroide_llToArray(address.centroide_ll);

        this.addMarker(locationCoordinates);

        this.zoomToLocation(this.locationSource, locationCoordinates);
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

    private zoomToLocation(sourceLayer: source.Vector, locationCoordinates: Array<number>): void {
        if (this.map.getView().getZoom() < this.MAX_ZOOM) {
            const mapCenter: Array<number> = proj.transform(locationCoordinates, 'EPSG:4326', 'EPSG:3857');
            this.map.getView().setCenter(mapCenter);

            const locationExtent = sourceLayer.getExtent();
            this.map.getView().fit(locationExtent, {
                size: this.map.getSize(),
                maxZoom: this.MAX_ZOOM
            });
        }
    }

    private clearFeatures(): void {
        const features = this.locationSource.getFeatures();
        features.forEach((feature) => this.locationSource.removeFeature(feature));
    }

    resultDisplay = (result: SuggestResult): string => {
        return result?.weergavenaam;
    };
}
