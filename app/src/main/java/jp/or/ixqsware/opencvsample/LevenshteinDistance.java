package jp.or.ixqsware.opencvsample;

/**
 *レーベンシュタイン距離による文字列比較
 */
public class LevenshteinDistance {

    public LevenshteinDistance() {
        super();
    }

    public int calculateDistance(String str1, String str2) {
        int len1 = str1.length();
        int len2 = str2.length();

        int[][] rows = new int[len1 + 1][len2 + 1];

        for (int i = 0; i < len1 + 1; i++) {
            rows[i][0] = i;
        }

        for (int i = 0; i < len2 + 1; i ++) {
            rows[0][i] = i;
        }

        for (int r = 1; r < len1 + 1; r++) {
            for (int c = 1; c < len2 + 1; c++) {
                rows[r][c] = Math.min(
                        Math.min(
                                (Integer)(rows[r - 1][r - 1])
                                        + ((str1.substring(r - 1, r).equals(str2.substring(c - 1, c))) ? 0 : 1),  //置換
                                (Integer)(rows[r][c - 1]) + 1),  // 削除
                        (Integer)(rows[r - 1][c]) + 1);  // 挿入
            }
        }
        return (int)rows[len1][len2];
    }
}
