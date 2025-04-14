package transcribe.application.core.operation.impl;

import com.vaadin.flow.component.UI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import transcribe.application.core.dialog.TsDialogs;
import transcribe.application.core.error.MoreErrors;
import transcribe.application.core.notification.Notifications;
import transcribe.application.core.operation.Operation;
import transcribe.application.core.operation.OperationErrorImportance;
import transcribe.application.core.operation.OperationRunner;
import transcribe.application.core.ui.UiUtils;
import transcribe.application.security.AuthenticatedUser;
import transcribe.core.core.executor.MoreExecutors;
import transcribe.domain.operation.service.OperationService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class OperationRunnerImpl implements OperationRunner {

    private final OperationService operationService;
    private final AuthenticatedUser authenticatedUser;

    @Override
    public <T> void run(Operation<T> operation, UI ui) {
        var op = operationService.createRunning(
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
                }), MoreExecutors.delegatingSecurityVirtualThreadExecutor())
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

                    operationService.updateSuccess(op.id());
                })
                .orTimeout(operation.getTimeout().toMillis(), TimeUnit.MILLISECONDS)
                .exceptionally(e -> {
                    if (operation.isOnErrorLog()) {
                        log.error("Operation failed: {}", operation.getName(), e);
                    }
                    UiUtils.safeAccess(ui, () -> {
                        progress.close();
                        operation.getOnError().accept(e);
                        if (operation.isOnErrorNotify()) {
                            if (OperationErrorImportance.HIGH.equals(operation.getErrorImportance())) {
                                if (StringUtils.isNotBlank(operation.getCustomErrorMessage())) {
                                    TsDialogs.error(operation.getCustomErrorMessage());
                                } else {
                                    TsDialogs.error(e, authenticatedUser);
                                }
                            } else {
                                var message = StringUtils.defaultIfBlank(
                                        operation.getCustomErrorMessage(),
                                        MoreErrors.resolveErrorMessage(e, authenticatedUser)
                                );
                                Notifications.error(
                                        message,
                                        operation.getErrorImportance().getNotificationPosition(),
                                        operation.getErrorImportance().getDurationMillis()
                                );
                            }
                        }
                    });

                    operationService.updateFailure(op.id(), e);
                    return null;
                })
                .whenComplete((_, _) -> UiUtils.safeAccess(ui, () -> {
                    progress.close();
                    operation.getOnFinally().run();
                }));
    }

}
