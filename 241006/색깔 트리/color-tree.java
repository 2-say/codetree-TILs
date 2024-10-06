import java.io.*;
import java.util.*;

public class Main {

    static Node[] nodes;
    static boolean[] isRoot;
    static int Q;
    static Map<Integer, Integer> colorMap;
    static final int MAX_ID = 100005;

    static class Node {
        int pid;
        int color;
        int maxDepth;
        List<Integer> childs;
        int colorLastUpdate; 

        Node(int pid, int color, int maxDepth, int colorLastUpdate) {
            this.pid = pid;
            this.color = color;
            this.maxDepth = maxDepth;
            childs = new ArrayList<>();
            this.colorLastUpdate = colorLastUpdate;
        }

        void addChild(int id) {
            childs.add(id);
        }

        void setColorUpdate(int color, int updateTime) {
            this.color = color;
            this.colorLastUpdate = updateTime;
        }

        @Override
        public String toString() {
            return "(" + pid + " " + maxDepth + " " + color +  ")" ; 
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Q = Integer.parseInt(br.readLine());
        
        // Q 범위 필요
        nodes = new Node[MAX_ID];
        isRoot = new boolean[MAX_ID];

        for(int i = 0; i < Q; i++) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            int command = Integer.parseInt(st.nextToken());
            
            //System.out.println(Arrays.toString(nodes));

            //노드 추가
            if(command == 100) {
                int mid = Integer.parseInt(st.nextToken());
                int pid = Integer.parseInt(st.nextToken());
                int color = Integer.parseInt(st.nextToken());
                int maxDepth = Integer.parseInt(st.nextToken());


                if(pid == -1) isRoot[mid] = true;
                //depth 체크
                if(depthCheck(pid)) {
                    nodes[mid] = new Node(pid, color, maxDepth, i);
                    //맨 위가 아니라면 부모 노드에서 자식리스트 추가
                    if(pid != -1) nodes[pid].childs.add(mid);
                }

            } if(command == 200) { //색 변경
                int mid = Integer.parseInt(st.nextToken());
                int color = Integer.parseInt(st.nextToken());
                nodes[mid].setColorUpdate(color, i);
            }

            if(command == 300) {
                int mid = Integer.parseInt(st.nextToken());
                System.out.println(getColor(mid));
            }

            if(command == 400) {
                System.out.println(score());
            }
        }
    }

    static boolean depthCheck(int pid) {
        int needDepth = 1;

        while(pid != -1) {
            Node p = nodes[pid];
            if(p.maxDepth <= needDepth) return false;
            pid = p.pid;
        }
        return true;
    }


    static int getColor(int mid) {
        for(int i = 0; i < MAX_ID; i++) {
            if(isRoot[i]) colorUpdate(i, nodes[i].color);
        }
        return nodes[mid].color;
    }
    
    //BFS를 통해서 칠해간다.
    static void colorUpdate(int mid, int color) {
        if(nodes[mid] == null) return;
        nodes[mid].color = color;

        for(int next : nodes[mid].childs) {
            if(nodes[mid].colorLastUpdate < nodes[next].colorLastUpdate)
                colorUpdate(next, nodes[next].color);
            else {
                colorUpdate(next, color);
            }
        }
    }

    static int score() {
        int result = 0;
        
        for(int i = 0 ; i < MAX_ID; i++) {
            if(nodes[i] == null) continue;
            colorMap = new HashMap<>();
            colorCount(i);
            result +=(int) Math.pow(colorMap.size(),2);
        }

        return result;
    }

    static void colorCount(int mid) {
        if(nodes[mid] == null) return;
        int c = nodes[mid].color; 

        if(colorMap.containsKey(c)) colorMap.put(c, colorMap.get(c) + 1);
        else colorMap.put(c, 1);
        
        for(int next : nodes[mid].childs) {
            colorCount(next);
        }
    }
    
    
}