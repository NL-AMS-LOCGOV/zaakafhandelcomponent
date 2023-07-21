import { GeometryCoordinate } from "../../zaken/model/geometryCoordinate";
import { Geometry } from "../../zaken/model/geometry";
import { GeometryType } from "../../zaken/model/geometryType";

export class LocationUtil {
  /**
   * Het converteren van een het centroide_ll attribuut vanuit de response van de locatieserver
   * @param wkt centroide_ll zijn latitude,longitude coordinaten in graden volgens de ETRS:89 projectie.
   * @private
   */
  public static wktToPoint(wkt: string): Geometry {
    const geometrie = new Geometry(GeometryType.POINT);
    const coordinates: string[] = wkt
      .replace("POINT(", "")
      .replace(")", "")
      .split(" ");
    geometrie.point = new GeometryCoordinate(+coordinates[0], +coordinates[1]);
    return geometrie;
  }

  public static coordinateToPoint(coordinate: number[]): Geometry {
    const geometrie = new Geometry(GeometryType.POINT);
    geometrie.point = new GeometryCoordinate(coordinate[0], coordinate[1]);
    return geometrie;
  }

  public static format(geometry: Geometry) {
    if (geometry && geometry.type == GeometryType.POINT) {
      return geometry.point.y + ", " + geometry.point.x;
    }
    return null;
  }
}
