package com.example;

public class MyClass {
    final String str1 = "Hello";//定义实例变量时指定初始值

                final String str2;//非静态初始化块中对实例变量进行初始化
         final String str3;//构造器中对实例变量进行初始化

    {
                 str2 = "Hello";
            }
         public MyClass() {
                 str3 = "Hello";
            }

                 public void show(){
                System.out.println(str1 + str1 == "HelloHello");//true
                 System.out.println(str2+str2);//false
                System.out.println(str3+str3 );//false
            }
    // new MyClass().show();
    public static final String A="ab";

    public static final String B="cd";



    public static final String C;

    public static final String D;

    static{

        C = "ab";

        D = "cd";

    }

    public static void main(String[] args) {

        String t = "abcd";//指向池



        String s1 = "ab";//指向池

        String s2 = "cd";//指向池



        String s = s1+s2;//指向堆

        System.out.println(s==t);//false



        String ss = "ab"+s2;//指向堆

        System.out.println(ss==t);//false



        String sss = "ab"+"cd";//指向池

        System.out.println(sss==t);//true



        String ssss = A+B;//指向池

        System.out.println(ssss==t);//true



        System.out.println((C+D)==t);//false
             }
}
