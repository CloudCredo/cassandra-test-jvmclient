package com.cloudcredo.cassandra

import com.netflix.astyanax.Keyspace
import org.junit.Ignore
import spock.lang.Specification

/**
 * @author: chris
 * @date: 17/12/2012
 */
@Ignore("nned to look into embedding Cassandra...")
class UserRepositoryTest extends Specification {

    private final CASSANDRA_HOST = "172.16.10.44"
    private final CASSANDRA_PORT = 5041


    def "should connect to local cassandra"() {

        given: "A user repostory"
        final unit = new UserRepository(port: CASSANDRA_PORT, host: CASSANDRA_HOST)

        when: "a connection is requested"
        final actual = unit.connect()

        then: "a valid keystore should be returned"
        assert actual instanceof Keyspace
    }

    def "should write and read data to users column family"() {

        given: "A User repository"
        final unit = new UserRepository(port: CASSANDRA_PORT, host: CASSANDRA_HOST)

        when: "Data is created"
        unit.initData()

        then: "The data should be read back successfully"
        assert "Chris" == unit.findUserByFirstName()
    }
}
