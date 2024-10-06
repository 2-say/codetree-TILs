import java.util.*;
import java.io.*;

public class Main {
    static List<Edge>[] eList;
    static int MAX_L = 30005;
    final static int MAX_V = 9_999_999;
    static Item[] items = new Item[MAX_L];
    static boolean[] visited;
    static int result; //dfs 결과 기록
    static int n; //노드 개수
    static int startN = 0;

    static class Item implements Comparable<Item> {
        int id = MAX_L;
        int rev = -1;
        int dest = -1;
        int cost = MAX_V;
        
        @Override
        public int compareTo(Item o) {
            if((rev - cost) == (o.rev - o.cost))return Integer.compare(id, o.id);
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

        for(int i = 0; i < MAX_L; i++) items[i] = new Item();

        for(int i = 0; i < Q; i++) {
            if(i == 0) {
                StringTokenizer st = new StringTokenizer(br.readLine());
                Integer.parseInt(st.nextToken());
                n = Integer.parseInt(st.nextToken());
                int m = Integer.parseInt(st.nextToken());
                
                eList = new List[n];
                for(int j = 0; j < n; j++) eList[j] = new ArrayList<>();

                //간선정보
                for(int j = 0; j < m; j++) {
                    int u = Integer.parseInt(st.nextToken());
                    int v = Integer.parseInt(st.nextToken());
                    int w = Integer.parseInt(st.nextToken());
                    //양방향 간선
                    eList[u].add(new Edge(v, w));
                    eList[v].add(new Edge(u, w));
                }
            } else {
                StringTokenizer st = new StringTokenizer(br.readLine());
                int q = Integer.parseInt(st.nextToken());

                //여행 생성
                if(q == 200) {
                    int id = Integer.parseInt(st.nextToken());
                    int rev = Integer.parseInt(st.nextToken());
                    int des = Integer.parseInt(st.nextToken());
                    items[id].id = id;
                    items[id].rev = rev;
                    items[id].dest = des;
                //상품 취소  
                } else if(q == 300) {
                    int id = Integer.parseInt(st.nextToken());

                    for(int j = 0; j < MAX_L; j++) {
                        if(items[j].id == id) {
                            items[j] = new Item();
                            break;
                        }
                    }

                //최적의 여행 상품 뽑기
                } else if(q == 400) {
                    //최적의 거리 계산하기 - 없다면 -1 출력
                    for(int j = 0; j < MAX_L; j++) {
                        if(items[j].rev != -1) {
                            result = MAX_V;
                            visited = new boolean[n];
                            dfs(items[j].dest, startN, 0);
                            items[j].cost = result;
                        } 
                    }

                    Arrays.sort(items);

                    if(items[0].id == MAX_L || items[0].cost == MAX_V) System.out.println(-1);
                    else if((items[0].rev - items[0].cost) < 0) System.out.println(-1);
                    else {
                        System.out.println(items[0].id);
                        items[0].cost = MAX_V;
                        items[0].rev = -1;
                    }
                //출발지 변경 - 거리 값이 바뀔 수 있다.
                } else if(q == 500) {
                    startN = Integer.parseInt(st.nextToken());
                }
            }

        }


    }

    static void dfs(int end, int cur, int cost) {
        if(end == cur) {
            result = Math.min(cost, result);
            return;
        }

        for(Edge next : eList[cur]) {
            if(!visited[next.v]) {
                visited[next.v] = true;
                dfs(end, next.v, cost + next.w);
                visited[next.v] = false;
            }
        }
    }
        
        
}