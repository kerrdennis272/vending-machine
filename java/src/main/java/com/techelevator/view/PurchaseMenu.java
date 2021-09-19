package com.techelevator.view;

import com.techelevator.Item;
import com.techelevator.VendingMachineCLI;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class PurchaseMenu extends Menu{
    private String dateAndTime;
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a ");
    private PrintWriter out;
    private Scanner in = new Scanner(System.in);
    private double currentMoneyProvided;

    public PurchaseMenu(InputStream input, OutputStream output) {
        super(input, output);
    }

    public void feedMoney(File file) {
        //Asks user how much money they are adding
        System.out.println("How much money do you want to add?");
        //Stores amount specified by user in a string, then parses the double from it and stores it
        String addedAmountAsString = in.nextLine();
        double addedAmount = Double.parseDouble(addedAmountAsString);

        //converts the addedAmount to a BigDecimal for later
        BigDecimal addedAmountAsBigDecimal = new BigDecimal(addedAmount).setScale(2, RoundingMode.HALF_UP);
        //increments currentMoneyProvided by however much the user added
        currentMoneyProvided += addedAmount;
        //Converts currentMoneyProvided to BigDecimal to ensure accuracy
        BigDecimal totalMoney = new BigDecimal(currentMoneyProvided).setScale(2, RoundingMode.HALF_UP);

        //Shows the user how much money they have in the machine
        System.out.println("Current Money Provided: $" + totalMoney);
        //Initialized when user adds money so the time that money was added is accurately noted in the log
        LocalDateTime currentDateTime = LocalDateTime.now();

        //creates a input string for our log
        dateAndTime = currentDateTime.format(dateTimeFormatter) + "FEED MONEY: $" + addedAmountAsBigDecimal + " $" + totalMoney;

        //calls our method that prints to the log with our string we want to write
        logPrinter(file, dateAndTime);
    }

    public void selectProduct(List<Item> list, File file) {
        //prints list of items in machine for user to select from
        VendingMachineCLI.showVendingMachineStock(list);
        //Extra line printed for formatting purposes
        System.out.println();
        System.out.println("Select product by product code: ");
        //Converts user input to upper case to prevent selecting from being case sensitive
        String selection = in.nextLine().toUpperCase();

        //used for keeping track of whether the input corresponds to the item position ID
        boolean isSelectionValid = false;
        //Checks user selection against all item ID's in list
        for (Item item : list){
            //Match found, set boolean to true
            if (item.getPosition().equals(selection)){
                isSelectionValid = true;
                //condition evaluates true if item is sold out, prints result
                if (item.getQuantity() == 0){
                    System.out.println("The product is sold out.");
                    //breaks out of loop, goes back to purchase submenu
                    break;
                    //If item is in stock, checks if there is a sufficient amount of money in the machine to
                    //purchase the selected product, if there is not break the loop and return to purchase submenu
                }
                else if (item.getPrice() > currentMoneyProvided){
                    System.out.println("You do not have enough money!");
                    break;
                }

                //If neither above conditions evaluate true, proceed to purchase



                BigDecimal moneyBeforePurchase = new BigDecimal(currentMoneyProvided).setScale(2, RoundingMode.HALF_UP);
                //subtracts price of item from currentMoneyProvided
                currentMoneyProvided -= item.getPrice();
                //convert to BigDecimal to ensure accuracy
                BigDecimal totalMoney = new BigDecimal(currentMoneyProvided).setScale(2, RoundingMode.HALF_UP);
                System.out.println("Dispensing " + item.getItemName() + " : $" + item.getPrice());

                //displays message "Crunch Crunch" or others
                System.out.println(item.toString());

                //Prints amount of money still in machine with line break for formatting
                System.out.println("\nCurrent Money Provided: $" + totalMoney);

                //updates stock of the item purchased
                item.setQuantity(item.getQuantity() - 1);

                LocalDateTime currentDateTime = LocalDateTime.now();
                dateAndTime = currentDateTime.format(dateTimeFormatter) + item.getItemName() + " " + item.getPosition() +
                        " $" + moneyBeforePurchase + " $" + totalMoney;
                logPrinter(file, dateAndTime);
                //go back to purchase submenu
                break;
            }
        }

        //condition evaluates true if selection does not match any item ID, also reminds user how much money is in machine
        if (isSelectionValid == false){
            BigDecimal totalMoney = new BigDecimal(currentMoneyProvided).setScale(2, RoundingMode.HALF_UP);
            System.out.println("Invalid selection. Please select a listed product code");
            System.out.println("Current Money Provided: $" + totalMoney);
        }
    }

    public void giveChange(File file) {
        double totalAmountReturned = currentMoneyProvided;
        //rounds currentMoneyProvided
        currentMoneyProvided = currentMoneyProvided * 100;
        currentMoneyProvided = Math.round(currentMoneyProvided);
        currentMoneyProvided = currentMoneyProvided / 100;
        //calculates how many quarters the user will receive in change
        double numberOfQuartersAsDouble = currentMoneyProvided / 0.25;
        //converts number of quarters to nearest whole number rounded down
        int numberOfQuarters = (int)numberOfQuartersAsDouble;
        int numberOfDimes = 0;
        int numberOfNickels = 0;

        //updates amount of money in machine to however much is left after quarters are taken out
        currentMoneyProvided = currentMoneyProvided - (numberOfQuarters * 0.25);
        //convert to BigDecimal to ensure accuracy
        BigDecimal currentMoneyAsBigDecimal = new BigDecimal(currentMoneyProvided).setScale(2, RoundingMode.HALF_UP);
        //this chain checks the remainder of change in currentMoneyProvided and gives the user the correct corresponding number of dimes and nickels
        if (currentMoneyAsBigDecimal.doubleValue() == 0.20){
            numberOfDimes = 2;
        }
        else if (currentMoneyAsBigDecimal.doubleValue() == 0.15){
            numberOfDimes = 1;
            numberOfNickels = 1;
        }
        else if (currentMoneyAsBigDecimal.doubleValue() == 0.10){
            numberOfDimes = 1;
        }
        else if (currentMoneyAsBigDecimal.doubleValue() == 0.05){
            numberOfNickels = 1;
        }
        //prints change dispensed in coins followed by total amount dispensed
        BigDecimal totalMoney = new BigDecimal(totalAmountReturned).setScale(2, RoundingMode.HALF_UP);
        System.out.println("Dispensing Change: " + numberOfQuarters + " Quarters, " + numberOfDimes + " Dimes, " +
                numberOfNickels + " Nickels.\nTotal Amount: $" + totalMoney);
        LocalDateTime currentDateTime = LocalDateTime.now();
        dateAndTime = currentDateTime.format(dateTimeFormatter) + "GIVE CHANGE: $" + totalMoney + " $0.00\n";
        logPrinter(file, dateAndTime);
    }

    public void logPrinter(File logFile, String stringToPrint) {
        try (FileWriter fileWriter = new FileWriter(logFile, true);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            printWriter.println(stringToPrint);
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
