import java.util.*;

public class Main {

    public static final int MAX_N = 31;
    public static final int MAX_L = 41;

    static int l,n,q;
    static int[][] info = new int[MAX_L][MAX_L];
    static int[] bef_k = new int[MAX_N];
    static int[] r = new int[MAX_N];
    static int[] c = new int[MAX_N];
    static int[] h = new int[MAX_N];
    static int[] w = new int[MAX_N];
    static int[] k = new int[MAX_N];
    static int[] nr = new int[MAX_N], nc = new int[MAX_N];
    static int[] dmg = new int[MAX_N];
    static boolean[] is_moved = new boolean[MAX_N];

    static int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};

    
    public static boolean tryMovement(int idx, int dir) {
        Queue<Integer> q = new LinkedList<>();
        boolean is_pos = true;

        //해당 조각에 좌표로 초기화
        for(int i = 1; i <= n; i++) {
            dmg[i] = 0;
            is_moved[i] = false;
            nr[i] = r[i];
            nc[i] = c[i];
        }
        
        q.add(idx);
        is_moved[idx] = true;

        while(!q.isEmpty()) {
            int x = q.poll();

            nr[x] += dx[dir];
            nc[x] += dy[dir];

            //경계를 벗어나는지 체크합니다.
            if(nr[x] < 1 || nc[x] < 1|| nr[x] + h[x] - 1 > l || nc[x] + w[x] - 1 > l) return false;

            //좌측서부터 기사 크기만큼 순회
            for(int i = nr[x]; i <= nr[x] + h[x] - 1; i++) {
                for(int j = nc[x]; j <= nc[x] + w[x] - 1; j++) {
                    if(info[i][j] == 1) dmg[x]++;
                    if(info[i][j] == 2) return false;
                }
            }


            // 다른 조각과 충돌하는 경우, 해당 조각도 같이 이동
            for(int i = 1; i <= n; i++) {
                if(is_moved[i] || k[i] <= 0)  continue;
                if(r[i] > nr[x] + h[x] - 1 || nr[x] > r[i] + h[i] - 1) continue;
                if(c[i] > nc[x] + w[x] - 1 || nc[x] > c[i] + w[i] - 1) continue;

                is_moved[i] = true;
                q.add(i);
            }
        }

        dmg[idx] = 0;
        return true;
    }


    //특정 조각을 지정된 방향으로 이동시키는 함수
    static void movePiece(int idx, int dir) {
        if(k[idx] <= 0) return; //체력이 0보다 작으면 종료

        // 이동이 가능한 경우, 실제 위치와 체력을 업데이트 한다.
        if(tryMovement(idx, dir)) {
            for(int i = 1; i <= n; i++) {
                r[i] = nr[i];
                c[i] = nc[i];
                k[i] -= dmg[i];
            }
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        //입력값을 받습니다.
        l = sc.nextInt();
        n = sc.nextInt(); 
        q = sc.nextInt();

        for(int i = 1 ; i <= l; i++) {
            for(int j = 1; j <= l; j++) {
                info[i][j] = sc.nextInt();
            }
        }

        for(int i = 1; i <= n; i++) {
            r[i] = sc.nextInt();
            c[i] = sc.nextInt();
            h[i] = sc.nextInt();
            w[i] = sc.nextInt();
            k[i] = sc.nextInt();
            bef_k[i] = k[i];
        }

        for(int i = 1; i <= q; i++) {
            int idx = sc.nextInt();
            int dir = sc.nextInt();
            movePiece(idx, dir);
        }

        long ans = 0;
        for(int i = 0; i <= n; i++) {
            if(k[i] > 0) {
                ans += bef_k[i] - k[i];
            }
        }
        
        System.out.println(ans);
    }
}