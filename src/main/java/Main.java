import java.io.IOException;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        BaseRobot chosenSite = getSiteSelection();
        if (chosenSite == null ) {
            chosenSite = getSiteSelection();
        }
        int points = 0;
        String userText;
        int userGuessNumber = 0;
        System.out.println("Loading......");
        HashMap<String, Integer> wordsMap;
        wordsMap = (HashMap<String, Integer>) chosenSite.getWordsStatistics();
        System.out.println("Guess what are the most used words on this site:\n" +
                "You have 5 guesses!");
        System.out.println("Hint: The title of the longest article is: " + chosenSite.getLongestArticleTitle());
        boolean moreThanAWord = true;
        String userGuess = "";
        for (int i = 1; i <= Definitions.MAXIMUM_NUMBER_OF_GUESSES; i++) {
            while (moreThanAWord) {
                System.out.println("Enter word number " + i + " (You have to enter just one word): ");
                userGuess = scanner.nextLine();
                if (userGuess.contains(" ")) {
                    moreThanAWord = true;
                    System.out.println("Incorrect! you have to insert only one word");
                } else {
                    moreThanAWord = false;
                }
            }
            moreThanAWord = true;
            if (wordsMap.get(userGuess) != null) {
                points += wordsMap.get(userGuess);
                System.out.println(userGuess + " : " + wordsMap.get(userGuess));
            }
        }
        System.out.println("Guess a text from 1 to 20 characters that you think appears the most on this site's titles:");


        boolean invalidText;
        do {
            System.out.println("Enter your text: ");
            userText = scanner.nextLine();
            if (userText.length() < Definitions.MINIMUM_TEXT_LENGTH || userText.length() > Definitions.MAXIMUM_TEXT_LENGTH) {
                System.out.println("Invalid text! please enter a text between 1 to 20 characters");
                invalidText = true;
            } else {
                invalidText = false;
            }
        } while (invalidText);


        boolean tryAgain = true;
        while (tryAgain) {
            try {
                System.out.println("For 250 points,Guess how many times does the text you wrote appears in the site titles:");
                userGuessNumber = scanner.nextInt();
                tryAgain = false;
            } catch (InputMismatchException e) {
                e.printStackTrace();
                scanner.nextLine();
                System.out.println("Invalid!please guess a number");
            }
        }
        System.out.println("Loading.....");
        int appearanceNumber = chosenSite.countInArticlesTitles(userText);
        if (userGuessNumber <= appearanceNumber + Definitions.GUESS_ACCURACY && userGuessNumber >= appearanceNumber - Definitions.GUESS_ACCURACY) {
            System.out.println("You got an extra of 250 points!Good job!");
            points += Definitions.EXTRA_POINTS;
        }else {
            System.out.println("You didnt get the extra points!");
        }
            System.out.println("The game is over,Your points: " + points + " Well played!");
        }

    private static BaseRobot getSiteSelection() {
        int input = 0;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the guessing game.");
        try {
            while (input < 1 || input > 3) {
                System.out.println("which site do you want to choose?\n" + "1.Mako\n" + "2.Ynet\n" + "3.Walla");
                input = scanner.nextInt();
            }
        } catch (InputMismatchException e) {
            System.out.println("You have to insert a number! try again");
        }

        scanner.nextLine();
        switch (input) {
            case Definitions.MAKO_OPTION:
                return new MakoRobot();
            case Definitions.YNET_OPTION:
                return new YnetRobot();
            case Definitions.WALLA_OPTION:
                return new WallaRobot();
            default:
                return null;
        }
    }
}