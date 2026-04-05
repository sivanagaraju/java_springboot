/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Sol02_MatrixOperations.java                            ║
 * ║  MODULE : 00-java-foundation / 04-strings-and-arrays             ║
 * ║  GRADLE : ./gradlew :00-java-foundation:run                      ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Solution — 2D array algorithms                 ║
 * ║  DEMONSTRATES   : transpose, in-place rotation, spiral traversal ║
 * ║  PYTHON COMPARE : zip(*matrix) vs manual loop; list comprehension║
 * ║                                                                  ║
 * ║  ALGORITHMS:                                                     ║
 * ║   transposeMatrix  → result[j][i] = src[i][j]   O(m*n)         ║
 * ║   rotateMatrix90   → transpose + reverse rows   O(n²) in-place  ║
 * ║   spiralOrder      → 4-boundary pointers        O(m*n)          ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.basics.exercises.solutions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Solutions for 2D array (matrix) operation exercises.
 *
 * <p>Key pattern: matrix dimensions are {@code matrix.length} (rows)
 * and {@code matrix[0].length} (columns). Getting them backwards is the
 * most common array bug in Java.
 *
 * <p>Python equivalent: {@code list(zip(*matrix))} transposes in one line.
 * Java requires explicit nested loops because arrays are not iterable
 * in the same way as Python lists.
 */
public class Sol02_MatrixOperations {

    /**
     * Transposes a matrix — rows become columns.
     *
     * <p>For an M×N matrix, the result is N×M.
     * Key transformation: {@code result[j][i] = original[i][j]}.
     *
     * <p>Python: {@code list(map(list, zip(*matrix)))}
     *
     * @param matrix input M×N matrix (not modified)
     * @return a new N×M transposed matrix
     */
    public static int[][] transposeMatrix(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        // WHY new int[cols][rows]: result has FLIPPED dimensions —
        // original M rows and N cols become N rows and M cols
        int[][] result = new int[cols][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // WHY result[j][i]: swap row/column indices
                result[j][i] = matrix[i][j];
            }
        }
        return result;
    }

    /**
     * Rotates an NxN matrix 90° clockwise IN PLACE.
     *
     * <p>Two-step algorithm (O(n²) time, O(1) space):
     * <ol>
     *   <li>Transpose: {@code matrix[i][j] ↔ matrix[j][i]} for all j > i</li>
     *   <li>Reverse each row: {@code matrix[i][0..n] reversed}</li>
     * </ol>
     *
     * <p>Why this works: clockwise 90° = transpose + horizontal reverse.
     * Counterclockwise 90° = transpose + vertical reverse.
     *
     * @param matrix NxN square matrix; modified in place
     */
    public static void rotateMatrix90(int[][] matrix) {
        int n = matrix.length;

        // Step 1: Transpose (in-place swap upper triangle with lower triangle)
        // WHY j starts at i+1: only swap the upper triangle to avoid double-swapping.
        // If we also swapped (j,i) for j < i, we'd undo the first swap.
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int temp = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = temp;
            }
        }

        // Step 2: Reverse each row
        // WHY only reverse (not re-transpose): after transpose, clockwise 90°
        // requires a horizontal mirror — reversing each row achieves that.
        for (int i = 0; i < n; i++) {
            int left = 0;
            int right = n - 1;
            while (left < right) {
                int temp = matrix[i][left];
                matrix[i][left] = matrix[i][right];
                matrix[i][right] = temp;
                left++;
                right--;
            }
        }
    }

    /**
     * Returns matrix elements in spiral order (outer ring inward).
     *
     * <p>Four-boundary approach: maintains top, bottom, left, right pointers
     * and shrinks them as each side is consumed.
     *
     * <p>Time: O(m*n) — every element visited exactly once.
     * Space: O(m*n) — the result list.
     *
     * @param matrix the input matrix
     * @return list of elements in spiral order
     */
    public static List<Integer> spiralOrder(int[][] matrix) {
        List<Integer> result = new ArrayList<>();
        if (matrix == null || matrix.length == 0) {
            return result;
        }

        // WHY four boundaries: each pass around the spiral consumes one full ring.
        // After processing top row → move top down.
        // After processing right col → move right left.
        // After processing bottom row → move bottom up.
        // After processing left col → move left right.
        int top = 0, bottom = matrix.length - 1;
        int left = 0, right = matrix[0].length - 1;

        while (top <= bottom && left <= right) {
            // → Traverse top row left to right
            for (int col = left; col <= right; col++) {
                result.add(matrix[top][col]);
            }
            top++; // WHY: top row consumed

            // ↓ Traverse right column top to bottom
            for (int row = top; row <= bottom; row++) {
                result.add(matrix[row][right]);
            }
            right--; // WHY: right column consumed

            // ← Traverse bottom row right to left (only if rows remain)
            if (top <= bottom) {
                for (int col = right; col >= left; col--) {
                    result.add(matrix[bottom][col]);
                }
                bottom--; // WHY: bottom row consumed
            }

            // ↑ Traverse left column bottom to top (only if columns remain)
            if (left <= right) {
                for (int row = bottom; row >= top; row--) {
                    result.add(matrix[row][left]);
                }
                left++; // WHY: left column consumed
            }
        }
        return result;
    }

    /**
     * Runs all test cases.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        System.out.println("=== Matrix Operations Solutions ===\n");

        // Transpose: 2×3 → 3×2
        int[][] m1 = {{1, 2, 3}, {4, 5, 6}};
        int[][] t = transposeMatrix(m1);
        System.out.println("Transpose of 2×3 matrix:");
        for (int[] row : t) System.out.println("  " + Arrays.toString(row));
        // Expected: [1, 4] / [2, 5] / [3, 6]

        // Rotate 90° clockwise in-place
        int[][] m2 = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        rotateMatrix90(m2);
        System.out.println("\nRotate 90° clockwise:");
        for (int[] row : m2) System.out.println("  " + Arrays.toString(row));
        // Expected: [7, 4, 1] / [8, 5, 2] / [9, 6, 3]

        // Spiral order
        int[][] m3 = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        System.out.println("\nSpiral order: " + spiralOrder(m3));
        // Expected: [1, 2, 3, 6, 9, 8, 7, 4, 5]

        // Edge case: single row
        int[][] m4 = {{1, 2, 3, 4}};
        System.out.println("Spiral (single row): " + spiralOrder(m4));
        // Expected: [1, 2, 3, 4]

        // Edge case: single column
        int[][] m5 = {{1}, {2}, {3}};
        System.out.println("Spiral (single col): " + spiralOrder(m5));
        // Expected: [1, 2, 3]
    }
}

/* ═══════════════════════════════════════════════════════════════════
 * COMMON MISTAKES
 * ═══════════════════════════════════════════════════════════════════
 *
 * MISTAKE 1: Transposing in-place on non-square matrix
 *   WRONG: for(i) for(j) matrix[i][j] = matrix[j][i];  // loses data!
 *   PROBLEM: For non-square matrices, swapping in-place with i/j indices
 *            overwrites values before they're moved.
 *   RIGHT: Allocate a new result[cols][rows] array for non-square transpose.
 *          Only square matrices can be transposed in-place.
 *
 * MISTAKE 2: Double-swapping in in-place transpose
 *   WRONG: for(i) for(j) swap(matrix[i][j], matrix[j][i]);
 *   PROBLEM: If j starts at 0, you swap (1,2) then later (2,1) → no net change.
 *   RIGHT: for(j = i+1) — only process the upper triangle.
 *
 * MISTAKE 3: Off-by-one in spiral boundary checks
 *   After consuming top row, increment top. After consuming right col, decrement right.
 *   But you MUST check top <= bottom before consuming bottom row (matrix may have
 *   only one row left). Same for left <= right before consuming left column.
 *
 * MISTAKE 4: matrix[row][col] vs matrix[col][row]
 *   Java 2D arrays are row-major: matrix[row][col].
 *   Most index bugs come from swapping row and col in the indexing expression.
 *   Always name loop variables 'row' and 'col' (not i and j) to avoid confusion.
 *
 * MISTAKE 5: Using matrix.length for column count
 *   matrix.length = number of ROWS
 *   matrix[0].length = number of COLUMNS
 *   Confusing the two causes ArrayIndexOutOfBoundsException on non-square matrices.
 * ═══════════════════════════════════════════════════════════════════ */
