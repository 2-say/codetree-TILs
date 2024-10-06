import java.io.*;
import java.util.*;

public class Main {
    static int[][] map = new int[5][5];
    static int[] wall; //벽면에 적혀있는 수
    static int lastIdx = 0; //벽면 적혀있는 수 마지막 인덱스 기록
    static boolean[][] visited;
    static PriorityQueue<RoatateGoal> pq;
    static int answer = 0;

    static class RoatateGoal implements Comparable<RoatateGoal> {
        int cost;
        int degree;
        int y; 
        int x;
        List<int[]> points; //유적모음된 좌표 모음

        RoatateGoal(int cost, int degree, int y, int x, List<int[]> points) {
            this.cost = cost;
            this.degree = degree;
            this.y = y;
            this.x = x;
            this.points = points;
        }

        @Override
        public int compareTo(RoatateGoal o) {
            if(o.cost == cost) {
                if(degree == o.degree) {
                    if(x == o.x) return Integer.compare(y, o.y);
                    return Integer.compare(x, o.x);
                }
                return Integer.compare(degree, o.degree);
            }
            return Integer.compare(o.cost, cost);
        }

        @Override
        public String toString() {
            return "(" + cost + " " + degree + " " + y + " " + x + ")";
        }
    }


    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        int K = Integer.parseInt(st.nextToken());
        int M = Integer.parseInt(st.nextToken());

        for(int i = 0; i < 5; i++) {
            map[i] = Arrays.stream(br.readLine().split(" ")).mapToInt(Integer::parseInt).toArray();
        }
        wall = Arrays.stream(br.readLine().split(" ")).mapToInt(Integer::parseInt).toArray();

        solve(K);
    }

    static void solve(int k) {
        while(k-- > 0) {
            pq = new PriorityQueue<>();
            answer = 0;

            //탐사 진행
            if(!adventure()) break; //각 좌표에서 90~270까지 돌려보고 제일 적은 값을 찾는다.

            //유적 채우고 && 획득 연속
            while(true) {
                // 주의 할 사항 wall lastIdx++ 넘어갈 수 있음
                for(int j = 0; j < 5; j++) { //유적채우기 
                    for(int i = 4; i >= 0; i--) {
                        if(map[i][j] == -1) {
                            map[i][j] = wall[lastIdx++];
                        }
                    }
                }

                //연쇄 해봄
                List<int[]> all = new ArrayList<>();
                visited = new boolean[5][5];
                for(int i = 0; i < 5; i++) {
                    for(int j = 0; j < 5; j++) {
                        if(!visited[i][j]) {
                            all.addAll(dfs(i, j, map[i][j], map));
                        }
                    }
                }

                if(all.isEmpty()) break; //만약 제거할 수 있는 유물이 없으면 종료
                answer += all.size();
                for(int[] p : all) map[p[0]][p[1]] = -1; //빈칸 만들기
            }

            System.out.print(answer + " ");
        }
    }

    static boolean adventure() {
        for(int i = 1; i < 4; i++) {  //회전시키기 위해 중점들 순회
            for(int j = 1; j < 4; j++) {
                int[][] tmp = new int[5][5]; //복사본 생성
                for(int k = 0; k < 5; k++) tmp[k] = map[k].clone();
                for(int k = 0; k < 3; k++) { ////1번 돌리면 90 ~ 270
                    calPrice(k, i, j, tmp); //해당 좌표와 각도로 가치 계산하기
                }
            }   
        }

        //System.out.println(pq.toString());
        RoatateGoal r = pq.poll(); ////선택된 방법

        rotate(map, r.degree, r.y, r.x);
        answer += r.cost;
        for(int[] p : r.points) map[p[0]][p[1]] = -1; ////빈칸으로 만들기
        if(answer == 0) return false; //선택된 방법 결과가 0이라면 얻은게 없다는 뜻으로 종료
        return true;
    }

    static void calPrice(int degree, int y, int x, int[][] tmp) { ////해당 맵으로 가치를 계산해본다.
        rotate(tmp, degree, y, x);
        
        int result = 0;
        List<int[]> lit;
        List<int[]> all = new ArrayList<>();
        visited = new boolean[5][5];
        //가치 판단
        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 5; j++) {
                if(!visited[i][j]) {
                    lit = dfs(i, j, tmp[i][j], tmp);
                    result += lit.size();
                    all.addAll(lit);
                }
            }
        }
        pq.add(new RoatateGoal(result, degree, y, x, all));
    }

    static List<int[]> dfs(int y, int x, int color, int[][] tmp) {
        List<int[]> points = new ArrayList<>();
        int[] dx = {1, -1, 0, 0}, dy = {0, 0, 1, -1};
        Stack<int[]> stac = new Stack<>();
        points.add(new int[]{y, x});
        visited[y][x] = true;
        stac.add(new int[]{y, x});

        while(!stac.isEmpty()) {
            int[] cur = stac.pop();

            for(int i = 0; i < 4; i++) {
                int nY = cur[0] + dy[i];
                int nX = cur[1] + dx[i];

                if(nY >= 0 && nX >= 0 && nY < 5 && nX < 5 && !visited[nY][nX] && tmp[nY][nX] == color) {
                    visited[nY][nX] = true;
                    stac.add(new int[]{nY, nX});
                    points.add(new int[]{nY, nX});
                }
            }
        }
        return (points.size() >= 3) ? points : new ArrayList<>();
    }


    // 90도, 180도, 270도 회전 구현
    static void rotate(int[][] arr, int degree, int y, int x) {
        int[][] copy = new int[3][3];

        // 90도 회전
        if (degree == 0) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    copy[j][2 - i] = arr[y - 1 + i][x - 1 + j];
                }
            }
        }
        // 180도 회전
        else if (degree == 1) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    copy[2 - i][2 - j] = arr[y - 1 + i][x - 1 + j];
                }
            }
        }
        // 270도 회전
        else if (degree == 2) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    copy[2 - j][i] = arr[y - 1 + i][x - 1 + j];
                }
            }
        }
        
        // 회전된 배열을 원래 배열에 적용
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                arr[y - 1 + i][x - 1 + j] = copy[i][j];
            }
        }
    }

}