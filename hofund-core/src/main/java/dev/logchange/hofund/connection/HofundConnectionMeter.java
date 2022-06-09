package dev.logchange.hofund.connection;

import dev.logchange.hofund.info.HofundInfoProvider;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.MeterBinder;

import java.util.LinkedList;
import java.util.List;

public class HofundConnectionMeter implements MeterBinder {

    private static final String NAME = "hofund_connection";
    private static final String DESCRIPTION = "TODO";

    private final HofundInfoProvider infoProvider;
    private final List<HofundConnectionsProvider> connectionsProviders;

    public HofundConnectionMeter(HofundInfoProvider infoProvider, List<HofundConnectionsProvider> connectionsProviders) {
        this.infoProvider = infoProvider;
        this.connectionsProviders = connectionsProviders;
    }

    @Override
    public void bindTo(MeterRegistry meterRegistry) {
        connectionsProviders.forEach(provider -> provider.getConnections().forEach(connection -> {
            Gauge.builder(NAME, connection, con -> con.getFun().getStatus().getValue())
                    .description(DESCRIPTION)
                    .tags(tags(connection))
                    .baseUnit("status")
                    .register(meterRegistry);
        }));
    }

    private List<Tag> tags(HofundConnection connection) {
        List<Tag> tags = new LinkedList<>();
        tags.add(Tag.of("id", infoProvider.getApplicationName() + "-" + connection.getTarget() + "-" + connection.getType()));

        tags.add(Tag.of("source", infoProvider.getApplicationName()));
        tags.add(Tag.of("target", connection.getTarget()));

        tags.add(Tag.of("type", connection.getType().toString()));

        return tags;
    }
}
