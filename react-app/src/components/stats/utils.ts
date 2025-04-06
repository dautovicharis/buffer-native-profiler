/**
 * Utility functions for the Buffer Monitor application
 */
export const BufferMonitorUtils = {
    /**
     * Formats bytes into human-readable format
     * @param {number} bytes - The number of bytes to format
     * @param {number} decimals - Number of decimal places to show
     * @returns {string} Formatted string (e.g., "1.5 MB")
     */
    formatBytes: (bytes: number, decimals: number = 2): string => {
        if (bytes === 0) return '0 Bytes';

        const k = 1024;
        const dm = decimals < 0 ? 0 : decimals;
        const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];

        const i = Math.floor(Math.log(bytes) / Math.log(k));

        return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
    },

    /**
     * Converts JVM class names to more readable format
     * @param {string} className - The JVM class name
     * @returns {string} Human-readable class name
     */
    getReadableClassName: (className: string): string => {
        switch (className) {
            case '[B': return 'ByteArray';
            case 'byte[]': return 'ByteArray';
            case '[Ljava.lang.String;': return 'StringArray';
            case '[I': return 'IntArray';
            case 'int[]': return 'IntArray';
            case 'java.lang.String': return 'String';
            case 'System Overhead': return 'System Overhead';
            default: return className;
        }
    }
};
