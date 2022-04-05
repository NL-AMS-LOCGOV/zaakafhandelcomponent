import {GeometryCoordinate} from '../../zaken/model/geometryCoordinate';
import {Geometry} from '../../zaken/model/geometry';
import {GeometryType} from '../../zaken/model/geometryType';

export class LocationUtil {

    public static point(centroidell: string): Geometry {
        const geometrie = new Geometry(GeometryType.POINT);
        geometrie.point = this.centroide_llToCoordinate(centroidell);
        return geometrie;
    }

    private static centroide_llToCoordinate(centroidell: string): GeometryCoordinate {
        const coordinates: string[] = centroidell.replace('POINT(', '').replace(')', '').split(' ');
        return new GeometryCoordinate(+coordinates[0], +coordinates[1]);
    }

    public static centroide_llToArray(centroidell: string): Array<number> {
        const coordinates: string[] = centroidell.replace('POINT(', '').replace(')', '').split(' ');
        return [+coordinates[0], +coordinates[1]];
    }

}
