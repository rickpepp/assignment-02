package e1;

public class BaseDecoratorBankAccount implements BankAccount {

    private final BankAccount base;

    public BaseDecoratorBankAccount(BankAccount base) {
        this.base = base;
    }

    public int getBalance() {
        return base.getBalance();
    }

    public void deposit(int amount) {
        base.deposit(amount);
    }

    public void withdraw(int amount) {
        base.withdraw(amount);
    }
}
