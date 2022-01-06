package oop.dayplanner3.model;

public class Recommends {
    private static String res;

    public static String analyseWork(Integer hour){

        return "work";
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
        return "study";
    }

    public static String analyseEatTime(Integer hour){
        return "eat time";
    }

    public static String analyseBreakTime(Integer hour){
        return "break time";
    }

}
