package e1;

public class OverDraftDecorator extends BaseDecoratorBankAccount {

    private final int overdraft;

    public OverDraftDecorator(BankAccount base, int overdraft) {
        super(base);
        this.overdraft = overdraft;
    }

    @Override
    public void withdraw(int amount) {
        if (amount > super.getBalance() + overdraft)
            throw new IllegalStateException();
        super.withdraw(amount);
    }
}
