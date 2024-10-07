import java.io.*;
import java.util.*;

public class Main {
    static int[][] map = new int[5][5];
    static int[] wall;
    static int lastIdx = 0; 
    static boolean[][] visited;
    static PriorityQueue<RotateGoal> pq;
    static int answer = 0;

    static class RotateGoal implements Comparable<RotateGoal> {
        int cost;
        int degree;
        int y; 
        int x;
        List<int[]> points;

        RotateGoal(int cost, int degree, int y, int x, List<int[]> points) {
            this.cost = cost;
            this.degree = degree;
            this.y = y;
            this.x = x;
            this.points = points;
        }

        @Override
        public int compareTo(RotateGoal o) {
            if (o.cost != cost) return Integer.compare(o.cost, cost);
            if (degree != o.degree) return Integer.compare(degree, o.degree);
            if (x != o.x) return Integer.compare(x, o.x);
            return Integer.compare(y, o.y);
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        int K = Integer.parseInt(st.nextToken());
        int M = Integer.parseInt(st.nextToken());

        for (int i = 0; i < 5; i++) {
            map[i] = Arrays.stream(br.readLine().split(" ")).mapToInt(Integer::parseInt).toArray();
        }
        wall = Arrays.stream(br.readLine().split(" ")).mapToInt(Integer::parseInt).toArray();

        solve(K);
    }

    static void solve(int k) {
        while (k-- > 0) {
            pq = new PriorityQueue<>();
            answer = 0;

            if (!adventure()) break;

            while (true) {
                for (int j = 0; j < 5; j++) {
                    for (int i = 4; i >= 0; i--) {
                        if (map[i][j] == -1) {
                            map[i][j] = wall[lastIdx++];
                        }
                    }
                }

                List<int[]> removable = new ArrayList<>();
                visited = new boolean[5][5];

                for (int i = 0; i < 5; i++) {
                    for (int j = 0; j < 5; j++) {
                        if (!visited[i][j]) {
                            removable.addAll(dfs(i, j, map[i][j], map));
                        }
                    }
                }

                if (removable.isEmpty()) break;
                answer += removable.size();
                for (int[] p : removable) map[p[0]][p[1]] = -1; // Empty space
            }

            System.out.print(answer + " ");
        }
    }

    static boolean adventure() {
        for (int i = 1; i < 4; i++) {
            for (int j = 1; j < 4; j++) {
                int[][] tmp = new int[5][5];
                for (int k = 0; k < 5; k++) {
                    tmp[k] = map[k].clone();
                }
                for (int degree = 0; degree < 3; degree++) {
                    calculatePrice(degree, i, j, tmp);
                }
            }
        }

        RotateGoal r = pq.poll();
        if (r == null) return false;

        rotate(map, r.degree, r.y, r.x);
        answer += r.cost;
        for (int[] p : r.points) map[p[0]][p[1]] = -1; // Empty space
        return answer > 0;
    }

    static void calculatePrice(int degree, int y, int x, int[][] tmp) {
        rotate(tmp, degree, y, x);

        List<int[]> all = new ArrayList<>();
        visited = new boolean[5][5];

        int result = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (!visited[i][j]) {
                    List<int[]> lit = dfs(i, j, tmp[i][j], tmp);
                    result += lit.size();
                    all.addAll(lit);
                }
            }
        }
        pq.add(new RotateGoal(result, degree, y, x, all));
    }

    static List<int[]> dfs(int y, int x, int color, int[][] map) {
        List<int[]> points = new ArrayList<>();
        Stack<int[]> stack = new Stack<>();
        stack.push(new int[]{y, x});
        visited[y][x] = true;

        while (!stack.isEmpty()) {
            int[] cur = stack.pop();
            points.add(cur);

            int[] dx = {1, -1, 0, 0};
            int[] dy = {0, 0, 1, -1};

            for (int i = 0; i < 4; i++) {
                int nY = cur[0] + dy[i];
                int nX = cur[1] + dx[i];

                if (nY >= 0 && nX >= 0 && nY < 5 && nX < 5 && !visited[nY][nX] && map[nY][nX] == color) {
                    visited[nY][nX] = true;
                    stack.push(new int[]{nY, nX});
                }
            }
        }
        return points.size() >= 3 ? points : Collections.emptyList();
    }

    static void rotate(int[][] arr, int degree, int y, int x) {
        int[][] copy = new int[3][3];

        if (degree == 0) { // 90 degrees
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    copy[j][2 - i] = arr[y - 1 + i][x - 1 + j];
                }
            }
        } else if (degree == 1) { // 180 degrees
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    copy[2 - i][2 - j] = arr[y - 1 + i][x - 1 + j];
                }
            }
        } else if (degree == 2) { // 270 degrees
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    copy[2 - j][i] = arr[y - 1 + i][x - 1 + j];
                }
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                arr[y - 1 + i][x - 1 + j] = copy[i][j];
            }
        }
    }
}