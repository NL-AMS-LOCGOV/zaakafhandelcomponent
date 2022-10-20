export interface ZoekObject {
    id: string;
    type: ZoekObjectType;
}

export enum ZoekObjectType {
    TAAK = 'TAAK',
    ZAAK = 'ZAAK'
}
