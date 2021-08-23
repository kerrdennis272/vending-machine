package com.techelevator;

import com.techelevator.view.Menu;
import com.techelevator.view.PurchaseMenu;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class VendingMachineCLI {

	private static final String MAIN_MENU_OPTION_DISPLAY_ITEMS = "Display Vending Machine Items";
	private static final String MAIN_MENU_OPTION_PURCHASE = "Purchase";
	private static final String MAIN_MENU_OPTION_EXIT = "Exit";
	private static final String MAIN_MENU_OPTION_PRINT_SALES_REPORT = "";
	private static final String[] MAIN_MENU_OPTIONS = {MAIN_MENU_OPTION_DISPLAY_ITEMS, MAIN_MENU_OPTION_PURCHASE, MAIN_MENU_OPTION_EXIT, MAIN_MENU_OPTION_PRINT_SALES_REPORT};

	private static final String PURCHASE_MENU_OPTION_FEED_MONEY = "Feed Money";
	private static final String PURCHASE_MENU_OPTION_SELECT_PRODUCT = "Select Product";
	private static final String PURCHASE_MENU_OPTION_FINISH_TRANSACTION = "Finish Transaction";
	private static final String[] PURCHASE_MENU_OPTIONS = {PURCHASE_MENU_OPTION_FEED_MONEY, PURCHASE_MENU_OPTION_SELECT_PRODUCT, PURCHASE_MENU_OPTION_FINISH_TRANSACTION};

	private Menu menu;
	private PurchaseMenu purchaseMenu;

	public VendingMachineCLI(Menu menu, PurchaseMenu purchaseMenu) {
		this.menu = menu;
		this.purchaseMenu = purchaseMenu;
	}

	public void run() {
		//initializes the date format and localtime classes. Makes it into a string so its easy to work with
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy+hh_mm_ss_a");
		LocalDateTime currentDateTime = LocalDateTime.now();
		String dateAndTime = currentDateTime.format(dateFormat);

		String currentDirectory = System.getProperty("user.dir");
		File purchaseLog = new File(currentDirectory + "/log.txt");
		File salesReport = new File(currentDirectory + "/salesReport_" + dateAndTime + ".txt");

		//if a log already exists then we delete it before making a new one
		if (purchaseLog.exists()) {
			purchaseLog.delete();
		}

		//creates a log file
		try {
			purchaseLog.createNewFile();
		} catch (IOException exception){
			System.out.println(exception);
		}
		//Populates vending machine line by line from file read in populateVendingMachine before the user can interact
		List<Item> vendingMachine = populateVendingMachine();

		while (true) {
			String choice = (String) menu.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			//Option 1: when selected prints the contents of the vending machine to console
			if (choice.equals(MAIN_MENU_OPTION_DISPLAY_ITEMS)) {
				showVendingMachineStock(vendingMachine);

			} else if (choice.equals(MAIN_MENU_OPTION_PURCHASE)) {
				// do purchase
				//vendingMachine = purchaseMenu(vendingMachine, purchaseLog);
				while (true) {
					String subMenuChoice = (String) purchaseMenu.getChoiceFromOptions(PURCHASE_MENU_OPTIONS);

					if (subMenuChoice.equals(PURCHASE_MENU_OPTION_FEED_MONEY)) {
						purchaseMenu.feedMoney(purchaseLog);

					}
					else if (subMenuChoice.equals(PURCHASE_MENU_OPTION_SELECT_PRODUCT)) {
						purchaseMenu.selectProduct(vendingMachine, purchaseLog);
					}
					else if (subMenuChoice.equals(PURCHASE_MENU_OPTION_FINISH_TRANSACTION)) {
						purchaseMenu.giveChange(purchaseLog);
						break;
					}
				}

			} else if (choice.equals(MAIN_MENU_OPTION_PRINT_SALES_REPORT)){
				System.out.println("Printing Sales Report");

				//If this hidden option is selected then it creates a salesReport file
				try {
					salesReport.createNewFile();
				} catch (IOException ex){
					System.out.println(ex);
				}

				//try block that adds the desired information to the end of the sales Report file
				try (FileWriter fileWriter = new FileWriter(salesReport, true);
					 PrintWriter printWriter = new PrintWriter(fileWriter)) {

					//initialize total profit
					double totalProfit = 0;

					//goes through each item of the vending machine list
					for (Item item : vendingMachine){

						//calculates amount sold by subtracting the amount left in index 4 from the total which is 5 in this problem
						int numberSold = 5 - item.getQuantity();

						//calculates the item profit and then adds it to the total profit
						double itemProfit = numberSold * item.getPrice();
						totalProfit += itemProfit;

						//creates our printed out string with the name of the item and amount sold
						String outputString = item.getItemName() + "|" + numberSold;
						printWriter.println(outputString);
					}

					//converts totalProfit to a Big Decimal so it rounds to 2 decimals each time
					BigDecimal totalProfitAsBigDecimal = new BigDecimal(totalProfit).setScale(2, RoundingMode.HALF_UP);

					String totalProfitString = "Total Profit: $" + totalProfitAsBigDecimal;
					printWriter.println(totalProfitString);

					if(totalProfit > 10.00){
						printWriter.println("You Are Rich!");
					}

				} catch (Exception ex) {
					System.out.println(ex);
				}
			//When option 3 is selected: end the program
			} else {
				System.exit(0);
			}
		}
	}

	//Reads input file line by line, returns a list containing each line in order
	public static List<Item> populateVendingMachine() {
		File vendingMachine = new File("vendingmachine.csv");

		List<Item> itemList = new ArrayList<Item>();

		try (Scanner stockReader = new Scanner(vendingMachine)) {

			//reads each line from the vending machine file and adds 5 to the end of it, where 5 is the stock of the product
			while (stockReader.hasNextLine()) {
				String line = stockReader.nextLine();
				String[] splitLine = line.split("\\|");

				String position = splitLine[0];
				String name = splitLine[1];
				double price = Double.parseDouble(splitLine[2]);

				if(splitLine[3].equals("Chip")){
					Chip chip = new Chip(position, name, price);
					itemList.add(chip);
				}
				else if(splitLine[3].equals("Candy")){
					Candy candy = new Candy(position, name, price);
					itemList.add(candy);
				}
				else if(splitLine[3].equals("Drink")){
					Drink drink = new Drink(position, name, price);
					itemList.add(drink);
				}
				else{
					Gum gum = new Gum(position, name, price);
					itemList.add(gum);
				}

			}

		} catch (FileNotFoundException fileNotFoundException) {
			System.out.println("Error: File not found");
		}
		return itemList;
	}

	//prints list of each item in machine with updated message if stock = 0
	public static void showVendingMachineStock(List<Item> vendingMachine){
		for (Item item : vendingMachine) {
			//Condition for when an item is out of stock, print SOLD OUT
			if (item.getQuantity() == 0){
				System.out.println(item.getPosition() + "|" + item.getItemName() + "|$" + item.getPrice() + "|Quantity: SOLD OUT");
			}
			else{
				System.out.println(item.getPosition() + "|" + item.getItemName() + "|$" + item.getPrice() + "|Quantity: " + item.getQuantity());
			}
		}
	}

	public static void main(String[] args) {
		Menu menu = new Menu(System.in, System.out);
		PurchaseMenu purchaseMenu = new PurchaseMenu(System.in, System.out);
		VendingMachineCLI cli = new VendingMachineCLI(menu, purchaseMenu);
		cli.run();
	}
}
