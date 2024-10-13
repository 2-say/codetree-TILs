import java.io.*;
import java.util.*;

public class Main {
    static int ans = 0;
    static int n, m, k, c;
    static int map[][];
    static int[] dy = { 0, 0, 1, -1 }, dx = { 1, -1, 0, 0 };
    static int[] ddy = { 1, 1, -1, -1 }, ddx = { 1, -1, -1, 1 };
    static int[][] treeYear; //제초제 기한

    static class BestKill implements Comparable<BestKill>{
        int y, x;
        int killN;
        BestKill(int y, int x, int killN) {
            this.y = y; 
            this.x = x;
            this.killN = killN;
        }

        @Override
        public int compareTo(BestKill o) {
            if(killN != o.killN) return Integer.compare(o.killN, killN);
            if(y != o.y) return Integer.compare(y, o.y);
            return Integer.compare(x, o.x);
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        k = Integer.parseInt(st.nextToken());
        c = Integer.parseInt(st.nextToken());

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
        while (m-- > 0) {
            //제초제년도 줄이기
            treeYearAfter();

            // 성장 & 번식
            growAndBreedingTree();

            // 제초제 뿌리기 좋은 곳 찾기
            BestKill b = bestPlaceFind();

            //정답 기록 
            ans += b.killN;

            //제초제 뿌리기
            killTree(b);
        }

        System.out.println(ans);
    }


    private static void treeYearAfter() {
        for(int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                if(treeYear[i][j] > 1) treeYear[i][j]--;
            }
        }
    }

    private static void killTree(BestKill b) {
        map[b.y][b.x] = 0;

        for(int d = 0; d < 4; d++) {  //모든 대각선 방향
            int ny = b.y;
            int nx = b.x;

            for(int l = 0; l < k; l++) { //범위 만큼 더가기
                ny += ddy[d];
                nx += ddx[d];

                if(inRange(ny, nx) && map[ny][nx] > 1) { //범위에 있고 나무이면
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
                if (map[i][j] < 1)
                    continue; 
                
                int treeKilln = 0;
                treeKilln += map[i][j];

                for(int d = 0; d < 4; d++) {  //모든 대각선 방향
                    int ny = i;
                    int nx = j;
                    for(int l = 0; l < k; l++) { //범위 만큼 더가기
                        ny += ddy[d];
                        nx += ddx[d];

                        if(inRange(ny, nx) && map[ny][nx] == -1) break; //벽 만나면 가지않는다.

                        if(inRange(ny, nx) && map[ny][nx] > 1) {
                            treeKilln += map[ny][nx];
                        }
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

                if (map[i][j] < 1)
                    continue;

                int count = 0;
                int breedAble = 0; //번식 가능한 칸

                for (int k = 0; k < 4; k++) {
                    int ny = i + dy[k];
                    int nx = j + dx[k];

                    if (inRange(ny, nx) && map[ny][nx] > 0)
                        count++;
                    if (inRange(ny, nx) && map[ny][nx] == 0 && treeYear[ny][nx] == 0)
                        breedAble++;
                }

                map[i][j] += count; // 성장하기
                tmp[i][j] += count; // 복사본도 함께

                // 번식진행
                for (int k = 0; k < 4; k++) {
                    int ny = i + dy[k];
                    int nx = j + dx[k];

                    if (inRange(ny, nx) && map[ny][nx] == 0 && treeYear[ny][nx] == 0) {
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