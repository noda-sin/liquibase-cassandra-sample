plugins {
    kotlin("jvm") version "1.8.0"
    id("com.avast.gradle.docker-compose") version "0.17.4"
    id("org.liquibase.gradle") version "2.2.0"
    application
}

group = "com.sample"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    liquibaseRuntime("org.liquibase:liquibase-core:4.25.0")
    liquibaseRuntime("org.liquibase.ext:liquibase-cassandra:4.25.0.1")
    liquibaseRuntime("info.picocli:picocli:4.7.3")
}

liquibase {
    activities {
        create("cassandra") {
            arguments = mapOf(
                "changelogFile" to "changelog.yml",
                "logLevel" to "debug",
                "databaseClass" to "liquibase.ext.cassandra.database.CassandraDatabase",
                "url" to "jdbc:cassandra://localhost:9044/test?compliancemode=Liquibase&localdatacenter=datacenter1",
            )
        }
    }
}

dockerCompose {
    createNested("cassandra").apply {
        startedServices.set(listOf("cassandra"))
    }
}

tasks.register<JavaExec>("migrate") {
    tasks.named("liquibaseUpdate").get().dependsOn("cassandraComposeUp")
    dependsOn(tasks.named("liquibaseUpdate"))
    mainClass.set(application.mainClass)
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("com.example.MainKt")
}
