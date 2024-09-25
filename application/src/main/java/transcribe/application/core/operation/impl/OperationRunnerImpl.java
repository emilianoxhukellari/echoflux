package transcribe.application.core.operation.impl;

import com.vaadin.flow.component.UI;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import transcribe.application.core.dialog.Dialogs;
import transcribe.application.core.notification.Notifications;
import transcribe.application.core.operation.Operation;
import transcribe.application.core.operation.OperationRunner;
import transcribe.application.core.ui.UiUtils;
import transcribe.core.core.executor.VirtualThreadExecutor;
import transcribe.domain.operation.service.OperationService;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class OperationRunnerImpl implements OperationRunner {

    private final OperationService operationService;
    private final VirtualThreadExecutor virtualThreadExecutor;

    @Override
    public <T> void run(Operation<T> operation, UI ui) {
        var operationEntity = operationService.createRunning(
                operation.getName(),
                operation.getDescription(),
                operation.getType()
        );

        var progress = !operation.isOnProgressNotify()
                ? new NoOpOperationProgress()
                : switch (operation.getType()) {
            case NON_BLOCKING -> new NonBlockingOperationProgress(operation.getName());
            case BLOCKING -> new BlockingOperationProgress(operation.getName());
        };

        CompletableFuture.runAsync(() -> UiUtils.safeAccess(ui, () -> {
                    progress.open();
                    operation.getBeforeCall().run();
                }), virtualThreadExecutor)
                .thenApply(_ -> operation.getCallable().call())
                .thenAccept(result -> {
                    UiUtils.safeAccess(ui, () -> {
                        progress.close();
                        operation.getOnSuccess().accept(result);
                        if (operation.isOnSuccessNotify()) {
                            var message = StringUtils.defaultIfBlank(
                                    operation.getCustomSuccessMessage(),
                                    String.format("Completed: \"%s\"", operation.getName())
                            );
                            Notifications.success(
                                    message,
                                    operation.getSuccessImportance().getNotificationPosition(),
                                    operation.getSuccessImportance().getDurationMillis()
                            );
                        }
                    });

                    operationService.updateSuccess(operationEntity.getId());
                }).exceptionally(e -> {
                    UiUtils.safeAccess(ui, () -> {
                        progress.close();
                        operation.getOnError().accept(e);
                        if (operation.isOnErrorNotify()) {
                            Dialogs.error(e);
                        }
                    });

                    operationService.updateFailure(operationEntity.getId(), e);
                    return null;
                }).whenComplete((_, _) -> UiUtils.safeAccess(ui, () -> {
                    progress.close();
                    operation.getOnFinally().run();
                }));

    }

}
