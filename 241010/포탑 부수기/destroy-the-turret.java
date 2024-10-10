import java.io.*;
import java.util.*;

public class Main {

	static class P {
		int y, x;
		List<int[]> path;

		P(int y, int x, List<int[]> path) {
			this.y = y;
			this.x = x;
			this.path = path;
		}
	}

	static class Top {
		int y, x;
		int atk;
		int lastAttackTime;

		Top(int y, int x, int atk, int lastAttackTime) {
			this.y = y;
			this.x = x;
			this.atk = atk;
			this.lastAttackTime = lastAttackTime;
		}
	}

	static int[] dy = { 0, 1, 0, -1, 1, -1, 1, -1 }, dx = { 1, 0, -1, 0, 1, -1, -1, 1 };
	static int[][] map;
	static int N, M, K;
	static List<Top> tops;
	static boolean[][] visited;
	static int ans;
	static boolean[][] notAttack;

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());

		map = new int[N][M];
		tops = new ArrayList<>();

		for (int i = 0; i < N; i++) {
			String[] in = br.readLine().split(" ");
			for (int j = 0; j < M; j++) {
				map[i][j] = Integer.parseInt(in[j]);
				if (map[i][j] > 0) {
					tops.add(new Top(i, j, map[i][j], 0));
				}
			}
		}

		solve();
	}

	private static void solve() {
		for (int i = 1; i <= K; i++) {
			// 공격자 선정
			Top[] t = findAttacker();
			
			// 공격
			attack(t, i);
			
			// 포탑 남아있는지 확인
			// 포탑개수 2개 이하면 종료
			if (!isLeftTop())
				break;

			// 포탑 정비
			topRepair();
		}

		System.out.println(ans);

	}

	private static void topRepair() {
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < M; j++) {
				if (map[i][j] > 0 && !notAttack[i][j]) {
					map[i][j]++;
				}
			}
		}
		
		for (Top tp : tops) {
			int at = map[tp.y][tp.x];
			tp.atk = at;
		}
	}

	private static boolean isLeftTop() {
		int count = 0;
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < M; j++) {
				if (map[i][j] > 0) {
					count++;
					ans =  Math.max(map[i][j], ans);
				}
			}
		}
		return (count > 1);
	}

	private static void attack(Top[] t, int time) {
		notAttack = new boolean[N][M];
		// 레이저 방법 가능 여부 보기
		List<int[]> paths = bfs(t[0].y, t[0].x, t[1].y, t[1].x);
		
		int attackP = t[0].atk + N + M;
		map[t[0].y][t[0].x] = attackP;

		// 레이저 방법 공격
		if (!paths.isEmpty()) {
			for (int[] p : paths) {
				if(p[0] == t[0].y && p[1] == t[0].x) continue; //공격자라면 제외
				
				notAttack[p[0]][p[1]] = true;
				if (p[0] == t[1].y && p[1] == t[1].x) { // 공격 대상이면
					map[p[0]][p[1]] -= attackP;
				} else { // 아니면
					map[p[0]][p[1]] -= (attackP / 2);
				}
			}
		} else { // 포탄 공격시도
			map[t[1].y][t[1].x] -= attackP;

			for (int i = 0; i < 8; i++) { // 8방향 공격
				int ny = (t[1].y + dy[i]+ N) % (N);
				int nx = (t[1].x + dx[i]+ M) % (M);
				if(ny == t[0].y && nx == t[0].x) continue; //공격자 제외
				notAttack[ny][nx] = true;
				map[ny][nx] -= (attackP / 2);
			}
		}

		// 공격후 0보다 작은 친구들은 0으로 수정해주기 && tops 리스트 공격력 업데이트
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < M; j++) {
				if (map[i][j] < 0)
					map[i][j] = 0;
			}
		}

		for (Top tp : tops) {
			int at = map[tp.y][tp.x];
			tp.atk = at;
		}

		t[0].lastAttackTime = time;
		t[1].lastAttackTime = time;
	}

	private static List<int[]> bfs(int sy, int sx, int ey, int ex) {
		Queue<P> q = new ArrayDeque<>();
		visited = new boolean[N][M];
		q.add(new P(sy, sx, new ArrayList<>()));
		visited[sy][sx] = true;

		while (!q.isEmpty()) {
			P cur = q.poll();

			if (cur.y == ey && cur.x == ex) {
				return cur.path;
			}

			for (int i = 0; i < 4; i++) {
				int ny = (cur.y + dy[i] + N) % (N);
				int nx = (cur.x + dx[i]+ M) % (M);
				

				if (isValid(ny, nx)) {
					visited[ny][nx] = true;
					List<int[]> tmp = new ArrayList<>(cur.path);
					tmp.add(new int[] { ny, nx });
					q.add(new P(ny, nx, tmp));
				}

			}
		}

		return Collections.emptyList();
	}

	private static boolean isValid(int y, int x) {
		return x >=0 && y>= 0 && x < M && y < N && map[y][x] > 0 && !visited[y][x];
	}

	private static Top[] findAttacker() {
		// 약한 포탑 선정
		Collections.sort(tops, (Top t, Top o) -> {
			if (t.atk != o.atk)
				return Integer.compare(t.atk, o.atk);
			if (t.lastAttackTime != o.lastAttackTime)
				return Integer.compare(o.lastAttackTime, t.lastAttackTime);
			if (t.y + t.x != o.y + o.x)
				return Integer.compare(t.y + t.x, o.y + o.x);
			return Integer.compare(t.x, o.x);
		});

		// 공격력이 0이상인것들중에서만 선택
		Top week = null, strong = null;
		for (int i = 0; i < tops.size() - 1; i++) {
			week = tops.get(i);
			if (map[week.y][week.x] == 0)
				continue;
			else
				break;
		}

		for (int i = tops.size() - 1; i >= 1; i--) {
			strong = tops.get(i);
			if (map[strong.y][strong.x] == 0)
				continue;
			else
				break;
		}

		return new Top[] { week, strong };
	}

}