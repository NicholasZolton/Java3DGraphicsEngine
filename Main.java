import javax.swing.*;

public class Main {

    public static void main(String[] args) {
	// write your code here

        JFrame mainFrame = new JFrame();
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setUndecorated(true);
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        DisplayPanel dp = new DisplayPanel();
        mainFrame.setContentPane(dp);
        mainFrame.setVisible(true);

        while(true){
            waitMS(10);
            UniversalTimer.addTime(10);
            dp.repaint();
        }

    }

    public static void waitMS(long ms){
        long currentTime = System.currentTimeMillis();
        long futureTime = currentTime+ms;
        while(currentTime < futureTime){
            currentTime = System.currentTimeMillis();
        }
    }
}
