package com.example;

/**
 * Created by weichangfa on 2017/1/5.
 */

public class FinalyTest {
    public static void main(String[] args) {

        //System.out.println("i in last is    "+new FinalyTest().test());;
        //A a=new A();
        //a.fisrst=4;
       // char[] ch={'e','f','g'};
        //change(a,ch);
        //System.out.println("str--------"+a.fisrst);
        //System.out.println(ch);

        String s1="ab";
        //String s2=s1.intern();
        String str2 = new String("abc").intern();
        String str3="a";
        String str4="b";
        String str5="a"+"b";
        System.out.print(s1==str5);//返回false
        System.out.print(s1==str2);//返回false
    }

    private static void change(A a, char[] ch) {
        a.fisrst=6;
        //System.out.println("str change--------"+str);
        ch[0] = 'h';
    }
    static class A{
        public int fisrst=3;
    }


    static int test()
    {
        int i=10;
        try{
            System.out.println("i in try block is："+i);
            return --i;
        }
        catch(Exception e){

        }
        finally{

            System.out.println("i in finally - from try or catch block is："+i);
            --i;
            //--i;
            System.out.println("i in finally block is："+i);
            return --i;
        }
    }
}
