public class Timer {
    public static double gameLength;
    public double turnLength = 5;
    public double getTime(){
        System.out.println("getTime()");
        return gameLength;
    }
    public static void setTime(double x){
        System.out.println("setTime(double)");
        gameLength = x;
    }
}
