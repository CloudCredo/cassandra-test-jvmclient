package com.cloudcredo.cassandra

import com.netflix.astyanax.AstyanaxContext
import com.netflix.astyanax.connectionpool.NodeDiscoveryType
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl
import com.netflix.astyanax.model.ColumnFamily
import com.netflix.astyanax.serializers.StringSerializer
import com.netflix.astyanax.thrift.ThriftFamilyFactory


/**
 * @author: chris
 * @date: 17/12/2012
 */
class UserRepository {

    private final USER =  new ColumnFamily("Standard1", StringSerializer.get(), StringSerializer.get())

    def connect() {
        final config = new AstyanaxConfigurationImpl()

        final context = new AstyanaxContext.Builder()
                .forCluster("Test Cluster")
                .forKeyspace("RandomKeySpace")
                .withAstyanaxConfiguration(config
                .setDiscoveryType(NodeDiscoveryType.NONE))
                .withConnectionPoolConfiguration(new ConnectionPoolConfigurationImpl("MyConnectionPool")
                .setPort(9160)
                .setMaxConnsPerHost(1)
                .setSeeds("127.0.0.1:9160"))
                .withConnectionPoolMonitor(new CountingConnectionPoolMonitor())
                .buildKeyspace(ThriftFamilyFactory.getInstance());

        context.start();

        final keyspace = context.getEntity();

        final options = ["strategy_options": ["replication_factor": "1"], "strategy_class": "SimpleStrategy"]
        keyspace.createKeyspace(options)


        keyspace
    }

    def initData() {

        final keyspace = connect().prepareMutationBatch();

        keyspace.withRow(USER, "USER")
                .putColumn("firstname", "Chris", null)
                .putColumn("lastname", "Whatever", null)

        keyspace.execute();
    }

    def findUserByFirstName() {
        final result = connect().prepareQuery(USER).getKey("Key1").execute();
        final columns = result.getResult();
        columns.getColumnByName("firstname").getStringValue()
    }
}
