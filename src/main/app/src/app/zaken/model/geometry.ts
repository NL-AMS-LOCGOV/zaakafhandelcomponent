export class Geometry {

    constructor(type: 'Point' | 'Polygon' | 'GeometryCollection') {
        this.type = type;
    }

    type: 'Point' | 'Polygon' | 'GeometryCollection';

    point: Coordinates;

    polygon: Coordinates[][];

    geometrycollection: Geometry[];
}

export class Coordinates {

    constructor(x: number, y: number) {
        this.x = x;
        this.y = y;
    }

    x: number;
    y: number;
}
