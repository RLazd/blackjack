package main.java.blackJack;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Scanner;

public class Blackjack {

    public static void main(String[] args) throws InterruptedException {
        Scanner scn = new Scanner(System.in);

        GetDeckResult deck = getDeck();

        boolean playAgain = true;

        System.out.println("Welcome to blackjack!");

        while (playAgain) {

            //player
            boolean isNotEnough = true;
            int currentValuePlayer = 0;
            while (isNotEnough) {
                Card myNewCard = drawCard(deck.getDeckId());
                Thread.sleep(2000);
                currentValuePlayer += myNewCard.getCardValue().getBlacjackValue();
                System.out.println("Your new card is: " + myNewCard.getCardValue() + " of " + myNewCard.getSuit());
                System.out.println("Current score: " + currentValuePlayer);
                System.out.println("Draw another? Y/N");
                isNotEnough = scn.nextLine().equals("Y");
            }

            //dealer
            int currentValueDealer = 0;
            while (currentValueDealer < 17) {
                Card dealersNewCard = drawCard(deck.getDeckId());
                currentValueDealer += dealersNewCard.getCardValue().getBlacjackValue();
                System.out.println(" Dealers new card is: " + dealersNewCard.getCardValue() + " of " + dealersNewCard.getSuit());
                Thread.sleep(2000);
                System.out.println("Current score: " + currentValueDealer);
            }

            System.out.println("Players score is: " + currentValuePlayer);
            System.out.println("Dealers score is: " + currentValueDealer);

            determineWinner(currentValuePlayer, currentValueDealer);

            reshuffle(deck.getDeckId());

            System.out.println("Would you like to play again? y/n");
            String response = scn.nextLine();
            if (response.equalsIgnoreCase("y")){
                playAgain = true;
            } else if (response.equalsIgnoreCase("n")){
                playAgain = false;
            }
        }

    }

    private static GetDeckResult getDeck() {
        String result = callApi("https://www.deckofcardsapi.com/api/deck/new/shuffle/?deck_count=1");
        JSONObject rawResult = new JSONObject(result);
        return new GetDeckResult(
                rawResult.getBoolean("success"),
                rawResult.getString("deck_id"),
                rawResult.getBoolean("shuffled"),
                rawResult.getInt("remaining")
        );
    }

    private static Card drawCard(String deckId) {
        String result = callApi("https://deckofcardsapi.com/api/deck/" + deckId + "/draw/?count=1");
        JSONObject rawResult = new JSONObject(result);
        JSONObject cardData = (JSONObject) rawResult.getJSONArray("cards").get(0);
        Suit suit = Suit.valueOf(cardData.getString("suit"));
        CardValue value = CardValue.getCardValueByString(cardData.getString("value"));
        return new Card(suit, value);
    }

    private static String callApi(String theURL) {
        try {
            URL url = new URL(theURL);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder inputData = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                inputData.append(inputLine);
            }
            return inputData.toString();
        } catch (IOException e) {
            System.out.println("Error!");
            return "";
        }
    }

    private static void determineWinner(int currentValuePlayer, int currentValueDealer) {
        if (currentValuePlayer >= 22) {
            System.out.println("You lost. Dealer won.");
        } else if (currentValuePlayer >= 22) {
            System.out.println("Dealer lost. You won!");
        } else if (currentValuePlayer == currentValueDealer) {
            System.out.println("It's a tie!");
        } else if (currentValuePlayer > currentValueDealer) {
            System.out.println("Player won. Dealer lost.");
        } else if (currentValuePlayer < currentValueDealer) {
            System.out.println("Dealer won. You lost.");
        }
    }

    private static void reshuffle(String deckId) {
        callApi("https://deckofcardsapi.com/api/deck/" + deckId + "/shuffle/");
    }

}
