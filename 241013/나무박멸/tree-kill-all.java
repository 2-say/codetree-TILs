import java.io.*;
import java.util.*;

public class Main {
    static int ans = 0;
    static int n, m, k, c;
    static int map[][];
    static int[] dy = { 0, 0, 1, -1 }, dx = { 1, -1, 0, 0 };
    static int[] ddy = { 1, 1, -1, -1 }, ddx = { 1, -1, -1, 1 };
    static int[][] treeYear; // 제초제 기한

    static class BestKill implements Comparable<BestKill> {
        int y, x;
        int killN;

        BestKill(int y, int x, int killN) {
            this.y = y;
            this.x = x;
            this.killN = killN;
        }

        @Override
        public int compareTo(BestKill o) {
            if (killN != o.killN)
                return Integer.compare(o.killN, killN);
            if (y != o.y)
                return Integer.compare(y, o.y);
            return Integer.compare(x, o.x);
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        k = Integer.parseInt(st.nextToken());
        c = Integer.parseInt(st.nextToken()) + 1;

        map = new int[n][n];
        treeYear = new int[n][n];

        for (int i = 0; i < n; i++) {
            String[] in = br.readLine().split(" ");
            for (int j = 0; j < n; j++) {
                map[i][j] = Integer.parseInt(in[j]);
            }
        }

        solve();
    }

    private static void solve() {
        for (int i = 0; i < m; i++) {
            // 제초제년도 줄이기
            treeYearAfter();

            // 성장 & 번식
            growAndBreedingTree();

            // 제초제 뿌리기 좋은 곳 찾기
            BestKill b = bestPlaceFind();
            if (b == null)
                continue; // 뿌릴 수 없으면 패스

            // 정답 기록
            ans += b.killN;

            // 제초제 뿌리기
            killTree(b);
        }

        System.out.println(ans);
    }

    private static void treeYearAfter() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {

                if (treeYear[i][j] < 0) {
                    treeYear[i][j] = 0;
                }

                if (treeYear[i][j] > 0)
                    treeYear[i][j]--;
            }
        }
    }

    private static void killTree(BestKill b) {
        map[b.y][b.x] = 0;
        treeYear[b.y][b.x] = c;

        for (int d = 0; d < 4; d++) { // 모든 대각선 방향
            for (int l = 1; l <= k; l++) { // 범위 만큼 더가기
                int ny = b.y + ddy[d] * l;
                int nx = b.x + ddx[d] * l;

                if (!inRange(ny, nx))
                    break;

                if (map[ny][nx] == -1 || map[ny][nx] == 0 ||  treeYear[ny][nx] >= 1) {
                    treeYear[ny][nx] = c;
                    break;
                }

                if (map[ny][nx] > 0) {
                    map[ny][nx] = 0;
                    treeYear[ny][nx] = c;
                }
            }

        }
    }

    private static BestKill bestPlaceFind() {
        PriorityQueue<BestKill> pq = new PriorityQueue<>();

        // 제초제 가장 좋은 곳 찾기
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {

                if (map[i][j] == -1) // 벽, 제초제, 빈칸이면 제외
                    continue;

                int treeKilln = 0;
                treeKilln += map[i][j];

                for (int d = 0; d < 4; d++) { // 모든 대각선 방향
                    for (int l = 1; l <= k; l++) { // 범위 만큼 더가기
                        int ny = i + ddy[d] * l;
                        int nx = j + ddx[d] * l;

                        if (!inRange(ny, nx) || map[ny][nx] == -1 || map[ny][nx] == 0 || treeYear[ny][nx] >= 1)
                            break;

                        if (map[ny][nx] > 0)
                            treeKilln += map[ny][nx];
                    }
                }

                pq.add(new BestKill(i, j, treeKilln));
            }
        }

        return pq.poll();
    }

    private static void growAndBreedingTree() {
        int[][] tmp = new int[n][n];
        for (int i = 0; i < n; i++)
            tmp[i] = map[i].clone();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {

                if (map[i][j] <= 0 || treeYear[i][j] > 0) // 나무이고, 제초제 끝나야 성장 가능
                    continue;

                int count = 0; // 성장 수
                int breedAble = 0; // 번식 가능한 칸

                for (int k = 0; k < 4; k++) {
                    int ny = i + dy[k];
                    int nx = j + dx[k];

                    if (inRange(ny, nx) && map[ny][nx] > 0)
                        count++; // 주변에 나무 있으면 성장
                    if (inRange(ny, nx) && map[ny][nx] == 0) // 번식 가능한지 체크
                        breedAble++;
                }

                map[i][j] += count; // 성장하기
                tmp[i][j] += count; // 복사본도 함께

                // 번식진행
                for (int k = 0; k < 4; k++) {
                    int ny = i + dy[k];
                    int nx = j + dx[k];

                    if (inRange(ny, nx) && map[ny][nx] == 0) {
                        tmp[ny][nx] += (map[i][j] / breedAble);
                    }
                }
            }
        }
        // 덮어쓰기
        map = tmp;
    }

    static boolean inRange(int y, int x) {
        return y >= 0 && x >= 0 && y < n && x < n;
    }
}