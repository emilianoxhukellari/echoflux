package echoflux.application.core.operation;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import echoflux.application.core.dialog.Dialogs;
import echoflux.application.core.error.MoreErrors;
import echoflux.application.core.notification.Notifications;
import echoflux.application.core.operation.impl.BlockingOperationProgress;
import echoflux.application.core.operation.impl.NoOpOperationProgress;
import echoflux.application.core.operation.impl.NonBlockingOperationProgress;
import echoflux.application.core.ui.UiUtils;
import echoflux.core.core.executor.MoreExecutors;
import echoflux.core.core.tuple.Tuple2;
import echoflux.core.core.validate.guard.Guard;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
public final class OperationRunner {

    private static final ExecutorService EXECUTOR_SERVICE = MoreExecutors.delegatingSecurityVirtualThreadExecutor();

    public static void run(String name, Runnable runnable, Runnable onSuccess) {
        Guard.notNull(onSuccess, "onSuccess");

        run(name, OperationCallable.ofRunnable(runnable), _ -> onSuccess.run());
    }

    public static <T> void run(String name, OperationCallable<T> callable, Consumer<T> onSuccess) {
        var operation = Operation.<T>builder()
                .name(name)
                .callable(callable)
                .onSuccess(onSuccess)
                .build();

        run(operation);
    }

    public static <T> void run(Operation<T> operation) {
        run(operation, UI.getCurrent());
    }

    public static <T> void run(Operation<T> operation, UI ui) {
        Guard.notNull(operation, "operation");

        var progress = !operation.onProgressNotify()
                ? new NoOpOperationProgress()
                : switch (operation.type()) {
            case BACKGROUND -> new NonBlockingOperationProgress(operation.name());
            case BLOCKING -> new BlockingOperationProgress(operation.name());
        };

        CompletableFuture.runAsync(
                        () -> UiUtils.safeAccess(ui, () -> {
                            progress.open();
                            operation.beforeCall().run();
                        }), EXECUTOR_SERVICE
                )
                .thenApplyAsync(
                        _ -> operation.callable().call(), EXECUTOR_SERVICE
                )
                .thenAcceptAsync(
                        result -> UiUtils.safeAccess(ui, () -> {
                                    progress.close();
                                    operation.onSuccess().accept(result);
                                    if (operation.onSuccessNotify()) {
                                        var message = StringUtils.defaultIfBlank(
                                                operation.customSuccessMessage(),
                                                String.format("Completed: \"%s\"", operation.name())
                                        );
                                        var notificationConfig = switch (operation.successImportance()) {
                                            case HIGH -> Tuple2.of(Notification.Position.TOP_CENTER, 5000);
                                            case NORMAL -> Tuple2.of(Notification.Position.BOTTOM_END, 3000);
                                            case LOW -> Tuple2.of(Notification.Position.BOTTOM_END, 1500);
                                        };
                                        Notifications.success(message, notificationConfig.t1(), notificationConfig.t2());
                                    }
                                }
                        ), EXECUTOR_SERVICE
                )
                .orTimeout(
                        operation.timeout().toMillis(), TimeUnit.MILLISECONDS
                )
                .exceptionallyAsync(
                        e -> {
                            if (operation.onErrorLog()) {
                                log.error("Operation failed: {}", operation.name(), e);
                            }
                            UiUtils.safeAccess(ui, () -> {
                                progress.close();
                                operation.onError().accept(e);
                                if (operation.onErrorNotify()) {
                                    if (OperationErrorImportance.HIGH.equals(operation.errorImportance())) {
                                        if (StringUtils.isNotBlank(operation.customErrorMessage())) {
                                            Dialogs.error(operation.customErrorMessage());
                                        } else {
                                            Dialogs.error(e);
                                        }
                                    } else {
                                        var message = StringUtils.defaultIfBlank(
                                                operation.customErrorMessage(),
                                                MoreErrors.resolveErrorMessage(e)
                                        );
                                        var notificationConfig = switch (operation.errorImportance()) {
                                            case NORMAL -> Tuple2.of(Notification.Position.TOP_END, 5000);
                                            case LOW -> Tuple2.of(Notification.Position.TOP_END, 3000);
                                            default ->
                                                    throw new IllegalStateException("Unexpected error importance [%s]".formatted(operation.errorImportance()));
                                        };
                                        Notifications.error(message, notificationConfig.t1(), notificationConfig.t2());
                                    }
                                }
                            });

                            return null;
                        }, EXECUTOR_SERVICE
                )
                .whenCompleteAsync(
                        (_, _) -> UiUtils.safeAccess(ui, () -> {
                            progress.close();
                            operation.onFinally().run();
                        }), EXECUTOR_SERVICE
                );
    }

}
