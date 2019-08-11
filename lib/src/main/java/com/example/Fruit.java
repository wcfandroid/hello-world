package com.example;

/**
 * Created by weichangfa on 2016/12/26.
 */

    public   class Fruit{

    {
      color="父类";
        System.out.println("父类构造代码块");//color: null


    }

        public Fruit() {
            System.out.println("父类构造函数");//color: null

            color = this.getColor();//父类color属性初始化依赖于重载的方法getColor
//            color = getColor();
        }
        public String getColor(){
            return "unkonw";
        }
    String color=getParentColor();
    public String getParentColor(){
        System.out.println("父类成员变量初始化∂∂");//color: null
        return "unkonw";
    }

        @Override
        public String toString() {
            return color;
        }
    }

     class Apple extends Fruit{

         {
             System.out.println("子类构造代码块");//color: null
         }
         public int status=getStatus();
        @Override
        public String getColor() {
            return "color: " + color;
        }
         public int getStatus(){
             System.out.println("子类显示初始化");//color: null
             return 2;
         }

        public Apple() {
            color = "red";
            System.out.println("子类构造函数");//color: null

        }

        public static void main(String[] args) {
            System.out.println(new Apple());//color: null
        }
    }
