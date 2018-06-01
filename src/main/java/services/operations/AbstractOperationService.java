package services.operations;

import org.apache.ignite.Ignite;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.services.Service;
import org.apache.ignite.services.ServiceContext;
import services.management.StatisticService;

public abstract class AbstractOperationService implements Service, OperationService {
    @IgniteInstanceResource
    private Ignite ignite;

    protected String svcName;

    protected StatisticService statSvc;

    @Override
    public void cancel(ServiceContext serviceContext) {
        System.out.println("Service was cancelled: " + svcName);
    }

    @Override
    public void init(ServiceContext serviceContext) throws Exception {
        svcName = serviceContext.name();
        statSvc = ignite.services().serviceProxy("StatisticService", StatisticService.class, /*not-sticky*/false);
        System.out.println("Service was initialized: " + svcName);
    }

    @Override
    public void execute(ServiceContext serviceContext) throws Exception {
        System.out.println("Executing distributed service: " + svcName);
    }
}
