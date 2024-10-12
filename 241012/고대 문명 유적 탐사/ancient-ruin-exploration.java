import java.io.*;
import java.util.*;

public class Main {

    static int[][] map = new int[5][5];
    static boolean[][] visited;
    static int K, M;
    static Queue<Integer> wallQ = new ArrayDeque<>();
    static PriorityQueue<RotateBest> pq = new PriorityQueue<>();
    static int[] dy = {0, 1, 0, -1}, dx = {1, 0, -1, 0};
    static int answer = 0;

    static class RotateBest implements Comparable<RotateBest> {
        int cost;
        int y, x;
        int rotateCount;
        List<int[]> founds = new ArrayList<>();
        int[][] newMap = new int[5][5];

        RotateBest(int cost, int y, int x, int rotateCount, List<int[]> founds, int[][] newMap) {
            this.cost = cost;
            this.y = y;
            this.x = x;
            this.rotateCount = rotateCount;
            this.founds = founds;
            this.newMap = newMap;
        }

        @Override
        public int compareTo(RotateBest o) {
            if (cost != o.cost)
                return Integer.compare(o.cost, cost);
            if (rotateCount != o.rotateCount)
                return Integer.compare(rotateCount, o.rotateCount);
            if (y != o.y)
                return Integer.compare(x, o.x);
            return Integer.compare(y, o.y);
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        K = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());

        for (int i = 0; i < 5; i++) {
            String[] in = br.readLine().split(" ");
            for (int j = 0; j < 5; j++) {
                map[i][j] = Integer.parseInt(in[j]);
            }
        }

        st = new StringTokenizer(br.readLine());
        for (int i = 0; i < M; i++)
            wallQ.add(Integer.parseInt(st.nextToken()));

        solve();
    }

    private static void solve() {
        while (K-- > 0) {
            pq.clear();
            answer = 0;

            // 회전 경우 찾기
            findRotate();
            RotateBest r = pq.poll();

            if (r.founds.isEmpty())
                break; // 아무것도 구할 수 없으면 바로 종료

            // 맵에 반영하고 정답 기록하기
            map = r.newMap;
            for (int[] yx : r.founds)
                map[yx[0]][yx[1]] = 0;
            answer += r.cost;

            while (true) {
                // 유물채우기
                refill();

                // 가치판단 && 맵에 0으로만들기 - boolean 가치판단 안되면 종료
                List<int[]> founds = new ArrayList<>();
                visited = new boolean[5][5];
                for (int i = 0; i < 5; i++) {
                    for (int j = 0; j < 5; j++) {
                        if (!visited[i][j] && map[i][j] != 0) {
                            founds.addAll(bfs(map[i][j], i, j, map));
                        }
                    }
                }

                if (founds.isEmpty())
                    break;

                answer += founds.size();
                for (int[] yx : founds) {
                    map[yx[0]][yx[1]] = 0;
                }
            }

            System.out.print(answer + " ");
        }

    }

    private static void refill() {
        for (int i = 0; i < 5; i++) {
            for (int j = 4; j >= 0; j--) {
                if (map[j][i] == 0) {
                    map[j][i] = wallQ.poll();
                }

            }
        }

    }

    private static void findRotate() {
        for (int i = 1; i < 4; i++) {
            for (int j = 1; j < 4; j++) {
                for (int k = 0; k < 3; k++) {
                    rotate(i, j, k);
                }
            }
        }
    }

    private static void rotate(int y, int x, int degree) {
        int[][] copy = new int[3][3];
		int[][] rotateArr = new int[5][5];

		for (int i = 0; i < 5; ++i) {
			for (int j = 0; j < 5; ++j)
				rotateArr[i][j] = map[i][j];
		}

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j)
				if (degree == 0) // 90도 회전
					copy[i][j] = map[3 - j + y - 2][i + x - 1];
				else if (degree == 1) // 180도 회전
					copy[i][j] = map[3 - i + y - 2][3 - j + x - 2];
				else // 270도 회전
					copy[i][j] = map[j + y - 1][3 - i + x - 2];
		}

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j)
				rotateArr[i + y - 1][j + x - 1] = copy[i][j];
		}

        // 가치 판단
        List<int[]> founds = new ArrayList<>();
        visited = new boolean[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (!visited[i][j] && rotateArr[i][j] != 0) {
                    founds.addAll(bfs(rotateArr[i][j], i, j, rotateArr));
                }
            }
        }

        pq.add(new RotateBest(founds.size(), y, x, degree, founds, rotateArr));
    }

    private static List<int[]> bfs(int color, int y, int x, int[][] tmp) {
        List<int[]> record = new ArrayList<>();
        Queue<int[]> q = new ArrayDeque<>();
        q.add(new int[] { y, x });
        record.add(new int[]{y,x});
        visited[y][x] = true;

        while (!q.isEmpty()) {
            int[] cur = q.poll();
            
            for (int i = 0; i < 4; i++) {
                int ny = cur[0] + dy[i];
                int nx = cur[1] + dx[i];

                if (inRange(ny, nx) && !visited[ny][nx] && tmp[ny][nx] == color) {
                    visited[ny][nx] = true;
                    record.add(new int[] { ny, nx }); // 경로 기록
                    q.add(new int[] { ny, nx });
                }
            }
        }

        return (record.size() >= 3) ? record : Collections.emptyList();
    }

    static boolean inRange(int y, int x) {
        return x >= 0 && y >= 0 && y < 5 && x < 5;
    }

}