package common;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.lang.IgnitePredicate;
import org.apache.ignite.resources.IgniteInstanceResource;

import javax.cache.Cache;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

public class OperationServiceFilter implements IgnitePredicate<ClusterNode> {

    @IgniteInstanceResource
    private Ignite ignite;

    private String name;

    public OperationServiceFilter(String name) {
        this.name = name;
    }

    @Override
    public boolean apply(ClusterNode clusterNode) {
        Boolean dataNode = clusterNode.attribute("operation.service.node");
        return !clusterNode.isClient() && mustDeploy() && dataNode;
    }

    private boolean mustDeploy() {
        IgniteCache<String, AtomicInteger> operationsCache = ignite.cache("operations");
        AtomicInteger num = operationsCache.getAndPutIfAbsent(name, new AtomicInteger(0));
        if (num == null)
            return true;

        Iterator<Cache.Entry<String, AtomicInteger>> iter = operationsCache.iterator();
        int max = 0;
        int min = 0;
        while (iter.hasNext()) {
            int value = iter.next().getValue().get();
            if (value > max)
                max = value;
            if (value < min)
                min = value;
        }
        if (num.get() > (max - min) * 0.38)
            return true;
        return false;
    }
}
