/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {
  AfterViewInit,
  Component,
  ElementRef,
  Input,
  OnChanges,
  OnInit,
  SimpleChanges,
  ViewChild,
} from "@angular/core";
import * as ol from "ol/index.js";
import * as layer from "ol/layer.js";
import * as proj from "ol/proj.js";
import * as geom from "ol/geom.js";
import * as source from "ol/source.js";
import WMTSTileGrid from "ol/tilegrid/WMTS.js";
import * as extent from "ol/extent.js";
import * as style from "ol/style.js";
import { Coordinate } from "ol/coordinate.js";
import { Geometry } from "../../zaken/model/geometry";
import { GeometryType } from "../../zaken/model/geometryType";
import proj4 from "proj4";
import { register } from "ol/proj/proj4.js";
import * as interaction from "ol/interaction";

@Component({
  selector: "zac-bag-locatie",
  templateUrl: "./bag-locatie.component.html",
  styleUrls: ["./bag-locatie.component.less"],
})
export class BagLocatieComponent implements OnInit, AfterViewInit, OnChanges {
  @Input() bagGeometrie: Geometry;
  @ViewChild("openLayersMap", { static: true }) openLayersMapRef: ElementRef;

  private map: ol.Map;
  private geometrieSource = new source.Vector();
  private readonly RDNEW: string = "EPSG:28992";
  private readonly DEFAULT_ZOOM: number = 14;
  private readonly MAX_ZOOM: number = 14;

  private defaultStyle: style.Style = new style.Style({
    fill: new style.Fill({
      color: "rgba(0,119,255,0.25)",
    }),
    stroke: new style.Stroke({
      color: "rgb(0,119,255)",
      width: 2,
    }),
  });
  private pointStyle: style.Style = new style.Style({
    text: new style.Text({
      text: "adjust",
      font: '640 16px "Material Symbols Outlined"',
      fill: new style.Fill({
        color: "#ff0000",
      }),
    }),
  });

  constructor() {
    proj4.defs(
      this.RDNEW,
      "+proj=sterea +lat_0=52.15616055555555 +lon_0=5.38763888888889 +k=0.9999079 +x_0=155000 +y_0=463000 +ellps=bessel +units=m" +
        " +towgs84=565.2369,50.0087,465.658,-0.406857330322398,0.350732676542563,-1.8703473836068,4.0812 +no_defs",
    );
    register(proj4);
    proj
      .get(this.RDNEW)
      .setExtent([-285401.92, 22598.08, 595401.92, 903401.92]);
  }

  ngOnInit(): void {
    const projection = proj.get(this.RDNEW);
    const extentMatrix = 20;
    const projectionExtent = projection.getExtent();
    const width = extent.getWidth(projectionExtent) / 256;
    const resolutions = new Array(extentMatrix);
    const matrixIds = new Array(extentMatrix);
    for (let z = 0; z < extentMatrix; ++z) {
      matrixIds[z] = z;
      resolutions[z] = width / Math.pow(2, z);
    }
    const kaartSource = new source.WMTS({
      layer: "standaard",
      format: "image/png",
      url: "https://service.pdok.nl/brt/achtergrondkaart/wmts/v2_0",
      matrixSet: this.RDNEW,
      style: "",
      tileGrid: new WMTSTileGrid({
        origin: extent.getTopLeft(projectionExtent),
        resolutions: resolutions,
        matrixIds: matrixIds,
      }),
    });
    const kaartLayer = new layer.Tile({
      source: kaartSource,
    });

    const geometrieLayer = new layer.Vector({
      source: this.geometrieSource,
      style: this.defaultStyle,
    });
    const view = new ol.View({
      projection: proj.get(this.RDNEW),
      constrainResolution: true,
      zoom: this.DEFAULT_ZOOM,
      minZoom: 3,
      maxZoom: this.MAX_ZOOM,
    });
    this.map = new ol.Map({
      controls: [],
      view: view,
      layers: [kaartLayer, geometrieLayer],
      interactions: interaction.defaults({
        mouseWheelZoom: false,
        onFocusOnly: true,
      }),
    });
  }

  ngAfterViewInit(): void {
    setTimeout(() => {
      this.map.setTarget(this.openLayersMapRef.nativeElement);
    }, 0);
    if (this.bagGeometrie) {
      this.draw(this.bagGeometrie);
      this.zoom();
    }
  }

  private draw(geometry: Geometry): void {
    if (geometry.type === GeometryType.POINT) {
      const coordinate: Array<number> = [geometry.point.x, geometry.point.y];
      this.addPoint(coordinate);
    }
    if (geometry.type === GeometryType.POLYGON) {
      const coordinates: Coordinate[][] = [[]];
      geometry.polygon.forEach((cs) => {
        coordinates.push(cs.map((c) => [c.x, c.y]));
      });
      this.addVlak(coordinates);
    }
    if (geometry.type === GeometryType.GEOMETRY_COLLECTION) {
      geometry.geometrycollection.forEach((g) => this.draw(g));
    }
  }

  private addPoint(coordinate: Coordinate): void {
    const marker = new ol.Feature({
      geometry: new geom.Point(coordinate),
    });
    marker.setStyle(this.pointStyle);
    this.geometrieSource.addFeature(marker);
  }

  private addVlak(coordinates: Coordinate[][]): void {
    const vlak = new ol.Feature({
      geometry: new geom.Polygon(coordinates),
    });
    this.geometrieSource.addFeature(vlak);
  }

  private zoom(): void {
    const locationExtent = this.geometrieSource.getExtent();
    this.map.getView().fit(locationExtent, {
      size: this.map.getSize(),
      maxZoom: this.DEFAULT_ZOOM,
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.bagGeometrie = changes.bagGeometrie.currentValue;
    if (this.bagGeometrie && !changes.bagGeometrie.isFirstChange()) {
      this.geometrieSource.clear();
      this.draw(this.bagGeometrie);
      this.zoom();
    }
  }
}
