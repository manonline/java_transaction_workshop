package davenkin.step5_transaction_proxy;

import davenkin.BankFixture;
import davenkin.BankService;
import davenkin.step3_connection_holder.TransactionManager;
import org.junit.Test;

import java.sql.SQLException;

import static junit.framework.Assert.assertEquals;
/**
 * Created with IntelliJ IDEA.
 * User: davenkin
 * Date: 2/7/13
 * Time: 8:23 PM
 * To change this template use File | Settings | File Templates.
 * =============================================================
 * 所有transactionManager相关操作均抽象到代理中；
 *
 */
public class BareBankServiceTest extends BankFixture {
    @Test
    public void transferSuccess() throws SQLException {
        TransactionManager transactionManager = new TransactionManager(dataSource);
        ProxyEnabledTransactionManager proxyEnabledTransactionManager = new ProxyEnabledTransactionManager(transactionManager);
        BareBankService bankService = new BareBankService(dataSource);
        BankService proxyBankService = (BankService) proxyEnabledTransactionManager.proxyFor(bankService);
        proxyBankService.transfer(1111, 2222, 200);

        assertEquals(800, getBankAmount(1111));
        assertEquals(1200, getInsuranceAmount(2222));

    }

    @Test
    public void transferFailure() throws SQLException {
        ProxyEnabledTransactionManager proxyEnabledTransactionManager = new ProxyEnabledTransactionManager(new TransactionManager(dataSource));
        Object bankService = new BareBankService(dataSource);
        BankService proxyBankService = (BankService) proxyEnabledTransactionManager.proxyFor(bankService);

        int toNonExistId = 3333;
        proxyBankService.transfer(1111, toNonExistId, 200);

        assertEquals(1000, getBankAmount(1111));
        assertEquals(1000, getInsuranceAmount(2222));
    }
}
