package davenkin.step5_transaction_proxy;

import davenkin.step3_connection_holder.TransactionManager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxyEnabledTransactionManager {
    private TransactionManager transactionManager;

    public ProxyEnabledTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public Object proxyFor(Object object) {
        return Proxy.newProxyInstance(object.getClass().getClassLoader(), object.getClass().getInterfaces(), new TransactionInvocationHandler(object, transactionManager));
    }
}

class TransactionInvocationHandler implements InvocationHandler {
    private Object proxy;
    private TransactionManager transactionManager;

    TransactionInvocationHandler(Object object, TransactionManager transactionManager) {
        this.proxy = object;
        this.transactionManager = transactionManager;
    }

    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        Object result = null;

        transactionManager.start();
        try {
            result = method.invoke(proxy, objects);
            transactionManager.commit();
        } catch (Exception e) {
            transactionManager.rollback();
        } finally {
            transactionManager.close();
        }
        return result;
    }
}