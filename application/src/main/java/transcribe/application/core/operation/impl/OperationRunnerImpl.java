package transcribe.application.core.operation.impl;

import com.vaadin.flow.component.UI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import transcribe.application.core.dialog.Dialogs;
import transcribe.application.core.operation.Operation;
import transcribe.application.core.operation.OperationRunner;
import transcribe.application.core.ui.UiUtils;
import transcribe.core.common.executor.CommonExecutor;
import transcribe.domain.operation.service.OperationService;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class OperationRunnerImpl implements OperationRunner {

    private final OperationService operationService;
    private final CommonExecutor commonExecutor;

    @Override
    public <T> void run(Operation<T> operation, UI ui) {
        var operationEntity = operationService.createRunning(
                operation.getName(),
                operation.getDescription(),
                operation.getType()
        );

        var notifier = switch (operation.getType()) {
            case NON_BLOCKING -> new NonBlockingOperationNotifier(operation.getName());
            case BLOCKING -> new BlockingOperationNotifier(operation.getName());
        };

        CompletableFuture.runAsync(() -> UiUtils.safeAccess(ui, () -> {
                    notifier.open();
                    operation.getBeforeCall().run();
                }), commonExecutor)
                .thenApply(_ -> operation.getCallable().call())
                .thenAccept(result -> {
                    UiUtils.safeAccess(ui, () -> {
                        notifier.close();
                        operation.getOnSuccess().accept(result);
                    });

                    operationService.updateSuccess(operationEntity.getId());
                    log.info("Operation [{}] success", operation.getName());
                }).exceptionally(e -> {
                    UiUtils.safeAccess(ui, () -> {
                        notifier.close();
                        operation.getOnError().accept(e);
                        Dialogs.error(e);
                    });

                    operationService.updateFailure(operationEntity.getId(), e);
                    log.error("Operation [{}] error", operation.getName(), e);
                    return null;
                }).whenComplete((_, _) -> UiUtils.safeAccess(ui, () -> {
                    notifier.close();
                    operation.getOnFinally().run();
                    log.info("Operation [{}] complete", operation.getName());
                }));

    }

}
