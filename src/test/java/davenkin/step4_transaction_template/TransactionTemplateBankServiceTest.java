package davenkin.step4_transaction_template;

import davenkin.BankFixture;
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
 * 所有transactionManager相关操作均抽象到模版当中；
 * TransactionManager实际存在在模版父类当中，子类种方法，只需执行业务逻辑，父类TransactionManager进行事务控制；
 *
 */
public class TransactionTemplateBankServiceTest extends BankFixture {
    @Test
    public void transferSuccess() throws SQLException {
        TransactionTemplateBankService transactionTemplateBankService = new TransactionTemplateBankService(dataSource);
        transactionTemplateBankService.transfer(1111, 2222, 200);

        assertEquals(800, getBankAmount(1111));
        assertEquals(1200, getInsuranceAmount(2222));

    }

    @Test
    public void transferFailure() throws SQLException {
        TransactionTemplateBankService transactionTemplateBankService = new TransactionTemplateBankService(dataSource);

        int toNonExistId = 3333;
        transactionTemplateBankService.transfer(1111, toNonExistId, 200);

        assertEquals(1000, getBankAmount(1111));
        assertEquals(1000, getInsuranceAmount(2222));
    }
}
