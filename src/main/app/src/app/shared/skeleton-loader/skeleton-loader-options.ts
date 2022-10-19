export class SkeletonLoaderOptions {
    layout?: SkeletonLayout;
    fields?: number;
    fieldsPerRow?: number;
}

export enum SkeletonLayout {
    CARD = 'CARD',
    TABLE = 'TABLE',
    FIELDS = 'FIELDS'
}
