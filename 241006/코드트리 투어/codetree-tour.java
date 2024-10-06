import java.util.*;
import java.io.*;

public class Main {
    static List<Edge>[] eList;
    static int MAX_L = 30005;
    final static int MAX_V = 9_999_999;
    static PriorityQueue<Item> items = new PriorityQueue<>();
    static int[] dist; // 다익스트라 결과 기록
    static int n; // 노드 개수
    static int startN = 0;

    static class Item implements Comparable<Item> {
        int id = MAX_L;
        int rev = -1;
        int dest = -1;
        int cost = MAX_V;
        
        @Override
        public int compareTo(Item o) {
            if((rev - cost) == (o.rev - o.cost)) return Integer.compare(id, o.id);
            return Integer.compare(o.rev - o.cost, rev - cost);
        }

        @Override
        public String toString() {
            return "(" + id +  " "  + rev +  " " +  dest + " " + cost + ")";
        }
    }

    static class Edge {
        int v;
        int w;

        Edge(int v, int w) {
            this.v = v;
            this.w = w;
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int Q = Integer.parseInt(br.readLine());

        for(int i = 0; i < Q; i++) {
            if(i == 0) {
                StringTokenizer st = new StringTokenizer(br.readLine());
                Integer.parseInt(st.nextToken());
                n = Integer.parseInt(st.nextToken());
                int m = Integer.parseInt(st.nextToken());
                
                eList = new List[n];
                for(int j = 0; j < n; j++) eList[j] = new ArrayList<>();

                // 간선정보
                for(int j = 0; j < m; j++) {
                    int u = Integer.parseInt(st.nextToken());
                    int v = Integer.parseInt(st.nextToken());
                    int w = Integer.parseInt(st.nextToken());
                    // 양방향 간선
                    eList[u].add(new Edge(v, w));
                    eList[v].add(new Edge(u, w));
                }
            } else {
                StringTokenizer st = new StringTokenizer(br.readLine());
                int q = Integer.parseInt(st.nextToken());

                // 여행 생성
                if(q == 200) {
                    int id = Integer.parseInt(st.nextToken());
                    int rev = Integer.parseInt(st.nextToken());
                    int des = Integer.parseInt(st.nextToken());
                    
                    Item newN = new Item();
                    newN.id = id;
                    newN.rev = rev;
                    newN.dest = des;
                    items.add(newN);
                // 상품 취소  
                } else if(q == 300) {
                    int id = Integer.parseInt(st.nextToken());
                    
                    PriorityQueue<Item> tmp = new PriorityQueue<>();

                    while(!items.isEmpty()) {
                        Item it = items.poll();
                        if(it.id != id)
                            tmp.add(it);
                    }
                    items = tmp;
                // 최적의 여행 상품 뽑기
                } else if(q == 400) {
                    // 다익스트라로 한 번만 최단 경로 계산
                    dijkstra();
                    
                    PriorityQueue<Item> tmp = new PriorityQueue<>();

                    while(!items.isEmpty()) {
                        Item it = items.poll();
                        if(it.rev != -1) {
                            // 이미 계산된 다익스트라 결과로 비용 설정
                            it.cost = dist[it.dest];
                        }
                        tmp.add(it);
                    }
                    items = tmp;

                    if(!items.isEmpty()) {
                        Item it = items.peek();

                        if(it == null || it.id == MAX_L || it.cost == MAX_V || (it.rev - it.cost) < 0) { 
                            System.out.println(-1);
                        } else System.out.println(items.poll().id);
                    } else System.out.println(-1);
                // 출발지 변경 - 다익스트라 다시 수행 필요
                } else if(q == 500) {
                    startN = Integer.parseInt(st.nextToken());
                    // 출발지가 바뀌었으므로, 다익스트라 결과 무효화
                    if(dist != null)
                        Arrays.fill(dist, MAX_V);
                }
            }
        }
    }

    // 다익스트라 알고리즘
    static void dijkstra() {
        // 이미 계산된 경우 건너뜀
        if (dist != null && dist[startN] != MAX_V) return;

        dist = new int[n];
        Arrays.fill(dist, MAX_V);
        PriorityQueue<Edge> pq = new PriorityQueue<>((a, b) -> Integer.compare(a.w, b.w));
        dist[startN] = 0;
        pq.add(new Edge(startN, 0));

        while (!pq.isEmpty()) {
            Edge cur = pq.poll();
            int v = cur.v;
            int w = cur.w;

            if (w > dist[v]) continue;

            for (Edge next : eList[v]) {
                if (dist[next.v] > dist[v] + next.w) {
                    dist[next.v] = dist[v] + next.w;
                    pq.add(new Edge(next.v, dist[next.v]));
                }
            }
        }
    }
}