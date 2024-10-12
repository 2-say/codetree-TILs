import java.io.*;
import java.util.*;

public class Main {

    static int[][] map;
    static int R, C, K;
    static List<int[]> gols = new ArrayList<>();
    static int ans = 0;
    static P curGol;
    static int[] dy = { -1, 0, 1, 0 }, dx = { 0, 1, 0, -1 };
    static int COLOR = 1;
    static int EXIST = 10001;

    static class P {
        int y, x;
        int d;

        P(int y, int x, int d) {
            this.y = y;
            this.x = x;
            this.d = d;
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        R = Integer.parseInt(st.nextToken());
        C = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());

        map = new int[R + 3][C];

        for (int i = 0; i < K; i++) {
            st = new StringTokenizer(br.readLine());
            int gc = Integer.parseInt(st.nextToken())-1;
            int d = Integer.parseInt(st.nextToken());
            gols.add(new int[] { gc, d });
        }

        solve();
    }

    private static void solve() {
        for (int[] g : gols) {
            P gol = new P(1, g[0], g[1]);

            // 골렘 이동
            while (moveGol(gol)) {
            }
            ;

            // 맵 안에 없으면 초기화 후 새롭게 시작
            if (gol.y < 4) {
                map = new int[R + 3][C];
                continue;
            }

            // 맵에 기록
            drawGol(gol);

            // 요정이동
            int result = moveAngel(gol.y, gol.x);
            ans += result;
        }

        System.out.println(ans);
    }

    private static int moveAngel(int y, int x) {
        //System.out.println("DFS 탐색 " +  y + " " + x);
        int maxR = 0;
        boolean[][] visited = new boolean[R+3][C];
        Stack<int[]> stac = new Stack<>();
        stac.add(new int[] { y, x, map[y][x] });
        visited[y][x] = true;

        while (!stac.isEmpty()) {
            int[] cur = stac.pop();
            
            maxR = Math.max(maxR, cur[0]-2);

            for (int i = 0; i < 4; i++) {
                int ny = cur[0] + dy[i];
                int nx = cur[1] + dx[i];

                if (!inRange(ny, nx) || map[ny][nx] == 0 || visited[ny][nx])
                    continue;

                int color = cur[2];
                if (cur[2] < 10000) color = cur[2] + 10000;
                int nextColor = map[ny][nx];
                if (map[ny][nx] < 10000) nextColor = map[ny][nx] + 10000;

                // 만약 색이 다른데, 현 위치가 출구라면
                if (color != map[ny][nx] && cur[2] > 10000) {
                    visited[ny][nx] = true;
                    stac.add(new int[] { ny, nx, map[ny][nx] });
                } else if(color == nextColor) { 
                    visited[ny][nx] = true;
                    stac.add(new int[] { ny, nx, map[ny][nx] });
                }
            }
        }

        return maxR;
    }

    private static void drawGol(P gol) {
        map[gol.y][gol.x] = COLOR;
        for (int i = 0; i < 4; i++) {
            int ny = gol.y + dy[i];
            int nx = gol.x + dx[i];
            if (i == gol.d)
                map[ny][nx] = EXIST;
            else
                map[ny][nx] = COLOR;
        }
        COLOR++;
        EXIST++;
    }

    private static boolean moveGol(P gol) {
        // 아래로 이동할 수 있으면 이동
        if (isArroundEmpty(gol.y + 1, gol.x)) {
            gol.y++;
            return true;
        }

        // 왼쪽, 아래 이동 가능하면 이동
        else if (isArroundEmpty(gol.y, gol.x - 1) && isArroundEmpty(gol.y + 1, gol.x - 1)) {
            gol.y++;
            gol.x--;
            gol.d = (4 + (gol.d - 1)) % 4;
            return true;
        }

        // 오른쪽, 아래 이동 가능하면 이동
        else if (isArroundEmpty(gol.y, gol.x + 1) && isArroundEmpty(gol.y + 1, gol.x + 1)) {
            gol.y++;
            gol.x++;
            gol.d = (gol.d + 1) % 4;
            return true;
        }

        return false;
    }

    private static boolean isArroundEmpty(int y, int x) {
        for (int i = 0; i < 4; i++) {
            int ny = y + dy[i];
            int nx = x + dx[i];

            if (!inRange(ny, nx) || map[ny][nx] != 0)
                return false;

        }
        return true;
    }

    private static boolean inRange(int y, int x) { // y 0 부터로 했음 주의
        return x >= 0 && y >= 0 && y < R+3 && x < C;
    }

}