package oop.dayplanner3.model;

public class Recommends {
    private static String res;

    public static String analyseWork(Integer hour){
        if(hour < 4){
            res+= "You work hard today!! Let's have a lunch.";
        }else if(hour > 4){
            res+= "You study a lot today..Let's spend time with friends or family!";
        }
        return res;
    }

    public static String analyseNightSleep(Integer hour){
        
        if(hour < 4){
           res+= "You sleep very little at night";
        }else if(hour > 4 && hour < 8){
            res+= "You sleep not enough at night. It's great idea to have a nap in daytime.";
        }
        else if(hour >= 8){
            res+= "You sleep enough at night. Great job!";
        }
        return res;
    }


    public static String analyseStudy(Integer hour){
        if(hour < 4){
            res+= "You did a good job!! It's time to have a break time.";
        }else if(hour > 4){
            res+= "You study a lot today..Let's spend time with friends or family!";
        }
        return res;
    }

    public static String analyseEatTime(Integer hour){
        if(hour < 2){
            res+= "Don't forget about lunch! Bon appetite ~";
        }
        return res;
    }

    public static String analyseSleep(Integer hour){
        if(hour > 2){
            res+= "It's scientific fact that day sleep help to long life.";
        }
        return res;
    }
}
