package e1;

public class BankAccountFactory {

    public static final int GOLD_ACCOUNT_OVERDRAFT = 500;
    public static final int FEE_BRONZE_AND_SILVER = 1;
    public static final int WITHDRAW_LIMIT_NO_FEE_ACCOUNT_BRONZE = 100;

    public BankAccount createSilverBankAccount() {
        BankAccount coreAccount = createBasicAccount();
        coreAccount = new WithdrawFunctionFeeDecorator(coreAccount,
                amountWithdraw -> FEE_BRONZE_AND_SILVER);
        coreAccount = new OverDraftDecorator(coreAccount, 0);
        return coreAccount;
    }

    private static BankAccount createBasicAccount() {
        BankAccount coreAccount = new CoreBankAccount();
        coreAccount = new BaseDecoratorBankAccount(coreAccount);
        return coreAccount;
    }

    public BankAccount createGoldBankAccount() {
        BankAccount coreAccount = createBasicAccount();
        coreAccount = new OverDraftDecorator(coreAccount, GOLD_ACCOUNT_OVERDRAFT);
        return coreAccount;
    }

    public BankAccount createBronzeBankAccount() {
        BankAccount coreAccount = createBasicAccount();
        coreAccount = new WithdrawFunctionFeeDecorator(coreAccount,
                amountWithdraw -> (amountWithdraw < WITHDRAW_LIMIT_NO_FEE_ACCOUNT_BRONZE) ? 0 : FEE_BRONZE_AND_SILVER);
        coreAccount = new OverDraftDecorator(coreAccount, 0);
        return coreAccount;
    }
}
