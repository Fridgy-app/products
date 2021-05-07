package me.rasztabiga.fridgy.products.config

import com.hazelcast.config.*
import com.hazelcast.core.Hazelcast
import com.hazelcast.core.HazelcastInstance
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.boot.info.BuildProperties
import org.springframework.boot.info.GitProperties
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.cloud.client.serviceregistry.Registration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles
import tech.jhipster.config.JHipsterConstants
import tech.jhipster.config.JHipsterProperties
import tech.jhipster.config.cache.PrefixedKeyGenerator
import javax.annotation.PreDestroy

@Configuration
@EnableCaching
class CacheConfiguration(
    @Autowired val gitProperties: GitProperties?,
    @Autowired val buildProperties: BuildProperties?,
    private val env: Environment,
    private val serverProperties: ServerProperties,
    private val discoveryClient: DiscoveryClient,
    @Autowired(required = false) val registration: Registration?
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @PreDestroy
    fun destroy() {
        log.info("Closing Cache Manager")
        Hazelcast.shutdownAll()
    }

    @Bean
    fun cacheManager(hazelcastInstance: HazelcastInstance): CacheManager {
        log.debug("Starting HazelcastCacheManager")
        return com.hazelcast.spring.cache.HazelcastCacheManager(hazelcastInstance)
    }

    @Bean
    fun hazelcastInstance(jHipsterProperties: JHipsterProperties): HazelcastInstance {
        log.debug("Configuring Hazelcast")
        val hazelCastInstance = Hazelcast.getHazelcastInstanceByName("products")
        if (hazelCastInstance != null) {
            log.debug("Hazelcast already initialized")
            return hazelCastInstance
        }
        val config = Config()
        config.instanceName = "products"
        config.networkConfig.join.multicastConfig.isEnabled = false
        if (registration == null) {
            log.warn("No discovery service is set up, Hazelcast cannot create a cluster.")
        } else {
            // The serviceId is by default the application's name,
            // see the "spring.application.name" standard Spring property
            val serviceId = registration!!.serviceId
            log.debug("Configuring Hazelcast clustering for instanceId: $serviceId")
            // In development, everything goes through 127.0.0.1, with a different port
            if (env.acceptsProfiles(Profiles.of(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT))) {
                log.debug("Application is running with the \"dev\" profile, Hazelcast cluster will only work with localhost instances")

                config.networkConfig.port = serverProperties.port!! + 5701
                config.networkConfig.join.tcpIpConfig.isEnabled = true
                for (instance in discoveryClient.getInstances(serviceId)) {
                    val clusterMember = "127.0.0.1:" + (instance.port + 5701)
                    log.debug("Adding Hazelcast (dev) cluster member $clusterMember")
                    config.networkConfig.join.tcpIpConfig.addMember(clusterMember)
                }
            } else { // Production configuration, one host per instance all using port 5701
                config.networkConfig.port = 5701
                config.networkConfig.join.tcpIpConfig.isEnabled = true
                for (instance in discoveryClient.getInstances(serviceId)) {
                    val clusterMember = instance.host + ":5701"
                    log.debug("Adding Hazelcast (prod) cluster member $clusterMember")
                    config.networkConfig.join.tcpIpConfig.addMember(clusterMember)
                }
            }
        }

        config.managementCenterConfig = ManagementCenterConfig()
        config.addMapConfig(initializeDefaultMapConfig(jHipsterProperties))
        config.addMapConfig(initializeDomainMapConfig(jHipsterProperties))
        return Hazelcast.newHazelcastInstance(config)
    }

    private fun initializeDefaultMapConfig(jHipsterProperties: JHipsterProperties): MapConfig {
        val mapConfig = MapConfig("default")

        /*
        Number of backups. If 1 is set as the backup-count for example,
        then all entries of the map will be copied to another JVM for
        fail-safety. Valid numbers are 0 (no backup), 1, 2, 3.
        */
        mapConfig.backupCount = jHipsterProperties.cache.hazelcast.backupCount

        /*
        Valid values are:
        NONE (no eviction),
        LRU (Least Recently Used),
        LFU (Least Frequently Used).
        NONE is the default.
        */
        mapConfig.evictionConfig.evictionPolicy = EvictionPolicy.LRU

        /*
        Maximum size of the map. When max size is reached,
        map is evicted based on the policy defined.
        Any integer between 0 and Integer.MAX_VALUE. 0 means
        Integer.MAX_VALUE. Default is 0.
        */
        mapConfig.evictionConfig.maxSizePolicy = MaxSizePolicy.USED_HEAP_SIZE

        return mapConfig
    }

    private fun initializeDomainMapConfig(jHipsterProperties: JHipsterProperties): MapConfig =
        MapConfig("me.rasztabiga.fridgy.products.domain.*").apply { timeToLiveSeconds = jHipsterProperties.cache.hazelcast.timeToLiveSeconds }

    @Bean
    fun keyGenerator() = PrefixedKeyGenerator(gitProperties, buildProperties)
}
