package ru.gx.fin.quik.dbadapter;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import ru.gx.core.channels.ChannelDirection;
import ru.gx.core.data.ActiveConnectionsContainer;
import ru.gx.core.events.DataEvent;
import ru.gx.core.kafka.KafkaConstants;
import ru.gx.core.kafka.load.KafkaIncomeTopicsLoader;
import ru.gx.core.kafka.load.KafkaIncomeTopicsOffsetsController;
import ru.gx.core.kafka.load.SimpleKafkaIncomeTopicsConfiguration;
import ru.gx.core.kafka.offsets.TopicPartitionOffset;
import ru.gx.core.kafka.offsets.TopicsOffsetsLoader;
import ru.gx.core.kafka.offsets.TopicsOffsetsSaver;
import ru.gx.core.simpleworker.SimpleWorker;
import ru.gx.core.simpleworker.SimpleWorkerOnIterationExecuteEvent;
import ru.gx.core.simpleworker.SimpleWorkerOnStartingExecuteEvent;
import ru.gx.core.simpleworker.SimpleWorkerOnStoppingExecuteEvent;
import ru.gx.fin.quik.events.LoadedAllTradesEvent;
import ru.gx.fin.quik.events.LoadedDealsEvent;
import ru.gx.fin.quik.events.LoadedOrdersEvent;
import ru.gx.fin.quik.events.LoadedSecuritiesEvent;

import javax.sql.DataSource;
import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.util.ArrayList;

import static lombok.AccessLevel.PROTECTED;

@Slf4j
public class DbAdapter {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Constants">
    private static final String callSaveAllTrades = "CALL \"Quik\".\"AllTrades@SavePackage\"(?)";
    private static final String callSaveOrders = "CALL \"Quik\".\"Orders@SavePackage\"(?)";
    private static final String callSaveDeals = "CALL \"Quik\".\"Deals@SavePackage\"(?)";
    private static final String callSaveSecurities = "CALL \"Quik\".\"Securities@SavePackage\"(?)";
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields">
    private final String serviceName;

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private DataSource dataSource;

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private ActiveConnectionsContainer connectionsContainer;

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private SimpleWorker simpleWorker;

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private DbAdapterSettingsContainer settings;

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private SimpleKafkaIncomeTopicsConfiguration incomeTopicsConfiguration;

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private KafkaIncomeTopicsLoader incomeTopicsLoader;

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private KafkaIncomeTopicsOffsetsController incomeTopicsOffsetsController;

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private TopicsOffsetsLoader topicsOffsetsLoader;

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private TopicsOffsetsSaver topicsOffsetsSaver;
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Initialization">

    public DbAdapter(String serviceName) {
        super();
        this.serviceName = serviceName;
    }

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Обработка событий Worker-а">

    private void saveData(
            @NotNull final String callProc,
            @NotNull final DataEvent event,
            @NotNull final String topicName) {
        final var data = event.getData();
        if (!(data instanceof final String jsonData)) {
            throw new InvalidParameterException("Parameter data isn't instance of String!");
        }
        final var timeStarted = System.currentTimeMillis();
        final var connection = this.connectionsContainer.getCurrent();
        try {
            if (connection == null) {
                throw new SQLException("Connection doesn't opened!");
            }
            try (final var stmt = this.connectionsContainer.getCurrent().prepareCall(callProc)) {
                this.simpleWorker.runnerIsLifeSet();
                stmt.setString(1, jsonData);
                stmt.execute();
                log.debug("Executed: {} in {} ms", callProc, System.currentTimeMillis() - timeStarted);
            }

            final var partition = event.getMetadataValue(KafkaConstants.METADATA_PARTITION);
            final var offset = event.getMetadataValue(KafkaConstants.METADATA_OFFSET);
            if (partition instanceof Integer && offset instanceof Long) {
                final var tps = new TopicPartitionOffset(
                        topicName,
                        (Integer) partition,
                        (Long) offset
                );
                final var tpsArray = new ArrayList<TopicPartitionOffset>();
                tpsArray.add(tps);
                this.topicsOffsetsSaver.saveOffsets(ChannelDirection.In, this.serviceName, tpsArray);
            }
        } catch (SQLException e) {
            log.error("", e);
        }
    }


    /**
     * Обработка события о начале работы цикла итераций.
     *
     * @param event Объект-событие с параметрами.
     */
    @SneakyThrows(SQLException.class)
    @SuppressWarnings("unused")
    @EventListener(SimpleWorkerOnStartingExecuteEvent.class)
    public void startingExecute(SimpleWorkerOnStartingExecuteEvent event) {
        log.debug("Starting startingExecute()");

        try (var connection = getDataSource().getConnection()) {
            this.connectionsContainer.putCurrent(connection);
            final var offsets = this.topicsOffsetsLoader.loadOffsets(ChannelDirection.In, this.incomeTopicsConfiguration.getConfigurationName());
            if (offsets.size() <= 0) {
                this.incomeTopicsOffsetsController.seekAllToBegin(this.incomeTopicsConfiguration);
            } else {
                this.incomeTopicsOffsetsController.seekTopicsByList(this.incomeTopicsConfiguration, offsets);
            }
        } finally {
            this.connectionsContainer.putCurrent(null);
        }

        log.debug("Finished startingExecute()");
    }

    /**
     * Обработка события об окончании работы цикла итераций.
     *
     * @param event Объект-событие с параметрами.
     */
    @SuppressWarnings("unused")
    @EventListener(SimpleWorkerOnStoppingExecuteEvent.class)
    public void stoppingExecute(SimpleWorkerOnStoppingExecuteEvent event) {
        log.debug("Starting stoppingExecute()");
        log.debug("Finished stoppingExecute()");
    }

    /**
     * Обработчик итераций.
     *
     * @param event Объект-событие с параметрами итерации.
     */
    @EventListener(SimpleWorkerOnIterationExecuteEvent.class)
    public void iterationExecute(SimpleWorkerOnIterationExecuteEvent event) {
        log.debug("Starting iterationExecute()");
        try {
            this.simpleWorker.runnerIsLifeSet();
            event.setImmediateRunNextIteration(false);

            try (var connection = getDataSource().getConnection()) {
                this.connectionsContainer.putCurrent(connection);
                try {
                    // Загружаем данные и отправляем в очередь на обработку
                    final var result = this.incomeTopicsLoader.processAllTopics(this.incomeTopicsConfiguration);
                    for (var descriptor : result.keySet()) {
                        final var count = result.get(descriptor);
                        if (count > 1) {
                            log.debug("Loaded from {} {} records", descriptor.getName(), count);
                            event.setImmediateRunNextIteration(true);
                            break;
                        }
                    }
                } catch (Exception e) {
                    internalTreatmentExceptionOnDataRead(event, e);
                }
            } finally {
                this.connectionsContainer.putCurrent(null);
            }

        } catch (Exception e) {
            internalTreatmentExceptionOnDataRead(event, e);
        } finally {
            log.debug("Finished iterationExecute()");
        }
    }

    /**
     * Обработка ошибки при выполнении итерации.
     *
     * @param event Объект-событие с параметрами итерации.
     * @param e     Ошибка, которую требуется обработать.
     */
    private void internalTreatmentExceptionOnDataRead(SimpleWorkerOnIterationExecuteEvent event, Exception e) {
        log.error("", e);
        if (e instanceof InterruptedException) {
            log.info("event.setStopExecution(true)");
            event.setStopExecution(true);
        } else {
            log.info("event.setNeedRestart(true)");
            event.setNeedRestart(true);
        }
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Обработка событий о чтении данных из Kafka">

    /**
     * Обработка события о загрузке из Kafka набора объектов {@link ru.gx.fin.gate.quik.provider.out.QuikSecurity}.
     *
     * @param event Объект-событие с параметрами.
     */
    @SneakyThrows(SQLException.class)
    @EventListener(LoadedSecuritiesEvent.class)
    public void loadedSecurities(LoadedSecuritiesEvent event) {
        if (event.getData() == null) {
            return;
        }
        log.debug("Starting loadedSecurities()");
        try {
            try (var connection = getDataSource().getConnection()) {
                this.connectionsContainer.putCurrent(connection);
                this.saveData(callSaveSecurities, event, settings.getIncomeTopicSecurities());
            } finally {
                this.connectionsContainer.putCurrent(null);
            }
        } finally {
            log.debug("Finished loadedSecurities()");
        }
    }

    /**
     * Обработка события о загрузке из Kafka набора объектов {@link ru.gx.fin.gate.quik.provider.out.QuikDeal}.
     *
     * @param event Объект-событие с параметрами.
     */
    @SneakyThrows(SQLException.class)
    @EventListener(LoadedDealsEvent.class)
    public void loadedDeals(LoadedDealsEvent event) {
        if (event.getData() == null) {
            return;
        }
        log.debug("Starting loadedDeals()");
        try {
            try (var connection = getDataSource().getConnection()) {
                this.connectionsContainer.putCurrent(connection);
                this.saveData(callSaveDeals, event, settings.getIncomeTopicDeals());
            } finally {
                this.connectionsContainer.putCurrent(null);
            }
        } finally {
            log.debug("Finished loadedDeals()");
        }
    }

    /**
     * Обработка события о загрузке из Kafka набора объектов {@link ru.gx.fin.gate.quik.provider.out.QuikOrder}.
     *
     * @param event Объект-событие с параметрами.
     */
    @SneakyThrows(SQLException.class)
    @EventListener(LoadedOrdersEvent.class)
    public void loadedOrders(LoadedOrdersEvent event) {
        if (event.getData() == null) {
            return;
        }
        log.debug("Starting loadedOrders()");
        try {
            try (var connection = getDataSource().getConnection()) {
                this.connectionsContainer.putCurrent(connection);
                this.saveData(callSaveOrders, event, settings.getIncomeTopicOrders());
            } finally {
                this.connectionsContainer.putCurrent(null);
            }
        } finally {
            log.debug("Finished loadedOrders()");
        }
    }

    /**
     * Обработка события о загрузке из Kafka набора объектов {@link ru.gx.fin.gate.quik.provider.out.QuikAllTrade}.
     *
     * @param event Объект-событие с параметрами.
     */
    @SneakyThrows(SQLException.class)
    @EventListener(LoadedAllTradesEvent.class)
    public void loadedAllTrades(LoadedAllTradesEvent event) {
        if (event.getData() == null) {
            return;
        }
        log.debug("Starting loadedAllTrades()");
        try {
            try (var connection = getDataSource().getConnection()) {
                this.connectionsContainer.putCurrent(connection);
                this.saveData(callSaveAllTrades, event, settings.getIncomeTopicAllTrades());
            } finally {
                this.connectionsContainer.putCurrent(null);
            }
        } finally {
            log.debug("Finished loadedAllTrades()");
        }
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
}
