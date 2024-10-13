import java.io.*;
import java.util.*;

public class Main {
	
	static int n, m, k, c, ans;
	static int[][] map, kill, tmp; // 숲, 제초제 위치, 옮길 상자
	static int[] dx = {-1, 1, 0, 0, 1, 1, -1, -1}, dy= { 0, 0, 1, -1, -1, 1, 1, -1}; //동서남북, 대각선4방
	static Point choice;
	
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        
        StringTokenizer st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        k = Integer.parseInt(st.nextToken());
        c = Integer.parseInt(st.nextToken()) + 1; //1년은 유지해야 하기 때문에 1을 더해줬다.(그냥 숫자로 적으면 0년차에 같이지워짐)
        
        map = new int[n][n];
        for (int i = 0; i < n; i++) {
        	st = new StringTokenizer(br.readLine());
        	for (int j = 0; j < n; j++)
        		map[i][j] = Integer.parseInt(st.nextToken());
        }
        
        kill = new int[n][n];
        tmp = new int[n][n];
        
        for (int i = 0; i < m; i++) {
        	check();
        	grow();
        	move();
        	select();
        	if (choice.x == -1) continue;
        	killing();	
        }
        
        System.out.println(ans);
    }
    
    public static void check() {//제초제 체크해주는 함수
    	for (int i = 0; i < n; i++) //제초제 시간 체크
    		for (int j = 0; j < n; j++)
    			if (kill[i][j] > 0)
    				kill[i][j]--;
    	
    	for (int i = 0; i < n; i++) //제초제 유효기간 끝
    		for (int j = 0; j < n; j++)
    			if (map[i][j] == -2 && kill[i][j] == 0)
    				map[i][j] = 0;
    }
    
    public static void grow() { // 나무 성장 함수
    	for (int i = 0; i < n; i++) {
    		for (int j = 0; j < n; j++) {
    			if (map[i][j] > 0) { //나무인 경우
    				int cnt = 0;
    				for (int p = 0; p < 4; p++) {
    					int nx = i + dx[p], ny = j + dy[p];
    					if (range(nx, ny) && map[nx][ny] > 0)
    						cnt++;
    				}
    				map[i][j] += cnt; 
    			}
    		}
    	}
    }
    
    public static void move() { //나무 번식 함수
    	copy(tmp, map); //값이 오염되는 것을 막기 위해 tmp 사용
    	
    	for (int i = 0; i < n; i++) {
    		for (int j = 0; j < n; j++) {
    			if (map[i][j] > 0) { //나무인 경우
    				int cnt = 0;
    				for (int p = 0; p < 4; p++) {
    					int nx = i + dx[p], ny = j + dy[p];
    					if (range(nx, ny) && map[nx][ny] == 0) //번식할 수 있는 곳 체크
    						cnt++;
    				}
    				
    				for (int p = 0; p < 4; p++) {
    					int nx = i + dx[p], ny = j + dy[p];
    					if (range(nx, ny) && map[nx][ny] == 0) //번식할 수 있는 곳에 너무 넣어줌
    						tmp[nx][ny] += (map[i][j] / cnt); //여러 곳에서 같은 빈칸에 나무를 넣을 수 있기 때문에 += 사용
    				}
    			}
    		}
    	}
    	copy (map, tmp);
    }
    
    public static void select() { //제초제 좌표 정하는 함수
    	tmp = new int[n][n];
    	
    	int mx = -1;//0으로 하면 나무 없는 경우 측정 불가
    	for (int i = 0; i < n; i++) {
    		for (int j = 0; j < n; j++) {
    			if (map[i][j] > 0) { //나무인 경우
    				int cnt = map[i][j];
    				for (int p = 4; p < 8; p++) { //대각선 4방
    					for (int q = 1; q <= k; q++) { //범위 k만큼
    						int nx = i + dx[p] * q, ny = j + dy[p] * q;
    						if (range(nx, ny)) {
    							if (map[nx][ny] > 0) //나무인 경우에만
    								cnt += map[nx][ny]; //나무 카운트
    							else 
    								break; //그 외 장애물(벽, 제초제 뿌려져 있는 경우, 빈칸)의 경우에는 계산 안하고 바로 그 방향으로 탐색 중단
    						}
    					}
    				}
    				tmp[i][j] = cnt; //tmp 배열에 각 칸의 나무일 때 그 자리에 제초제 뿌렸을 때 박멸할 수 있는 나무를 적어준다
    				mx = Math.max(mx, cnt); //그 중 최댓값 저장
    			}
    		}
    	}

    	List<Point> list = new ArrayList<>();
    	
    	for (int i = 0; i < n; i++)
    		for (int j = 0; j < n; j++)
    			if (tmp[i][j] == mx) //최댓값이 여러 개인 경우
    				list.add(new Point(i, j)); // 그 좌표들을 전부 저장
    	
    	if (list.isEmpty()) {//죽일 것이 없는 경우
    		choice = new Point(-1, -1);
    		return;
    	}
    	
    	Collections.sort(list); //그 좌표의 우선순위를 위해 정렬
    	choice = new Point(list.get(0).x, list.get(0).y); //제초제를 뿌렸을 때 가장 많이 박멸되는 칸의 좌표
    }
    
    public static void killing() { //선택된 좌표로 제초제를 뿌리는 함수
    	int x = choice.x, y = choice.y; //좌표
    	
    	copy(tmp, map);
    	
    	//좌표값 처리
    	ans += tmp[x][y]; //박멸할 나무에 더하고
	    tmp[x][y] = -2; //제초제 뿌렸다는 표시
	    kill[x][y] = c; //연(year) 수 저장
    	
		for (int p = 4; p < 8; p++) { //대각선 4방
			for (int q = 1; q <= k; q++) { //확장 범위 k~
				int nx = x + dx[p] * q, ny = y + dy[p] * q;
				if (range(nx, ny)) {
					if (map[nx][ny] >= 0) {//나무가 0~개인 경우
						ans += map[nx][ny];
						tmp[nx][ny] = -2;
						kill[nx][ny] = c;
						if (map[nx][ny] == 0) //0인 경우(아예 없는 경우)는 제초제는 뿌리돼 멈춰야 함
							break;
					}
					else {
						if (map[nx][ny] == -2) //제초제가 이미 뿌려져있는 경우
							kill[nx][ny] = c; //값은 바뀌지만
						break; //더 확장되지는 않음
					}
				}
			}
		}
			
		copy(map, tmp);
    }
    
    public static void copy(int[][] a, int[][] b) {
    	for (int i = 0; i < n; i++)
    		for (int j = 0; j < n; j++)
    			a[i][j] = b[i][j];
    }
    
    public static boolean range(int nx, int ny) {
    	if (nx >= 0 && nx < n && ny >=0 && ny < n)
    		return true;
    	return false;
    }
    
    static class Point implements Comparable<Point>{
    	int x, y;
    	
    	public Point(int x, int y) {
    		this.x = x;
    		this.y = y;
    	}

		@Override
		public int compareTo(Point o) {// 문제조건인 "만약 박멸시키는 나무의 수가 동일한 칸이 있는 경우에는 행이 작은 순서대로, 만약 행이 같은 경우에는 열이 작은 칸에 제초제를 뿌리게 됩니다." 구현
			if (o.x == x)
				return y - o.y;
			return x - o.x;
		}
    }
}