/**
 * ====================================================================
 * FILE    : Ex02_MatrixOperations.java
 * MODULE  : 04 — Strings & Arrays
 * PURPOSE : Practice 2D array operations and matrix algorithms
 * ====================================================================
 *
 * EXERCISES:
 *
 *   1. transposeMatrix(matrix)     → swap rows and columns
 *   2. rotateMatrix90(matrix)      → rotate NxN matrix clockwise 90°
 *   3. spiralOrder(matrix)         → traverse matrix in spiral order
 *   4. findSaddlePoint(matrix)     → min in row AND max in column
 *
 * ====================================================================
 */
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class Ex02_MatrixOperations {

    /**
     * Transpose a matrix (swap rows with columns).
     * 
     * Example:
     *   Input:   [[1, 2, 3],      Output:  [[1, 4],
     *             [4, 5, 6]]                [2, 5],
     *                                        [3, 6]]
     *
     * VISUAL:
     *   ┌─────────┐         ┌───────┐
     *   │ 1  2  3 │   →→→   │ 1  4  │
     *   │ 4  5  6 │         │ 2  5  │
     *   └─────────┘         │ 3  6  │
     *   (2×3)               └───────┘
     *                       (3×2)
     *
     * Key: result[j][i] = original[i][j]
     */
    public static int[][] transposeMatrix(int[][] matrix) {
        // TODO: Implement this
        return new int[0][0];
    }

    /**
     * Rotate an NxN matrix 90 degrees clockwise IN PLACE.
     * 
     * Example:
     *   Input:   [[1, 2, 3],      Output:  [[7, 4, 1],
     *             [4, 5, 6],                [8, 5, 2],
     *             [7, 8, 9]]                [9, 6, 3]]
     *
     * ALGORITHM (two-step approach):
     *   ┌─────────┐    Step 1:     ┌─────────┐    Step 2:      ┌─────────┐
     *   │ 1  2  3 │   Transpose    │ 1  4  7 │   Reverse       │ 7  4  1 │
     *   │ 4  5  6 │   ────────▶    │ 2  5  8 │   each row     │ 8  5  2 │
     *   │ 7  8  9 │               │ 3  6  9 │   ────────▶     │ 9  6  3 │
     *   └─────────┘               └─────────┘                 └─────────┘
     */
    public static void rotateMatrix90(int[][] matrix) {
        // TODO: Implement this
    }

    /**
     * Return elements in spiral order (outer ring → inner ring).
     * 
     * Example:
     *   Input:  [[1, 2, 3],
     *            [4, 5, 6],
     *            [7, 8, 9]]
     *   Output: [1, 2, 3, 6, 9, 8, 7, 4, 5]
     *
     * TRAVERSAL PATTERN:
     *   ┌──────────────┐
     *   │ 1 → 2 → 3    │  → right along top
     *   │           ↓   │  ↓ down right side
     *   │ 4    5    6   │
     *   │ ↑         ↓   │
     *   │ 7 ← 8 ← 9    │  ← left along bottom
     *   └──────────────┘     then ↑ up left side
     *                        then inward to 5
     */
    public static List<Integer> spiralOrder(int[][] matrix) {
        // TODO: Implement this
        return new ArrayList<>();
    }

    // ── Test your implementations ───────────────────────────────────
    public static void main(String[] args) {
        System.out.println("=== Matrix Operations Exercises ===\n");

        // Test transpose
        int[][] m1 = {{1, 2, 3}, {4, 5, 6}};
        int[][] t = transposeMatrix(m1);
        System.out.println("Transpose:");
        for (int[] row : t) System.out.println("  " + Arrays.toString(row));

        // Test rotate 90
        int[][] m2 = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        rotateMatrix90(m2);
        System.out.println("Rotate 90°:");
        for (int[] row : m2) System.out.println("  " + Arrays.toString(row));

        // Test spiral order
        int[][] m3 = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        System.out.println("Spiral: " + spiralOrder(m3));
    }
}
