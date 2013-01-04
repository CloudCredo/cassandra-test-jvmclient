package com.cloudcredo.cassandra

import com.netflix.astyanax.AstyanaxContext
import com.netflix.astyanax.Keyspace
import com.netflix.astyanax.connectionpool.NodeDiscoveryType
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl
import com.netflix.astyanax.model.ColumnFamily
import com.netflix.astyanax.serializers.StringSerializer
import com.netflix.astyanax.thrift.ThriftFamilyFactory
import groovy.util.logging.Log4j
import org.cloudfoundry.runtime.env.CassandraServiceInfo
import org.cloudfoundry.runtime.env.CloudEnvironment


/**
 * @author: chris
 * @date: 17/12/2012
 */
@Log4j
class UserRepository {

    private final USER = new ColumnFamily("Standard1", StringSerializer.get(), StringSerializer.get())

    private port = 5041

    private host = "172.16.10.44"

    def connect() {

        setUpCloud()


        final config = new AstyanaxConfigurationImpl()

        final context = new AstyanaxContext.Builder()
                .forCluster("Test Cluster")
                .forKeyspace("Test")
                .withAstyanaxConfiguration(config
                .setDiscoveryType(NodeDiscoveryType.NONE))
                .withConnectionPoolConfiguration(new ConnectionPoolConfigurationImpl("MyConnectionPool")
                .setPort(port)
                .setMaxConnsPerHost(1)
                .setSeeds("${host}:${port}"))
                .withConnectionPoolMonitor(new CountingConnectionPoolMonitor())
                .buildKeyspace(ThriftFamilyFactory.getInstance());

        context.start();

        final keyspace = context.getEntity();

        createKeyspace(keyspace)
        createColumnFamily(keyspace)

        keyspace
    }

    def setUpCloud() {

        final env = new CloudEnvironment()
        final cassandraServiceInfos = env.getServiceInfos(CassandraServiceInfo)

        log.info("Found cassandra Service Info ${cassandraServiceInfos[0]}")
        log.info("Host: ${cassandraServiceInfos[0]?.getHost()}")
        log.info("Port: ${cassandraServiceInfos[0]?.getPort()}")
        log.info("ServiceName: ${cassandraServiceInfos[0]?.getServiceName()}")


        final port = cassandraServiceInfos[0]?.getPort()
        final host = cassandraServiceInfos[0]?.getHost()

        if (port) {
            this.port = port; log.info("Setting port to ${port}")
        }

        if (host) {
            this.host = host; log.info("Setting host to ${host}")
        }
    }

    def initData() {

        final keyspace = connect().prepareMutationBatch();

        keyspace.withRow(USER, "USER")
                .putColumn("firstname", "Chris", null)
                .putColumn("lastname", "Whatever", null)

        keyspace.execute();
    }

    def findUserByFirstName() {
        final result = connect().prepareQuery(USER).getKey("USER").execute();
        final columns = result.getResult();
        columns.getColumnByName("firstname").getStringValue()
    }

    private void createColumnFamily(Keyspace keyspace) {
        try {
            keyspace.createColumnFamily(USER, null)
        } catch (e) {
            //Need to test for existence of column family correctly
        }
    }

    private void createKeyspace(Keyspace keyspace) {
        final options = ["strategy_options": ["replication_factor": "1"], "strategy_class": "SimpleStrategy"]

        try {
            keyspace.createKeyspace(options)
        } catch (e) {
            //Need to test for existence of keyspace
        }
    }
}
