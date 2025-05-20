import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;



public class ExpenseTracker {
    private static List<Transaction> transactions = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Expense Tracker");
        while (true) {
            System.out.println("\n1. Add Transaction\n2. View Monthly Summary\n3. Load Transactions from File\n4. Save Transactions to File\n5. Exit");
            System.out.print("Choose option: ");
            int option = scanner.nextInt();
            scanner.nextLine(); // clear buffer

            switch (option) {
                case 1 -> addTransaction();
                case 2 -> viewMonthlySummary();
                case 3 -> loadFromFile();
                case 4 -> saveToFile();
                case 5 -> {
                    System.out.println("Exiting. Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void addTransaction() {
        System.out.print("Enter type (income/expense): ");
        String type = scanner.nextLine().trim().toLowerCase();
        if (!type.equals("income") && !type.equals("expense")) {
            System.out.println("Invalid type.");
            return;
        }

        System.out.print("Enter amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // clear buffer

        String category;
        if (type.equals("income")) {
            System.out.print("Enter category (salary/business): ");
        } else {
            System.out.print("Enter category (food/rent/travel): ");
        }
        category = scanner.nextLine().trim();

        System.out.print("Enter date (YYYY-MM-DD): ");
        LocalDate date = LocalDate.parse(scanner.nextLine().trim());

        transactions.add(new Transaction(type, amount, category, date));
        System.out.println("Transaction added!");
    }

    private static void viewMonthlySummary() {
        System.out.print("Enter year (YYYY): ");
        int year = scanner.nextInt();
        System.out.print("Enter month (1-12): ");
        int month = scanner.nextInt();

        double totalIncome = 0, totalExpense = 0;
        Map<String, Double> categoryExpenses = new HashMap<>();

        for (Transaction t : transactions) {
            if (t.date.getYear() == year && t.date.getMonthValue() == month) {
                if (t.type.equals("income")) {
                    totalIncome += t.amount;
                } else {
                    totalExpense += t.amount;
                    categoryExpenses.put(t.category,
                            categoryExpenses.getOrDefault(t.category, 0.0) + t.amount);
                }
            }
        }

        System.out.println("\nSummary for " + Month.of(month) + " " + year + ":");
        System.out.println("Total Income: " + totalIncome);
        System.out.println("Total Expense: " + totalExpense);
        System.out.println("Expense by Category:");
        for (String cat : categoryExpenses.keySet()) {
            System.out.println("  " + cat + ": " + categoryExpenses.get(cat));
        }
        System.out.println("Net Savings: " + (totalIncome - totalExpense));
    }

    private static void loadFromFile() {
        System.out.print("Enter file path to load (CSV): ");
        String filePath = scanner.nextLine();
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            for (String line : lines) {
                if (line.trim().isEmpty() || line.startsWith("TYPE")) continue;
                String[] parts = line.split(",");
                if (parts.length != 4) continue;

                String type = parts[0].trim();
                double amount = Double.parseDouble(parts[1].trim());
                String category = parts[2].trim();
                LocalDate date = LocalDate.parse(parts[3].trim());

                transactions.add(new Transaction(type, amount, category, date));
            }
            System.out.println("Transactions loaded successfully!");
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    private static void saveToFile() {
        System.out.print("Enter file path to save (CSV): ");
        String filePath = scanner.nextLine();
        try (PrintWriter writer = new PrintWriter(filePath)) {
            writer.println("TYPE,AMOUNT,CATEGORY,DATE");
            for (Transaction t : transactions) {
                writer.println(t);
            }
            System.out.println("Transactions saved successfully!");
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }
}

class Transaction {
    String type; // income or expense
    double amount;
    String category;
    LocalDate date;

    public Transaction(String type, double amount, String category, LocalDate date) {
        this.type = type.toLowerCase();
        this.amount = amount;
        this.category = category.toLowerCase();
        this.date = date;
    }

    @Override
    public String toString() {
        return type + "," + amount + "," + category + "," + date;
    }
}
