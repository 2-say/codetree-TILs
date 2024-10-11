import java.io.*;
import java.util.*;

public class Main {
    static int n, m, k;
    static List<Integer>[][] map; // 총 정보맵
    static List<Player> players = new ArrayList<>();
    static int[] score;
    static int[] dy = { -1, 0, 1, 0 }, dx = { 0, 1, 0, -1 };

    static class Player {
        int id;
        int y, x;
        int atk = -1;
        int av; // 능력치
        int dir; // 방향

        Player(int id, int y, int x, int av, int d) {
            this.id = id;
            this.y = y;
            this.x = x;
            this.av = av;
            this.dir = d;
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        k = Integer.parseInt(st.nextToken());

        map = new List[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                map[i][j] = new ArrayList<>();
            }
        }

        score = new int[m];

        for (int i = 0; i < n; i++) {
            String[] in = br.readLine().split(" ");
            for (int j = 0; j < n; j++) {
                int v = Integer.parseInt(in[j]);
                if (v > 0)
                    map[i][j].add(v);
            }
        }

        for (int i = 0; i < m; i++) {
            st = new StringTokenizer(br.readLine());
            int y = Integer.parseInt(st.nextToken()) - 1;
            int x = Integer.parseInt(st.nextToken()) - 1;
            int d = Integer.parseInt(st.nextToken());
            int s = Integer.parseInt(st.nextToken());

            players.add(new Player(i, y, x, s, d));
        }

        solve();
    }

    private static void solve() {
        for (int tc = 1; tc <= k; tc++) { // 라운드 진행
            for (Player p : players) {

                // 플레이어 이동 - 이동한 곳 반환
                int[] yx = playersMove(p);

                // 모든 플레이어, 격투, 총교체 경우 진행
                playersAction(p, yx);
            }
        }

        StringBuilder sb = new StringBuilder();
        for (int v : score)
            sb.append(v).append(" ");
        System.out.println(sb);
    }

    /**
     * 한명씩 움직이니 한명밖에 있을 수 있다.
     */
    private static void playersAction(Player p, int[] yx) {
        // 플레이어가 있는지 파악한다
        Player target = existPlayer(p, yx);
        if (target != null) {

            // 결투시작
            Player[] result = fight(p, target); // 승자가 0열, 패배가 1열

            int pAtk = (result[0].atk == -1) ? 0 : result[0].atk;
            int tAtk = (result[1].atk == -1) ? 0 : result[1].atk;

            int winnerP = (pAtk + result[0].av) - (tAtk + result[1].av);

            // 패배한 플레이어는 총을두고 돌면서 이동한다.
            looserDo(result[1], yx);

            // 승리한 플레이어는 점수가 올라가고 놓고간 총을 교체한다.
            winnerDo(result[0], yx, winnerP);
        } else {
            // 바닥에 총이 있다면 총교체
            gunChange(p, yx);
        }
    }

    private static void winnerDo(Player wP, int[] yx, int point) {
        score[wP.id] += point;
        gunChange(wP, yx);
    }

    private static void looserDo(Player p, int[] yx) {
        for (int i = 0; i < 3; i++) { // 3번만 진행 270도 회전
            int ny = yx[0] + dy[p.dir];
            int nx = yx[1] + dx[p.dir]; // 현재 방향 그대로 유지

            Player tar = existPlayer(p, new int[] { ny, nx });

            if (!inRange(ny, nx) || tar != null) { // 만약 격자나 플레이어가 존재 시 90도
                p.dir = (p.dir + 1) % 4; // 방향바꿔서 시도
                continue;
            }

            // 원래 자리에 공격력 높은 총 놓기
            if (p.atk != -1) {
                map[yx[0]][yx[1]].add(p.atk);
                p.atk = -1; // 총 없음 처리
            }

            // 해당 플레이어이동
            p.y = ny;
            p.x = nx;
            gunChange(p, new int[] { ny, nx });
            break;
        }
    }

    private static Player[] fight(Player p, Player t) { // 승자가 0열, 패배가 1열
        int pAtk = (p.atk == -1) ? 0 : p.atk; // 총이 없으면 0으로 치환합니다.
        int tAtk = (t.atk == -1) ? 0 : t.atk;
        int pTotal = (pAtk + p.av);
        int tTotal = (tAtk + t.av);

        if (pTotal == tTotal) { // 같으면
            if (p.av > t.av)
                return new Player[] { p, t };
            else
                return new Player[] { t, p };
        } else if (pTotal > tTotal) {
            return new Player[] { p, t };
        } else {
            return new Player[] { t, p };
        }
    }

    private static void gunChange(Player p, int[] yx) {
        if (!map[yx[0]][yx[1]].isEmpty()) {
            Collections.sort(map[yx[0]][yx[1]]);
            int idx = map[yx[0]][yx[1]].size() - 1;
            int strongGun = map[yx[0]][yx[1]].get(idx);

            if (p.atk == -1) { // 내가 총이 없는 상황이라면
                p.atk = strongGun;
                map[yx[0]][yx[1]].remove(idx);
            } else if (p.atk < strongGun) { // 바닥 총이 더 쎄면 교체
                int tmp = p.atk;
                p.atk = strongGun;
                map[yx[0]][yx[1]].remove(idx);
                map[yx[0]][yx[1]].add(tmp);
            }
        }
    }

    static Player existPlayer(Player t, int[] yx) {
        for (Player p : players) {
            if (t.id != p.id && p.y == yx[0] && p.x == yx[1])
                return p;
        }
        return null;
    }

    private static int[] playersMove(Player p) {
        int ny = p.y + dy[p.dir];
        int nx = p.x + dx[p.dir];

        if (!inRange(ny, nx)) { // 격자를 나가면 반대 방향으로 이동
            p.dir = (p.dir + 2) % 4;
            ny = p.y + dy[p.dir];
            nx = p.x + dx[p.dir];
        }

        // 이동
        p.y = ny;
        p.x = nx;
        return new int[] { p.y, p.x };
    }

    private static boolean inRange(int y, int x) {
        return y >= 0 && x >= 0 && y < n && x < n;
    }

}