export class OrderUtil {

    static orderBy(sortKey?: string): { (a: any, b: any) } {
        return (a: any, b: any): number => {
            const valueA = sortKey ? a[sortKey] : a;
            const valueB = sortKey ? b[sortKey] : b;

            return typeof valueA === 'number' ? valueA - valueB : valueA.localeCompare(valueB);
        };
    }
}
