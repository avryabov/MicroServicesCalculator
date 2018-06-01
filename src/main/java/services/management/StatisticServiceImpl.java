package services.management;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.services.Service;
import org.apache.ignite.services.ServiceContext;

import java.util.concurrent.atomic.AtomicInteger;

public class StatisticServiceImpl implements Service, StatisticService {
    @IgniteInstanceResource
    private Ignite ignite;

    private String svcName;

    private IgniteCache<String, AtomicInteger> operationsCache;

    @Override
    public void cancel(ServiceContext serviceContext) {
        System.out.println("Service was cancelled: " + svcName);
    }

    @Override
    public void init(ServiceContext serviceContext) throws Exception {
        svcName = serviceContext.name();
        operationsCache = ignite.cache("operations");
        System.out.println("Service was initialized: " + svcName);
    }

    @Override
    public void execute(ServiceContext serviceContext) throws Exception {
        System.out.println("Executing distributed service: " + svcName);
    }

    @Override
    public void refresh(String key) {
        AtomicInteger num = operationsCache.get(key);
        if (num == null)
            return;
        num.incrementAndGet();
        operationsCache.put(key, num);
        System.out.println("Now " + key + " = " + num);
    }
}
