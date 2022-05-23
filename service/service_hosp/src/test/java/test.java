import java.util.*;
class Main {

    public static  void main(String []args){
        int []arr = {3,1,2,5,2,4};
        System.out.println(maxWater(arr));
//        Scanner sc = new Scanner(System.in);
//        int t= sc.nextInt();
//        while(t>0){
//            t--;
//            String a = sc.nextLine();
//            String b = sc.nextLine();
//            if((a.length()&1)!=0){
//                if(a ==b) System.out.println("YES");
//                else System.out.println("NO");
//            }else{
//                if(solve(a,b)) System.out.println("YES");
//                else System.out.println("NO");
//            }
//        }
    }

    public static long maxWater (int[] arr) {
        // write code here
        if (arr == null || arr.length <= 2) {
            return 0;
        }
        int low = 0;
        long sum = 0;
        long tmp = 0;
        //从左向右
        for (int i = 0; i < arr.length; i++) {
            if (arr[low] > arr[i]) {
                tmp = tmp + arr[low] - arr[i];
            }
            if (arr[low] <= arr[i]) {
                sum = sum + tmp;
                tmp = 0;
                low = i;
            }
        }
        System.out.println("======");
        System.out.println(sum);
        System.out.println("=========");
        low = arr.length-1;
        tmp = 0;
        //从右向左
        for (int j = arr.length-1; j >= 0; j--) {
            if (arr[low] > arr[j]) {
                tmp = tmp + arr[low] - arr[j];
            }
            //注意这里不能再 <=，否则可能会重复计算等于的情况
            if (arr[low] < arr[j]) {
                sum = sum + tmp;
                tmp = 0;
                low = j;
            }
        }
        return sum;
    }
    public static  boolean solve(String a,String b){
        if(a.length()==1&&b.length()==1&&a==b) return true;
        if(a.length()==1&&b.length()==1&&a!=b) return false;
        int mid = a.length()/2;
        if((mid&1) !=0){
            if(a.substring(0,mid)==b.substring(0,mid)&&a.substring(mid,mid)==b.substring(mid,mid)) return true;
            if(a.substring(0,mid)==b.substring(mid,a.length())&&a.substring(mid,a.length())==b.substring(0,mid)) return true;
            return false;
        }
        boolean a1 = solve(a.substring(0,mid),b.substring(0,mid));
        boolean b1 = solve(a.substring(mid,a.length()),b.substring(mid,a.length()));
        boolean c1 = solve(a.substring(0,mid),b.substring(mid,a.length()));
        boolean d1 = solve(a.substring(mid,a.length()),b.substring(0,mid));
        if(a1&&b1 || c1&&d1) return true;
        else return  false;
    }

}
