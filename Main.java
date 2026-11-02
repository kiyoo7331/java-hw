public class Main {
    public static void main(String[] args) {
        Balance rubBalance = new Balance(150_000, Currency.RUB);
        System.out.println("Initial balance: " + rubBalance);

        Balance updatedBalance = rubBalance.add(50_050);
        System.out.println("After add: " + updatedBalance);
        System.out.println("Original balance (unchanged): " + rubBalance);

        System.out.println("In USD: " + updatedBalance.convertTo(Currency.USD));
        System.out.println("In EUR: " + updatedBalance.convertTo(Currency.EUR));
        System.out.println("In CNY: " + updatedBalance.convertTo(Currency.CNY));

        BankAccount firstAccount = new BankAccount(
                "40817810000000000001",
                "Ivan Ivanov",
                new Balance(5_000_000, Currency.RUB),
                2_000_000
        );

        BankAccount secondAccount = new BankAccount(
                "40817840000000000002",
                "Anna Smirnova",
                new Balance(10_000, Currency.USD),
                50_000
        );

        System.out.println("\nAccounts created:");
        System.out.println(firstAccount);
        System.out.println(secondAccount);

        firstAccount.deposit(500_000);
        firstAccount.withdraw(1_000_000);
        System.out.println("\nFirst account after deposit and withdrawal:");
        System.out.println(firstAccount);

        firstAccount.transferTo(secondAccount, 960_000);
        System.out.println("\nAfter transfer of 9600.00 RUB to USD account:");
        System.out.println(firstAccount);
        System.out.println(secondAccount);

        firstAccount.changeCurrency(Currency.EUR);
        System.out.println("\nFirst account after currency change to EUR:");
        System.out.println(firstAccount);
        firstAccount.changeCurrency(Currency.RUB);

        System.out.println("\nTesting exceptions:");

        try {
            rubBalance.add(-500);
        } catch (IllegalArgumentException e) {
            System.out.println("Caught expected error: " + e.getMessage());
        }

        try {
            firstAccount.withdraw(3_000_000);
        } catch (IllegalStateException e) {
            System.out.println("Caught expected error: " + e.getMessage());
        }

        firstAccount.block();
        try {
            firstAccount.deposit(10_000);
        } catch (IllegalStateException e) {
            System.out.println("Caught expected error: " + e.getMessage());
        }
        firstAccount.unblock();

        try {
            firstAccount.transferTo(firstAccount, 1_000);
        } catch (IllegalArgumentException e) {
            System.out.println("Caught expected error: " + e.getMessage());
        }
    }
}