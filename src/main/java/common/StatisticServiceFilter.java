package common;

import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.lang.IgnitePredicate;

public class StatisticServiceFilter implements IgnitePredicate<ClusterNode> {

    @Override
    public boolean apply(ClusterNode clusterNode) {
        return !clusterNode.isClient();
    }
}