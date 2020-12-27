//给定一个 N 叉树，返回其节点值的后序遍历。 
//
// 例如，给定一个 3叉树 : 
//
// 
//
// 
//
// 
//
// 返回其后序遍历: [5,6,3,2,4,1]. 
//
// 
//
// 说明: 递归法很简单，你可以使用迭代法完成此题吗? Related Topics 树 
// 👍 118 👎 0


//leetcode submit region begin(Prohibit modification and deletion)
/*
// Definition for a Node.
class Node {
    public int val;
    public List<Node> children;

    public Node() {}

    public Node(int _val) {
        val = _val;
    }

    public Node(int _val, List<Node> _children) {
        val = _val;
        children = _children;
    }
};
*/

class Solution {
    public List<Integer> postorder(Node root) {
        List<Integer> ret = new ArrayList<>();
        if (root != null) {
            search(root, ret);
        }
        return ret;
    }
    
    public void search(Node root, List<Integer> ret) {
       
        if (root.children != null) {
            for (Node child : root.children) {
                search(child, ret);
            }
        }
        ret.add(root.val);
        
    }
}
//leetcode submit region end(Prohibit modification and deletion)
