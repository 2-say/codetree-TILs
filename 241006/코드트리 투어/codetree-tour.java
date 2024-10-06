import java.util.*;

public class Main {
    final static int INF = 0x7fffffff; // INT 최대값을 정의합니다
    final static int MAX_N = 2000; // 코드트리 랜드의 최대 도시 개수입니다
    final static int MAX_ID = 30005; // 여행상품 ID의 최대값입니다

    static int N, M; // 도시의 개수 N과 간선의 개수 M 입니다
    static int[][] A = new int[MAX_N][MAX_N]; // 코드트리 랜드의 간선을 인접 행렬로 저장합니다
    static int[] D = new int[MAX_N]; // Dijkstra 알고리즘을 통해 시작도시 S부터 각 도시까지의 최단경로를 저장합니다
    static boolean[] isMade = new boolean[MAX_ID]; // index에 해당하는 ID를 갖는 여행상품이 만들어진적 있는지 저장합니다.
    static boolean[] isCancel = new boolean[MAX_ID]; // index에 해당하는 ID를 갖는 여행상품이 취소되었는지 저장합니다
    static int S; // 여행 상품의 출발지 입니다

    // 여행 상품을 정의합니다
    static class Package implements Comparable<Package> {
        int id; // 고유 식별자 ID
        int revenue; // 매출
        int dest; // 도착도시
        int profit; // 여행사가 벌어들이는 수익

        public Package(int id, int revenue, int dest, int profit) {
            this.id = id;
            this.revenue = revenue;
            this.dest = dest;
            this.profit = profit;
        }

        // 우선순위 큐 비교를 위한 compareTo 메서드를 오버라이드합니다
        @Override
        public int compareTo(Package other) {
            if (this.profit == other.profit) {
                return Integer.compare(this.id, other.id); // profit이 같으면 id가 작은 순으로
            }
            return Integer.compare(other.profit, this.profit); // profit이 클수록 우선 순위 높게
        }
    }

    static PriorityQueue<Package> pq = new PriorityQueue<>(); // 최적의 여행 상품을 찾기 위한 우선순위 큐를 사용합니다

    // dijkstra 알고리즘을 통해 시작도시 S에서 각 도시로 가는 최단거리를 구합니다.
    static void dijkstra() {
        boolean[] visit = new boolean[N];
        Arrays.fill(D, INF);
        D[S] = 0;

        for (int i = 0; i < N - 1; i++) {
            int v = 0, minDist = INF;
            for (int j = 0; j < N; j++) {
                if (!visit[j] && minDist > D[j]) {
                    v = j;
                    minDist = D[j];
                }
            }
            visit[v] = true;
            for (int j = 0; j < N; j++) {
                if (!visit[j] && D[v] != INF && A[v][j] != INF && D[j] > D[v] + A[v][j]) {
                    D[j] = D[v] + A[v][j];
                }
            }
        }
    }

    // 코드트리랜드를 입력받고
    // 주어진 코드트리 랜드를 인접행렬에 저장합니다
    static void buildLand(Scanner sc) {
        N = sc.nextInt();
        M = sc.nextInt();
        for (int i = 0; i < N; i++) {
            Arrays.fill(A[i], INF);
            // 출발지와 도착지가 같은 경우 거리는 0입니다
            A[i][i] = 0;
        }
        for (int i = 0; i < M; i++) {
            int u = sc.nextInt();
            int v = sc.nextInt();
            int w = sc.nextInt();
            // 양방향 간선에 대해 두 도시간 여러 간선이 주어질 수 있으므로 min 값으로 저장합니다
            A[u][v] = Math.min(A[u][v], w);
            A[v][u] = Math.min(A[v][u], w);
        }
    }

    // 여행 상품을 추가합니다
    // 추가된 여행상품은 priority queue안에도 들어가야합니다.
    static void addPackage(int id, int revenue, int dest) {
        isMade[id] = true;
        int profit = revenue - D[dest];
        pq.offer(new Package(id, revenue, dest, profit));
    }

    // id에 해당하는 여행상품이 취소되었음을 기록합니다
    static void cancelPackage(int id) {
        // 만들어진적 있는 여행상품에 대해서만 취소할 수 있습니다.
        if (isMade[id]) isCancel[id] = true;
    }

    // 최적의 여행상품을 판매합니다
    static int sellPackage() {
        while (!pq.isEmpty()) {
            Package p = pq.peek();
            // 최적이라고 생각한 여행 상품이 판매 불가능 하다면 while문을 빠져나가 -1을 반환합니다.
            if (p.profit < 0) {
                break;
            }
            pq.poll();
            if (!isCancel[p.id]) {
                return p.id; // 해당 여행 상품이 취소되지 않았다면 정상 판매되므로 id를 반환합니다
            }
        }
        return -1;
    }

    // 변경할 시작도시를 입력받고 변경됨에 따른 기존 여행상품 정보들을 수정합니다.
    static void changeStart(Scanner sc) {
        S = sc.nextInt(); // 변경된 시작도시를 반영합니다
        dijkstra(); // 시작도시가 변경됨에 따라 각 도시로 가는 최단경로를 갱신합니다
        List<Package> packages = new ArrayList<>();
        // 기존의 여행상품들을 packages에 기록하며 priority queue에서 삭제합니다
        while (!pq.isEmpty()) {
            packages.add(pq.poll());
        }
        // 기존의 여행 상품들의 profit을 수정하여 새로이 priority queue에 넣습니다
        for (Package p : packages) {
            addPackage(p.id, p.revenue, p.dest);
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int Q = sc.nextInt();
        // 총 Q개의 쿼리를 입력받습니다
        while (Q-- > 0) {
            int T = sc.nextInt();
            // 쿼리의 종류에 따라 필요한 함수들을 호출하여 처리합니다
            switch (T) {
            case 100:
                buildLand(sc);
                dijkstra();
                break;
            case 200:
                int id = sc.nextInt();
                int revenue = sc.nextInt();
                int dest = sc.nextInt();
                addPackage(id, revenue, dest);
                break;
            case 300:
                int cancelId = sc.nextInt();
                cancelPackage(cancelId);
                break;
            case 400:
                System.out.println(sellPackage());
                break;
            case 500:
                changeStart(sc);
                break;
            }
        }
        sc.close();
    }
}