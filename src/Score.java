import javax.swing.*;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class Score {
    public void writeHighScore(int score) {
        int lastScore = 0;
        String name;
        try {
            String lastHighScoreTxt = readHighScore();
            if (lastHighScoreTxt.length() > 0) {
                String[] arr = lastHighScoreTxt.split("[ ]");
                lastScore = Integer.parseInt(arr[arr.length - 1]);
            }
            if (score > lastScore) {
                name = getHighScoreName();
                // Creates a FileWriter & writes the highScore string to the file
                FileWriter highScore = new FileWriter("highScore.txt");
                highScore.write(name + ": " + score);
                highScore.close(); // Closes the writer
            }
        } catch (Exception e) {
//            System.out.println(e);
            e.getStackTrace();
        }
    }

    protected String readHighScore() {
        try {
            Path pathOfHighScoreFile = Path.of("highScore.txt");
            return Files.readString(pathOfHighScoreFile);
        } catch (Exception e) {
            e.getStackTrace();
        }
        return null;
    }

    public String getHighScoreName() {
        String lastHighScoreTxt = readHighScore();
        String[] arr = lastHighScoreTxt.split("[:]");
        String lastName = arr[0];
        return JOptionPane.showInputDialog(null,
                "You've achieved the highest score! your Name please..",
                lastName);
    }

}
