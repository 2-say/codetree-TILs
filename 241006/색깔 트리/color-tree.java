import java.util.*;

public class Main {
    static final int MAX_ID = 100005; // ID의 최대값입니다
    static final int MAX_DEPTH = 105; // 트리의 최대 깊이입니다
    static final int COLOR_MAX = 5;

    static class Node {
        int id = 0;
        int color = 0;
        int lastUpdate = 0; // 노드가 추가 된 시점 혹은 가장 마지막으로 색깔 변경 명령이 시행된 시점입니다
        int maxDepth = 0; // node가 가질 수 있는 최대 깊이입니다
        int parentId = 0; // 부모노드의 id를 저장합니다
        ArrayList<Integer> childIds = new ArrayList<>(); // 자식노드의 id들을 저장합니다
    }

    // 점수 조회 명령을 간편히 구현하기 위한 class입니다
    static class ColorCount {
        int[] cnt = new int[COLOR_MAX + 1];

        // 각 Color의 개수를 합칩니다
        ColorCount add(ColorCount obj) {
            ColorCount res = new ColorCount();
            for (int i = 1; i <= COLOR_MAX; i++) {
                res.cnt[i] = this.cnt[i] + obj.cnt[i];
            }
            return res;
        }

        // 서로다른 색의 개수의 제곱을 반환합니다
        int score() {
            int result = 0;
            for (int i = 1; i <= COLOR_MAX; i++) {
                if (this.cnt[i] > 0) result++;
            }
            return result * result;
        }
    }

    static Node[] nodes = new Node[MAX_ID];
    static boolean[] isRoot = new boolean[MAX_ID];

    static {
        for (int i = 0; i < MAX_ID; i++) {
            nodes[i] = new Node();
        }
    }

    // 해당 node가 자식노드를 가질 수 있는지 확인합니다
    // 해당 과정에서는 root까지 조상들을 각각 탐색하며 maxDepth를 확인합니다
    static boolean canMakeChild(Node curr, int needDepth) {
        if (curr.id == 0)
            return true;
        if (curr.maxDepth <= needDepth)
            return false;
        return canMakeChild(nodes[curr.parentId], needDepth + 1);
    }

    // curr 노드의 색깔 정보와 해당 색깔이 설정된 시간을 return 합니다.
    // root에 도달할때까지 부모를 거슬러 올라가며 lastUpdate시간을 이용하여 현재 노드가 가져야하는 색깔을 계산합니다
    static int[] getColor(Node curr) {
        if (curr.id == 0)
            return new int[] { 0, 0 };
        int[] info = getColor(nodes[curr.parentId]);
        if (info[1] > curr.lastUpdate) {
            return info;
        } else {
            return new int[] { curr.color, curr.lastUpdate };
        }
    }

    static Object[] getBeauty(Node curr, int color, int lastUpdate) {
        // root에서부터 내려온 색 정보보다 현재 노드의 색정보가 최신이라면 갱신합니다
        if (lastUpdate < curr.lastUpdate) {
            lastUpdate = curr.lastUpdate;
            color = curr.color;
        }
        int result = 0;
        ColorCount colorCount = new ColorCount();
        colorCount.cnt[color] = 1;
        for (int childId : curr.childIds) {
            Node child = nodes[childId];
            Object[] subResult = getBeauty(child, color, lastUpdate);
            colorCount = colorCount.add((ColorCount) subResult[1]);
            result += (Integer) subResult[0];
        }
        result += colorCount.score();
        return new Object[] { result, colorCount };
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int Q = scanner.nextInt();
        // Q개의 query에 대해 명령을 수행합니다
        for (int i = 1; i <= Q; i++) {
            int T = scanner.nextInt();
            if (T == 100) {
                int mId = scanner.nextInt();
                int pId = scanner.nextInt();
                int color = scanner.nextInt();
                int maxDepth = scanner.nextInt();
                // 부모의 Id가 -1인 경우 root노드입니다
                if (pId == -1) {
                    isRoot[mId] = true;
                }
                // 현재 노드를 만드려는 위치에 노드를 만들 수 있는지 확인합니다
                if (isRoot[mId] || canMakeChild(nodes[pId], 1)) {
                    // node 정보를 기입해줍니다
                    nodes[mId].id = mId;
                    nodes[mId].color = color;
                    nodes[mId].maxDepth = maxDepth;
                    nodes[mId].parentId = isRoot[mId] ? 0 : pId;
                    nodes[mId].lastUpdate = i;
                    if (!isRoot[mId]) {
                        nodes[pId].childIds.add(mId);
                    }
                }
            } else if (T == 200) {
                int mId = scanner.nextInt();
                int color = scanner.nextInt();
                // 색 변화 명령에 대해 lastUpdate를 갱신하여 lazy update를 가능하게 준비합니다.
                // 시간복잡도를 위하여 색깔 변화 명령에 대해 subtree에 모두 갱신하는 것이 아닌, 추후 색깔 조회, 점수 조회 명령에서 lazy한 계산 가능한 형태로 만듭니다.
                nodes[mId].color = color;
                nodes[mId].lastUpdate = i;
            } else if (T == 300) {
                int mId = scanner.nextInt();
                // mId번 node가 가지는 색깔을 계산합니다
                System.out.println(getColor(nodes[mId])[0]);
            } else if (T == 400) {
                int beauty = 0;
                for (int idx = 1; idx < MAX_ID; idx++) {
                    // root 노드들에 대해 점수를 계산합니다
                    if (isRoot[idx]) {
                        beauty += (Integer) getBeauty(nodes[idx], nodes[idx].color, nodes[idx].lastUpdate)[0];
                    }
                }
                System.out.println(beauty);
            }
        }
        scanner.close();
    }
}