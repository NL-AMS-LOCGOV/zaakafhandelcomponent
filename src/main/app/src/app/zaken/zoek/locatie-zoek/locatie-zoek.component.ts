/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, ElementRef, EventEmitter, OnDestroy, OnInit, Output, ViewChild} from '@angular/core';
import * as ol from 'ol/index.js';
import * as layer from 'ol/layer.js';
import * as proj from 'ol/proj.js';
import * as source from 'ol/source.js';
import WMTSTileGrid from 'ol/tilegrid/WMTS.js';
import * as extent from 'ol/extent.js';
import * as control from 'ol/control.js';
import * as style from 'ol/style.js';
import * as coordinate from 'ol/coordinate.js';
import {Coordinate} from 'ol/coordinate.js';
import * as interaction from 'ol/interaction.js';

@Component({
    selector: 'zac-locatie-zoek',
    templateUrl: './locatie-zoek.component.html',
    styleUrls: ['./locatie-zoek.component.less']
})
export class LocatieZoekComponent implements OnInit, AfterViewInit, OnDestroy {

    @Output() locatie = new EventEmitter<Coordinate>();
    @ViewChild('openLayersMap', {static: true}) openLayersMapRef: ElementRef;

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

    constructor() {

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

        const mousePosition = new control.MousePosition({
            coordinateFormat: coordinate.createStringXY(2),
            projection: this.EPSG3857,
            target: document.getElementById('mouse-position'),
            undefinedHTML: '&nbsp;'
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
        this.map.addControl(mousePosition);
    }

    ngAfterViewInit(): void {
        setTimeout(() => {
            this.map.setTarget(this.openLayersMapRef.nativeElement);
        }, 0);

        this.map.on('click', (event) => {
            this.locatie.next(proj.transform(event.coordinate, 'EPSG:3857', 'EPSG:4326'));
        });

        this.map.on('click', () => {
            this.openLayersMapRef.nativeElement.focus();
        });

        this.map.on('pointerdrag', () => {
            this.openLayersMapRef.nativeElement.focus();
        });
    }

    ngOnDestroy(): void {
    }

}
