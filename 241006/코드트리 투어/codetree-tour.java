import java.util.*;
import java.io.*;

public class Main {
    static List<Edge>[] eList;
    static int MAX_L = 30005;
    final static int MAX_V = 9_999_999;
    static PriorityQueue<Item> items = new PriorityQueue<>();
    static Set<Integer> idSet = new HashSet<>(); // 삭제된 id를 저장하는 Set
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
            if ((rev - cost) == (o.rev - o.cost)) return Integer.compare(id, o.id);
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

        for (int i = 0; i < Q; i++) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            int query = Integer.parseInt(st.nextToken());

            // 여행 생성
            if (query == 200) {
                int id = Integer.parseInt(st.nextToken());
                int rev = Integer.parseInt(st.nextToken());
                int des = Integer.parseInt(st.nextToken());

                Item newN = new Item();
                newN.id = id;
                newN.rev = rev;
                newN.dest = des;
                items.add(newN);
                idSet.add(id); // id를 추가
            }
            // 상품 취소
            else if (query == 300) {
                int id = Integer.parseInt(st.nextToken());
                idSet.remove(id);  // Set에서 해당 id를 삭제
            }
            // 최적의 여행 상품 뽑기
            else if (query == 400) {
                // Set에 없는 원소들을 힙에서 제거
                while (!items.isEmpty() && !idSet.contains(items.peek().id)) {
                    items.poll();
                }

                if (!items.isEmpty()) {
                    Item it = items.poll();

                    if ((it.rev - it.cost) > 0) {
                        System.out.println(-1);
                        items.add(it);  // 다시 추가
                    } else {
                        System.out.println(it.id);
                    }
                } else {
                    System.out.println(-1);
                }
            }
            // 출발지 변경
            else if (query == 500) {
                startN = Integer.parseInt(st.nextToken());
                if(dist != null)
                    Arrays.fill(dist, MAX_V);
            }
        }
    }

    // 다익스트라 알고리즘
    static void dijkstra() {
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