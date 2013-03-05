package com.cloudcredo.repositories

import com.netflix.astyanax.AstyanaxContext
import com.netflix.astyanax.AuthenticationCredentials
import com.netflix.astyanax.Keyspace
import com.netflix.astyanax.connectionpool.NodeDiscoveryType
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor
import com.netflix.astyanax.connectionpool.impl.SimpleAuthenticationCredentials
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
class CassandraRepository {

    private final USER = new ColumnFamily("Standard1", StringSerializer.get(), StringSerializer.get())

    def port = 9160

    def host = "localhost"

    private AuthenticationCredentials credentials

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
                .setAuthenticationCredentials(credentials)
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


        final port = cassandraServiceInfos[0]?.getPort()
        final host = cassandraServiceInfos[0]?.getHost()

        if (port) {
            this.port = port; log.info("Setting port to ${port}")
        }

        if (host) {
            this.host = host; log.info("Setting host to ${host}")
        }

        final username = cassandraServiceInfos[0]?.getUserName()
        final password = cassandraServiceInfos[0]?.getPassword()
        credentials = new SimpleAuthenticationCredentials(username, password)

        log.info("Found repositories Service Info ${cassandraServiceInfos[0]}")
        log.info("Host: ${host}")
        log.info("Port: ${port}")
        log.info("ServiceName: ${cassandraServiceInfos[0]?.getServiceName()}")
        log.info("Username: ${username}")
        log.info("Password: ${password}")
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
