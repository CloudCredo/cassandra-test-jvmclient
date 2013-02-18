package com.cloudcredo.cassandra
import org.cloudfoundry.runtime.env.CassandraServiceInfo
import org.cloudfoundry.runtime.env.CloudEnvironment
import org.junit.Test
import spock.lang.Specification
/**
 * @author: chris
 * @date: 23/01/2013
 */
class CassandraServiceInfoTest extends Specification {

    final serviceName = "cassandra-1"
    final hostname = "hostname"
    final port = 8080
    final password = "password"
    final username = "username"
    final clusterName = "clusterName"

    @Test
    void "should find CassandraServiceInfo from the cassandra-provider found on the classpath"() {

        given: "a CloudEnvironment with a valid ServiceInfo provider on the classpath"
        CloudEnvironment env = new CloudEnvironment()

        and: "a n EnvironmentAccessor that can hit the classpath for the CloudFoundry environment"
        final environment = Mock(CloudEnvironment.EnvironmentAccessor)
        env.environment = environment

        when: "the CassandraServiceInfo is requested for the service name"
        final serviceInfo = env.getServiceInfo(serviceName, CassandraServiceInfo)

        then: "the environment should return the Cassandra CloudFoundry environment"
        environment.getValue("VCAP_SERVICES") >> getCassandraCloudFoundryEnvironment()

        and: "the CassandraServiceInfo should be hydrated with appropriate data from the Cassandra CloudFoundry environment"
        assert serviceInfo.class == CassandraServiceInfo
        assert serviceName == serviceInfo.getServiceName()
        assert hostname == serviceInfo.getHost();
        assert port, serviceInfo.getPort()
        assert clusterName, serviceInfo.getClusterName()
        assert password, serviceInfo.getPassword()
        assert username, serviceInfo.getUserName()
    }

    private getCassandraCloudFoundryEnvironment() {
        """
{"redis-2.2":[{
        "name": "${serviceName}",
        "label": "cassandra-1.1.6",
        "plan": "free",
        "credentials": {
            "node_id": "redis_node_8",
            "hostname": "${hostname}",
            "port": ${port},
            "password": "${password}",
            "name": "${clusterName}"
        }
}]}
"""
    }
}
