//给定一个字符串数组，将字母异位词组合在一起。字母异位词指字母相同，但排列不同的字符串。 
//
// 示例: 
//
// 输入: ["eat", "tea", "tan", "ate", "nat", "bat"]
//输出:
//[
//  ["ate","eat","tea"],
//  ["nat","tan"],
//  ["bat"]
//] 
//
// 说明： 
//
// 
// 所有输入均为小写字母。 
// 不考虑答案输出的顺序。 
// 
// Related Topics 哈希表 字符串 
// 👍 614 👎 0


//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> strMap = new HashMap<>();
        for (String str : strs) {
            char[] keys = str.toCharArray();
            Arrays.sort(keys);
            String key = String.valueOf(keys);
            List<String> value = strMap.getOrDefault(key, new ArrayList<>());
            value.add(str);
            strMap.put(key, value);
        }
        return new ArrayList<>(strMap.values());
    }
}
//leetcode submit region end(Prohibit modification and deletion)
