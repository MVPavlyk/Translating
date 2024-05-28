package com.example.translating.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource

@Configuration
class JdbcConfig {

    @Bean
    fun dataSource(): DataSource {
        val dataSource = DriverManagerDataSource()
        dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver")
        dataSource.url = "jdbc:sqlserver://buspark.database.windows.net:1433;database=BusParkDB;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"
        dataSource.username = "busAdmin@buspark"
        dataSource.password = "20TarasFake"
        return dataSource
    }

    @Bean
    fun jdbcTemplate(dataSource: DataSource): JdbcTemplate {
        return JdbcTemplate(dataSource)
    }
}
