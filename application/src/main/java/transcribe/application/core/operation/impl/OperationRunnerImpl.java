package transcribe.application.core.operation.impl;

import com.vaadin.flow.component.UI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import transcribe.application.core.notification.Notifications;
import transcribe.application.core.operation.Operation;
import transcribe.application.core.operation.OperationNotifier;
import transcribe.application.core.operation.OperationRunner;
import transcribe.core.common.executor.CommonExecutor;
import transcribe.domain.operation.service.OperationService;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public final class OperationRunnerImpl implements OperationRunner {

    private final OperationService operationService;
    private final CommonExecutor commonExecutor;

    @Override
    public <T> void run(Operation<T> operation) {
        var notifier = new OperationNotifier(operation.getName(), operation.getType());
        notifier.open();

        var operationEntity = operationService.createRunning(
                operation.getName(),
                operation.getDescription(),
                operation.getType()
        );

        var ui = UI.getCurrent();

        CompletableFuture.supplyAsync(operation.getCallable()::call, commonExecutor)
                .thenAccept(result -> {
                    ui.access(() -> {
                        notifier.close();
                        operation.getOnSuccess().accept(result);
                    });

                    operationService.updateSuccess(operationEntity.getId());
                    log.info("Operation [{}] success", operation.getName());
                }).exceptionally(e -> {
                    ui.access(() -> {
                        notifier.close();
                        operation.getOnError().accept(e);
                        Notifications.showError(e);
                    });

                    operationService.updateFailure(operationEntity.getId(), e);
                    log.error("Operation [{}] error", operation.getName(), e);
                    return null;
                }).whenComplete((_, _) -> ui.access(() -> {
                    notifier.close();
                    operation.getOnFinally().run();
                    log.info("Operation [{}] complete", operation.getName());
                }));

    }

}
