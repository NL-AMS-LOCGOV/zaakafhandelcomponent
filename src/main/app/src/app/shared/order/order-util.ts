export class OrderUtil {
  static orderBy(sortKey?: string): { (a: any, b: any) } {
    return (a: any, b: any): number => {
      const valueA = sortKey ? a[sortKey] : a;
      const valueB = sortKey ? b[sortKey] : b;

      return typeof valueA === "number"
        ? valueA - valueB
        : valueA.localeCompare(valueB);
    };
  }

  static orderAsIs(): { (a: any, b: any) } {
    // Array sort is stable since node.js 12
    return (a: any, b: any): number => {
      return 0;
    };
  }
}
