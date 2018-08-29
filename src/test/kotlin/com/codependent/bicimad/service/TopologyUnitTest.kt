package com.codependent.bicimad.service

import com.codependent.bicimad.dto.BiciMadStation
import com.codependent.bicimad.serdes.JsonPojoSerializer
import com.codependent.bicimad.streams.STATIONS_BY_NAME_STORE
import com.codependent.bicimad.streams.STATIONS_STORE
import com.codependent.bicimad.streams.STATIONS_TOPIC
import com.codependent.bicimad.streams.StreamsConfiguration
import org.apache.kafka.common.serialization.IntegerSerializer
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.TopologyTestDriver
import org.apache.kafka.streams.state.KeyValueStore
import org.apache.kafka.streams.test.ConsumerRecordFactory
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.*


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TopologyUnitTest {

    private val config = Properties()
    private val streamsConfiguration = StreamsConfiguration("test", "dummy:1234")
    private val recordFactory: ConsumerRecordFactory<Int, BiciMadStation>
    private val testDriver: TopologyTestDriver
    private var stationsStore: KeyValueStore<Int, BiciMadStation>
    private var stationsByNameStore: KeyValueStore<String, BiciMadStation>

    init {
        config[StreamsConfig.APPLICATION_ID_CONFIG] = "test"
        config[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = "dummy:1234"
        recordFactory = ConsumerRecordFactory<Int, BiciMadStation>(STATIONS_TOPIC, IntegerSerializer(), JsonPojoSerializer<BiciMadStation>())
        testDriver = TopologyTestDriver(streamsConfiguration.topology(), config)
        stationsStore = testDriver.getKeyValueStore(STATIONS_STORE)
        stationsByNameStore = testDriver.getKeyValueStore(STATIONS_BY_NAME_STORE)
    }

    @BeforeEach
    fun initializeTestDriver() {

        stationsStore = testDriver.getKeyValueStore(STATIONS_STORE)
        stationsByNameStore = testDriver.getKeyValueStore(STATIONS_BY_NAME_STORE)
    }

    @AfterEach
    fun tearDown() {
        testDriver.close()
    }

    @Test
    fun testShouldAddStationsToStore() {
        val station = BiciMadStation(1, "40.416896", "-3.7024255", "Puerta del Sol A", 1, "1a", "Puerta del Sol nº 1", 1, 0, 24, 16, 5, 1)
        testDriver.pipeInput(recordFactory.create(STATIONS_TOPIC, 1, station))
        assertEquals(station, stationsStore.get(1))
        assertEquals(station, stationsByNameStore.get("Puerta del Sol A"))
    }
}