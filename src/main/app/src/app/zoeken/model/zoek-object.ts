export interface ZoekObject {
    id: string;
    type: ZoekObjectType;
}

export enum ZoekObjectType {
    DOCUMENT = 'DOCUMENT',
    TAAK = 'TAAK',
    ZAAK = 'ZAAK'
}
