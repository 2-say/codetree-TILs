import java.io.*;
import java.util.*;

public class Main {

	static int n, m;
	static int[][] map;
	static boolean[][] notMove;
	static int[] dy = { -1, 0, 0, 1 }, dx = { 0, -1, 1, 0 };
	static int afterGoBasecampC = 0;

	static List<P> stores = new ArrayList<>();
	static List<P> persons = new ArrayList<>(); // 0번부터 ~ m-1번까지 유지

	static class P {
		int y, x;
		int moveTime = -1;
		boolean arrive = false;

		P(int y, int x) {
			this.y = y;
			this.x = x;
		}
	}

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		n = Integer.parseInt(st.nextToken());
		m = Integer.parseInt(st.nextToken()); // 사람의 수

		map = new int[n][n];
		notMove = new boolean[n][n];

		for (int i = 0; i < n; i++) {
			String[] in = br.readLine().split(" ");
			for (int j = 0; j < n; j++) {
				map[i][j] = Integer.parseInt(in[j]);
			}
		}

		for (int i = 0; i < m; i++) {
			st = new StringTokenizer(br.readLine());
			int y = Integer.parseInt(st.nextToken()) - 1;
			int x = Integer.parseInt(st.nextToken()) - 1;
			stores.add(new P(y, x));
		}

		for (int t = 1; t <= m; t++) { // 사람들 순차적으로 넣기
			P person = new P(-1, -1);
			person.moveTime = t;
			persons.add(person);
		}

		solve();
	}

	static void solve() {
		int time = 0;
		while (true) {
			time++;

			// 사람들 한명씩 순회
			for (int i = 0; i < m; i++) {
				P p = persons.get(i);
				P s = stores.get(i);
				// 1. 사람들 move
				movePerson(p, s, time);
			}
		
			
			for(int i = 0; i < m; i++) {
				P p = persons.get(i);
				P s = stores.get(i);
				checkArrive(p, s); //편의점 이동 처리
			}
			
			
			for(int i = 0; i < m; i++) {
				P p = persons.get(i);
				P s = stores.get(i);
				if (afterGoBasecampC < m) // 모두 베이스캠프 이동하면 함수 실행 x
					goBasecamp(p, s, time);
			}
			

			// 모두 도착했는지 파악 종료 조건
			if (checkAllArrive())
				break;

		}

		System.out.println(time);
	}

	private static boolean checkAllArrive() {
		for (int i = 0; i < m; i++) {
			P p = persons.get(i);
			P s = stores.get(i);
			if (p.y != s.y || p.x != s.x)
				return false;
		}
		return true;
	}

	private static void goBasecamp(P p, P s, int time) {
		if (p.y == -1 && p.x == -1 && time >= p.moveTime) { // 베이스 캠프 이동 가능 조건
			// 거리가까운 베이스캠프자리 찾기
			int[] yx = bfs(s); // 출발지는 해당 편의점 도착지는 가까운 1 -> visited가 가능한곳

			// 이동처리
			p.y = yx[0];
			p.x = yx[1];
			notMove[p.y][p.x] = true;
			afterGoBasecampC++;
		}
	}

	private static int[] bfs(P s) {
		boolean[][] visited = new boolean[n][n]; // 이게 순회할 때 필요한 visited 해깔림 주의
		Queue<P> q = new ArrayDeque<>();
		q.add(s);
		visited[s.y][s.x] = true;

		while (!q.isEmpty()) {
			P cur = q.poll();
			if (map[cur.y][cur.x] == 1)
				return new int[] { cur.y, cur.x };

			for (int i = 0; i < 4; i++) {
				int ny = cur.y + dy[i];
				int nx = cur.x + dx[i];

				if (inRange(ny, nx) && !visited[ny][nx] && !notMove[ny][nx]) {
					visited[ny][nx] = true;
					q.add(new P(ny, nx));
				}
			}
		}

		return null;
	}

	private static void checkArrive(P p, P s) {
		if (p.y == s.y && p.x == s.x) {
			notMove[p.y][p.x] = true;
			p.arrive = true;
		}
	}

	static void movePerson(P p, P s, int time) {
		if (p.y != -1 && p.x != -1 && !p.arrive) {
			int[] yx = foundMoveClose(p, s);
			p.y = yx[0]; // 이동 처리
			p.x = yx[1];
		}
	}

	private static int[] foundMoveClose(Main.P p, Main.P store) {
		int minD = Integer.MAX_VALUE;
		for (int i = 0; i < 4; i++) {
			int ny = p.y + dy[i];
			int nx = p.x + dx[i];

			if (!inRange(ny, nx) || notMove[ny][nx]) // 이동 불가지역은 못감
				continue;

			int distance = Math.abs(ny - store.y) + Math.abs(nx - store.x);
			minD = Math.min(distance, minD);
		}

		for (int i = 0; i < 4; i++) {
			int ny = p.y + dy[i];
			int nx = p.x + dx[i];

			if (!inRange(ny, nx) || notMove[ny][nx])
				continue;

			int distance = Math.abs(ny - store.y) + Math.abs(nx - store.x);
			if (minD == distance) {
				return new int[] { ny, nx };
			}
		}

		return null; // 없음 표시
	}

	private static boolean inRange(int y, int x) {
		return y >= 0 && x >= 0 && y < n && x < n;
	}

}