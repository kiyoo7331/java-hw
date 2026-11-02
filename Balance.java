import java.util.Objects;

public final class Balance {
    private final long amountInMinorUnits;
    private final Currency currency;

    public Balance(long amountInMinorUnits, Currency currency) {
        if (currency == null) {
            throw new IllegalArgumentException("Currency cannot be null");
        }
        if (amountInMinorUnits < 0) {
            throw new IllegalArgumentException("Amount cannot be negative: " + amountInMinorUnits);
        }
        this.amountInMinorUnits = amountInMinorUnits;
        this.currency = currency;
    }

    public Balance() {
        this(0L, Currency.RUB);
    }

    public long getAmountInMinorUnits() {
        return amountInMinorUnits;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Balance add(long minorUnits) {
        if (minorUnits < 0) {
            throw new IllegalArgumentException("Cannot add negative amount: " + minorUnits);
        }
        long result = Math.addExact(this.amountInMinorUnits, minorUnits);
        return new Balance(result, this.currency);
    }

    public Balance add(Balance other) {
        if (other == null) {
            throw new IllegalArgumentException("Balance to add cannot be null");
        }
        Balance converted = other.convertTo(this.currency);
        return add(converted.amountInMinorUnits);
    }

    public Balance subtract(long minorUnits) {
        if (minorUnits < 0) {
            throw new IllegalArgumentException("Cannot subtract negative amount: " + minorUnits);
        }
        if (minorUnits > this.amountInMinorUnits) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        return new Balance(this.amountInMinorUnits - minorUnits, this.currency);
    }

    public Balance subtract(Balance other) {
        if (other == null) {
            throw new IllegalArgumentException("Balance to subtract cannot be null");
        }
        Balance converted = other.convertTo(this.currency);
        return subtract(converted.amountInMinorUnits);
    }

    public Balance convertTo(Currency targetCurrency) {
        if (targetCurrency == null) {
            throw new IllegalArgumentException("Target currency cannot be null");
        }
        if (this.currency == targetCurrency) {
            return this;
        }
        long totalRubKopecks = Math.multiplyExact(
                this.amountInMinorUnits,
                this.currency.getPriceInRublesKopecks()
        );
        long convertedAmount = totalRubKopecks / targetCurrency.getPriceInRublesKopecks();
        return new Balance(convertedAmount, targetCurrency);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Balance balance = (Balance) o;
        return amountInMinorUnits == balance.amountInMinorUnits && currency == balance.currency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amountInMinorUnits, currency);
    }

    @Override
    public String toString() {
        long major = amountInMinorUnits / 100;
        long minor = amountInMinorUnits % 100;
        return String.format("%d.%02d %s", major, minor, currency.getSymbol());
    }
}