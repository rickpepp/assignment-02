package e1;

import java.util.function.Function;

public class WithdrawFunctionFeeDecorator extends BaseDecoratorBankAccount {

    private final Function<Integer, Integer> feeFunction;

    public WithdrawFunctionFeeDecorator(BankAccount base, Function<Integer, Integer> feeFunction) {
        super(base);
        this.feeFunction = feeFunction;
    }

    @Override
    public void withdraw(int amount) {
        super.withdraw(amount + this.feeFunction.apply(amount));
    }
}
