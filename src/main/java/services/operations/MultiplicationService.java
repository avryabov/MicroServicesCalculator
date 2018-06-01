package services.operations;

public class MultiplicationService extends AbstractOperationService {
    @Override
    public Double calc(Double a, Double b) {
        statSvc.refresh(svcName);
        return a * b;
    }
}
