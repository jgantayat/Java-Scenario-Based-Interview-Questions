import java.util.Arrays;

public class RemoveDuplicatesFromASortedArray {

    /**
     * Example 1:
     *
     * Input: nums = [1,1,2]
     * Output: 2, nums = [1,2,_]
     * Explanation: Your function should return k = 2, with the first two elements of nums being 1 and 2 respectively.
     * It does not matter what you leave beyond the returned k (hence they are underscores).
     *
     */
    public static void main(String[] args) {
        int[] nums = {0,0,1,1,1,2,2,3,3,4};
        int k = removeDuplicates(nums);
        System.out.println("Length after removing duplicates: " + k);
    }

    private static int removeDuplicates(int[] nums) {
        if (nums.length == 0) return 0;

        int uniqueindex=0;

        for(int i=1; i< nums.length; i++){
            if(nums[i] !=nums[uniqueindex]){
                uniqueindex++;
                nums[uniqueindex] = nums[i];
            }
        }
        return uniqueindex + 1;
    }
}
