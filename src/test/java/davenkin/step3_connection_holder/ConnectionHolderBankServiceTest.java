package davenkin.step3_connection_holder;

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
 * =========================================================================================================
 * 全局来共享connection对象
 * 1. dataSource放入ConnectionHodler里面
 * ++++
 * 2. TransactionManager ： 从ConnectionHolder里面取出Connection，来控制事务
 * 2.0 constructor -> ConnectionHolder.getConnection() -> 通常是在这个方法里面创建的Connection。
 * 2.1 transactionManager.start() -> connection.setAutoCommit(false)
 * 2.2 transactionManager.commit() -> connection.commit()
 * 2.3 transactionManager.rollback() -> connection.rollback()
 * 2.4 transactionManager.close() -> connection.close()
 * ++++
 * 3. 需要DML操作的函数 ： 从ConnectionHolder里面取出Connection，来执行操作；
 * 3.1 ConnectionHolder.getConnection(dataSource);
 * ++++
 * ConnectionHodler的实现 ：
 * 1. Map<DataSource, Connection> : 一个dataSource对应一个connection, 这样一个事务里面，同一个数据源就只能获取到一个connection
 * 2. 考虑到多线程问题，需要将ConnectionHolder做成线程安全
 * 2.1 SingleThreadConnectionHolder -> ThreadLocal<ConnectionHolder>
 * =========================================================================================================
 */
public class ConnectionHolderBankServiceTest extends BankFixture {

    @Test
    public void transferSuccess() throws SQLException {
        ConnectionHolderBankService connectionHolderBankService = new ConnectionHolderBankService(dataSource);
        connectionHolderBankService.transfer(1111, 2222, 200);

        assertEquals(800, getBankAmount(1111));
        assertEquals(1200, getInsuranceAmount(2222));

    }

    @Test
    public void transferFailure() throws SQLException {
        ConnectionHolderBankService connectionHolderBankService = new ConnectionHolderBankService(dataSource);

        int toNonExistId = 3333;
        connectionHolderBankService.transfer(1111, toNonExistId, 200);

        assertEquals(1000, getBankAmount(1111));
        assertEquals(1000, getInsuranceAmount(2222));

    }
}
