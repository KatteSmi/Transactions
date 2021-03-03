import java.util.HashMap;
import java.util.Random;

public class Bank {

    private HashMap<Integer, Account> accounts;
    private final Random random = new Random();
    private static final int POSSIBLE_FRAUD_AMOUNT = 50_000;

    {
        accounts = fillAccounts();
    }

    public long getTotalBalance() {
        return accounts.values().stream().mapToLong(Account::getBalance).sum();
    }

    private synchronized boolean isFraud(int fromAccountNum, int toAccountNum, int amount)
            throws InterruptedException {
        Thread.sleep(1000);
        return random.nextBoolean();
    }

    public void transfer(int fromAccountNum, int toAccountNum, int amount) throws InterruptedException {
        Account fromAccount = accounts.get(fromAccountNum);
        Account toAccount = accounts.get(toAccountNum);

        if (fromAccountNum == toAccountNum) {
            return;
        }

        boolean c = fromAccountNum > toAccountNum;

        synchronized (c ? fromAccount : toAccount) {
            synchronized (c ? toAccount : fromAccount) {
                if (fromAccount.isBlocked() || toAccount.isBlocked()) {
                    return;
                }

                if (amount > POSSIBLE_FRAUD_AMOUNT) {
                    if (isFraud(fromAccountNum, toAccountNum, amount)) {
                        fromAccount.blockAccount();
                        toAccount.blockAccount();
                    } else {
                        transaction(amount, fromAccount, toAccount);
                    }
                } else {
                    transaction(amount, fromAccount, toAccount);
                }
            }
        }
    }

    private void transaction(int amount, Account fromAccount, Account toAccount) {
        if (fromAccount.withdrawMoney(amount)) {
            toAccount.putMoney(amount);
        }
    }


    public long getBalance(int accountNum) {
        Account account = accounts.get(accountNum);
        return account.getBalance();
    }

    public void setAccounts(HashMap<Integer, Account> accounts) {
        this.accounts = accounts;
    }

    private static HashMap<Integer, Account> fillAccounts() {
        HashMap<Integer, Account> accountMap = new HashMap<>();
        for (int i = 1; i <= 100; i++) {
            long initialValue = (long) (80000 + 20000 * Math.random());
            Account account = new Account(i, initialValue);
            accountMap.put(i, account);
        }
        return accountMap;
    }
}
