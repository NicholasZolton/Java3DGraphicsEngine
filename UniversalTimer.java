public class UniversalTimer {
    static double timeElapsed = 0;

    static public void setTime(double newTime){
        timeElapsed = newTime;
    }

    static public void addTime(int timeToAdd){
        double dividedTime = ((double)timeToAdd/1000);
        timeElapsed += dividedTime;
    }
}
