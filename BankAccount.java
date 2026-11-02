import java.util.Objects;

public class BankAccount {
    private final String accountNumber;
    private final String ownerName;
    private Balance balance;
    private long operationLimit;
    private boolean blocked;

    public BankAccount(String accountNumber, String ownerName, Balance balance, long operationLimit) {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Account number cannot be empty");
        }
        if (ownerName == null || ownerName.isBlank()) {
            throw new IllegalArgumentException("Owner name cannot be empty");
        }
        if (balance == null) {
            throw new IllegalArgumentException("Balance cannot be null");
        }
        if (operationLimit <= 0) {
            throw new IllegalArgumentException("Operation limit must be positive");
        }
        this.accountNumber = accountNumber.strip();
        this.ownerName = ownerName.strip();
        this.balance = balance;
        this.operationLimit = operationLimit;
        this.blocked = false;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public Balance getBalance() {
        return balance;
    }

    public long getOperationLimit() {
        return operationLimit;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setOperationLimit(long operationLimit) {
        checkNotBlocked();
        if (operationLimit <= 0) {
            throw new IllegalArgumentException("Operation limit must be positive");
        }
        this.operationLimit = operationLimit;
    }

    public void block() {
        this.blocked = true;
    }

    public void unblock() {
        this.blocked = false;
    }

    public void deposit(long minorUnits) {
        checkNotBlocked();
        if (minorUnits <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        this.balance = this.balance.add(minorUnits);
    }

    public void deposit(Balance amount) {
        checkNotBlocked();
        if (amount == null || amount.getAmountInMinorUnits() <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive and not null");
        }
        Balance converted = amount.convertTo(this.balance.getCurrency());
        if (converted.getAmountInMinorUnits() == 0) {
            throw new IllegalArgumentException("Amount is too small for currency conversion");
        }
        this.balance = this.balance.add(converted);
    }

    public void withdraw(long minorUnits) {
        checkNotBlocked();
        if (minorUnits <= 0) {
            throw new IllegalArgumentException("Withdraw amount must be positive");
        }
        if (minorUnits > this.operationLimit) {
            throw new IllegalStateException("Operation limit exceeded");
        }
        this.balance = this.balance.subtract(minorUnits);
    }

    public void withdraw(Balance amount) {
        checkNotBlocked();
        if (amount == null || amount.getAmountInMinorUnits() <= 0) {
            throw new IllegalArgumentException("Withdraw amount must be positive and not null");
        }
        Balance converted = amount.convertTo(this.balance.getCurrency());
        withdraw(converted.getAmountInMinorUnits());
    }

    public void transferTo(BankAccount target, long minorUnits) {
        if (target == null) {
            throw new IllegalArgumentException("Target account cannot be null");
        }
        if (this.equals(target)) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
        this.checkNotBlocked();
        target.checkNotBlocked();

        if (minorUnits <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }

        Balance transferBalance = new Balance(minorUnits, this.balance.getCurrency());
        Balance convertedForTarget = transferBalance.convertTo(target.balance.getCurrency());

        if (convertedForTarget.getAmountInMinorUnits() == 0) {
            throw new IllegalArgumentException("Transfer amount is too small after conversion");
        }

        this.withdraw(minorUnits);
        target.deposit(convertedForTarget.getAmountInMinorUnits());
    }

    public void changeCurrency(Currency newCurrency) {
        checkNotBlocked();
        if (newCurrency == null) {
            throw new IllegalArgumentException("Currency cannot be null");
        }
        if (this.balance.getCurrency() == newCurrency) {
            return;
        }
        Balance currentLimit = new Balance(this.operationLimit, this.balance.getCurrency());
        long newLimit = currentLimit.convertTo(newCurrency).getAmountInMinorUnits();

        this.balance = this.balance.convertTo(newCurrency);
        this.operationLimit = Math.max(1L, newLimit);
    }

    private void checkNotBlocked() {
        if (this.blocked) {
            throw new IllegalStateException("Account " + accountNumber + " is blocked");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BankAccount that = (BankAccount) o;
        return Objects.equals(accountNumber, that.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }

    @Override
    public String toString() {
        return "BankAccount{" +
                "accountNumber='" + accountNumber + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", balance=" + balance +
                ", operationLimit=" + new Balance(operationLimit, balance.getCurrency()) +
                ", blocked=" + blocked +
                '}';
    }
}