//给定两个字符串 s 和 t ，编写一个函数来判断 t 是否是 s 的字母异位词。 
//
// 示例 1: 
//
// 输入: s = "anagram", t = "nagaram"
//输出: true
// 
//
// 示例 2: 
//
// 输入: s = "rat", t = "car"
//输出: false 
//
// 说明: 
//你可以假设字符串只包含小写字母。 
//
// 进阶: 
//如果输入字符串包含 unicode 字符怎么办？你能否调整你的解法来应对这种情况？ 
// Related Topics 排序 哈希表 
// 👍 321 👎 0


//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    // 用数组实现map
    public boolean isAnagram(String s, String t) {
        if (s == null || t == null) {
            return false;
        }
        int[] map = new int[255];
        for (char ch : s.toCharArray()) {
            map[(int) ch] = map[(int) ch] += 1;
        }
        
        for (char ch : t.toCharArray()) {
            map[(int) ch] = map[(int) ch] -= 1;
        }
        
        for (int value : map) {
            if (value != 0) {
                return false;
            }
        }
        return true;
    }
    
    // 排序挨个比较
    public boolean isAnagram0(String s, String t) {
        if (s == null || t == null || s.length() != t.length()) {
            return false;
        }
        char[] sArray = s.toCharArray();
        char[] tArray = t.toCharArray();
        Arrays.sort(sArray);
        Arrays.sort(tArray);
        for (int i = 0; i < sArray.length; i++) {
            if (sArray[i] != tArray[i]) {
                return false;
            }
        }
        return true;
    }
}
//leetcode submit region end(Prohibit modification and deletion)
