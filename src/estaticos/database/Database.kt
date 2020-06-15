package estaticos.database

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import estaticos.database.Database.operation.close
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement
import kotlin.system.exitProcess

class Database(
    val base: String?,
    val serverName: String?,
    val port: String?,
    val user: String?,
    val password: String?
) {
    private val logger = LoggerFactory.getLogger(Database::class.java) as Logger

    //connection
    var dataSource: HikariDataSource? = null
        private set
    object operation{
        val locker = Any()
        val logger = LoggerFactory.getLogger(Database::class.java) as Logger
        fun execute(statement: PreparedStatement) {
                var connection: Connection? = null
                try {
                    connection = statement.connection
                    statement.execute()
//                    logger.debug("SQL request executed successfully {}", statement.toString())
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    logger.error("Can't execute SQL Request :$statement", e)
                } finally {
                    close(statement)
                    close(connection)
                }
        }
        fun close(statement: PreparedStatement?) {
            if (statement == null) return
            try {
                if (!statement.isClosed) {
                    statement.clearParameters()
                    statement.close()
                }
            } catch (e: java.lang.Exception) {
//                logger.error("Can't stop statement", e)
            }
        }

        fun close(connection: Connection?) {
            if (connection == null) return
            try {
                if (!connection.isClosed) {
                    connection.close()
//                    logger.trace("{} released", connection)
                }
            } catch (e: java.lang.Exception) {
                logger.error("Can't stop connection", e)
            }
        }

        fun close(statement: Statement?) {
            if (statement == null) return
            try {
                if (!statement.isClosed) statement.close()
            } catch (e: java.lang.Exception) {
                logger.error("Can't stop statement", e)
            }
        }

        fun close(resultSet: ResultSet?) {
            if (resultSet == null) return
            try {
                if (!resultSet.isClosed) resultSet.close()
            } catch (e: java.lang.Exception) {
                logger.error("Can't stop resultSet", e)
            }
        }

        fun close(result: Result?) {
            if (result != null) {
                close(result.resultSet)
                if (result.connection != null) close(result.connection)
//                logger.trace("Connection {} has been released", result.connection)
            }
        }
    }

    fun initializeConnection(): Boolean {
        try {
//            logger.level = Level.ALL
//            logger.trace("Reading database config")
            val config = HikariConfig()
            config.dataSourceClassName = "org.mariadb.jdbc.MySQLDataSource"
            config.addDataSourceProperty("serverName", serverName)
            config.addDataSourceProperty("port", port)
            config.addDataSourceProperty("databaseName", base)
            config.addDataSourceProperty("user", user)
            config.addDataSourceProperty("password", password)
            config.isAutoCommit = true // AutoCommit, c'est cool
            config.maximumPoolSize = 20
            dataSource = HikariDataSource(config)
            if (this.tryConnection(dataSource!!)) {
                logger.error("Please verify your username and password and database connection")
                return true
            }
//            logger.info("Database connection established")
//            logger.info("Database data loaded")
        } catch (e: Exception) {
            e.printStackTrace()
            return true
        }
        return false
    }

    fun AbstractDAO(dataSource: HikariDataSource?) {
        this.dataSource = dataSource
        logger.level = Level.OFF
    }
    fun execute(query: String) {
            var connection: Connection? = null
            var statement: Statement? = null
            try {
                connection = dataSource!!.connection
                statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)
                statement.execute(query)
//                logger.debug("SQL request executed successfully {}", query)
            } catch (e: java.lang.Exception) {
                logger.error("Can't execute SQL Request :$query", e)
            } finally {
                close(statement)
                close(connection)
            }
    }


    fun getData(query: String): Result? {
        var query = query
            val connection: Connection?
            try {
                if (!query.endsWith(";")) query = "$query;"
                connection = dataSource!!.connection
                val statement =
                    connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)
                val result = Result(connection, statement.executeQuery(query))
//                logger.debug("SQL request executed successfully {}", query)
                return result
            } catch (e: java.lang.Exception) {
                logger.error("Can't execute SQL Request :$query", e)
            }
            return null
    }

    fun getPreparedStatement(query: String?): PreparedStatement? {
        return try {
            val connection = dataSource!!.connection
            connection.prepareStatement(query)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            logger.error("Can't getWaitingAccount datasource connection", e)
            dataSource!!.close()
            if (this.initializeConnection()) exitProcess(1)
            null
        }
    }


    fun sendError(msg: String, e: java.lang.Exception) {
        e.printStackTrace()
        logger.error("Error $base database " + msg + " : " + e.message)
    }

    class Result(val connection: Connection?, val resultSet: ResultSet)

    fun tryConnection(dataSource: HikariDataSource): Boolean {
        return try {
            val connection = dataSource.connection
            connection.close()
            false
        } catch (e: Exception) {
            e.printStackTrace()
            true
        }
    }
}