export class CollectionUtils {

    public static binarySearch<T>(array: T[], target: T, comparator: (a: T, b: T) => number): number {
        let left = 0;
        let right = array.length - 1;

        while (left <= right) {
            const mid = Math.floor((left + right) / 2);
            const cmp = comparator(array[mid], target);

            if (cmp === 0) {
                return mid;
            } else if (cmp < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return -(left + 1);
    }

    public static binarySearchInRange<T>(
        array: T[],
        target: number,
        startExtractor: (item: T) => number,
        endExtractor: (item: T) => number
    ): number {
        let low = 0;
        let high = array.length - 1;

        while (low <= high) {
            const mid = Math.floor((low + high) / 2);
            const start = startExtractor(array[mid]);
            const end = endExtractor(array[mid]);

            if (target >= start && target < end) {
                return mid;
            } else if (target < start) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }

        return -1;
    }

}
